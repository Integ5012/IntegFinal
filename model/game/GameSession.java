package com.wordy.server.model.game;

import com.wordy.grpc.GameEvent;
import com.wordy.grpc.SubmitWordResponse;
import com.wordy.server.model.entity.TimeConfig;
import com.wordy.server.model.repository.LeaderboardRepository;
import com.wordy.server.model.repository.PlayerRepository;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameSession {

    public static final int WINS_TO_WIN = 3;
    public static final String EVENT_START = "START";
    public static final String EVENT_ROUND = "ROUND";
    public static final String EVENT_RESULT = "RESULT";
    public static final String EVENT_END = "END";
    public static final String EVENT_PLAYER_JOINED = "PLAYER_JOINED";

    private final CopyOnWriteArrayList<GamePlayer> players;
    private final WordDictionary dictionary;
    private final LetterGenerator letterGenerator;
    private final int roundDurationSeconds;
    private final int pauseBetweenRoundsSeconds;
    private final Runnable onFinished;
    private final PlayerRepository playerRepository;
    private final LeaderboardRepository leaderboardRepository;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "game-session");
                thread.setDaemon(true);
                return thread;
            });

    private final Map<String, String> roundSubmissions = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean roundActive = new AtomicBoolean(false);
    private final AtomicBoolean finished = new AtomicBoolean(false);

    private volatile int currentRound;
    private volatile int currentTimeLeft;
    private volatile List<String> currentLetters = List.of();
    private volatile ScheduledFuture<?> roundEndTask;

    public GameSession(
            List<GamePlayer> players,
            WordDictionary dictionary,
            LetterGenerator letterGenerator,
            TimeConfig timeConfig,
            Runnable onFinished,
            PlayerRepository playerRepository,
            LeaderboardRepository leaderboardRepository
    ) {
        this.players = new CopyOnWriteArrayList<>(players);
        this.dictionary = dictionary;
        this.letterGenerator = letterGenerator;
        this.roundDurationSeconds = timeConfig.getRoundDuration();
        this.pauseBetweenRoundsSeconds = 2;
        this.onFinished = onFinished;
        this.playerRepository = playerRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        broadcast(buildEvent(EVENT_START, 0, List.of(), "", "", roundDurationSeconds, Map.of()));
        scheduler.schedule(this::beginRound, 1, TimeUnit.SECONDS);
    }

    public SubmitWordResponse submitWord(String username, String word) {
        if (!running.get() || !roundActive.get()) {
            return SubmitWordResponse.newBuilder()
                    .setValid(false)
                    .setMessage("No active round")
                    .build();
        }

        if (!isParticipant(username)) {
            return SubmitWordResponse.newBuilder()
                    .setValid(false)
                    .setMessage("You are not in this game")
                    .build();
        }

        WordDictionary.ValidationResult validation = dictionary.validate(word, currentLetters);
        if (!validation.valid()) {
            return SubmitWordResponse.newBuilder()
                    .setValid(false)
                    .setMessage(validation.message())
                    .build();
        }

        String accepted = validation.normalizedWord();
        String previous = roundSubmissions.get(username);
        if (previous != null && accepted.length() <= previous.length()) {
            return SubmitWordResponse.newBuilder()
                    .setValid(true)
                    .setMessage("Word accepted, but your longer submission (\""
                            + previous + "\") still counts for this round")
                    .build();
        }
        roundSubmissions.put(username, accepted);
        return SubmitWordResponse.newBuilder()
                .setValid(true)
                .setMessage("Word accepted")
                .build();
    }

    public boolean hasPlayer(String username) {
        return isParticipant(username);
    }

    public void disconnectPlayer(String username) {
        for (GamePlayer player : players) {
            if (player.getUsername().equals(username)) {
                try {
                    player.getEventObserver().onError(io.grpc.Status.UNAUTHENTICATED
                            .withDescription("Logged in from another location")
                            .asRuntimeException());
                } catch (RuntimeException ignored) {
                    // Client already disconnected.
                }
            }
        }
    }

    public List<String> getPlayerUsernames() {
        return players.stream().map(GamePlayer::getUsername).toList();
    }

    public boolean hasConnectedPlayers() {
        return players.stream().anyMatch(GamePlayer::isConnected);
    }

    public synchronized void onPlayerDisconnected(String username) {
        GamePlayer player = findPlayer(username);
        if (player != null) {
            player.setConnected(false);
        }
        long connected = players.stream().filter(GamePlayer::isConnected).count();
        if (connected < 2) {
            abandon("Not enough players remaining. Game ended.");
        } else {
            broadcast(buildEvent(
                    EVENT_PLAYER_JOINED,
                    currentRound,
                    currentLetters,
                    username,
                    "left the game",
                    currentTimeLeft,
                    Map.of()
            ));
        }
    }

    public synchronized boolean addPlayer(GamePlayer player) {
        if (!running.get() || finished.get()) {
            return false;
        }
        if (isParticipant(player.getUsername())) {
            return false;
        }
        players.add(player);

        sendToPlayer(player, buildEvent(
                EVENT_PLAYER_JOINED,
                currentRound,
                currentLetters,
                player.getUsername(),
                buildScoreboardSummary(),
                currentTimeLeft,
                Map.of()
        ));

        if (roundActive.get() && !currentLetters.isEmpty()) {
            sendToPlayer(player, buildEvent(
                    EVENT_ROUND,
                    currentRound,
                    currentLetters,
                    "",
                    "",
                    currentTimeLeft,
                    snapshotSubmissions()
            ));
        }

        broadcast(buildEvent(
                EVENT_PLAYER_JOINED,
                currentRound,
                currentLetters,
                player.getUsername(),
                "joined the game",
                currentTimeLeft,
                Map.of()
        ));
        return true;
    }

    private String buildScoreboardSummary() {
        StringBuilder builder = new StringBuilder("Round wins — ");
        for (int i = 0; i < players.size(); i++) {
            GamePlayer p = players.get(i);
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(p.getUsername()).append(": ").append(p.getRoundWins());
        }
        return builder.toString();
    }

    private static void sendToPlayer(GamePlayer player, GameEvent event) {
        if (!player.isConnected()) {
            return;
        }
        try {
            player.getEventObserver().onNext(event);
        } catch (RuntimeException ex) {
            player.setConnected(false);
        }
    }

    private void beginRound() {
        if (!running.get()) {
            return;
        }

        currentRound++;
        roundSubmissions.clear();
        currentLetters = letterGenerator.generate();
        roundActive.set(true);
        currentTimeLeft = roundDurationSeconds;

        broadcast(buildEvent(
                EVENT_ROUND,
                currentRound,
                currentLetters,
                "",
                "",
                roundDurationSeconds,
                Map.of()
        ));

        for (int elapsed = 1; elapsed < roundDurationSeconds; elapsed++) {
            final int timeLeft = roundDurationSeconds - elapsed;
            scheduler.schedule(() -> {
                if (running.get() && roundActive.get()) {
                    currentTimeLeft = timeLeft;
                    broadcast(buildEvent(
                            EVENT_ROUND,
                            currentRound,
                            currentLetters,
                            "",
                            "",
                            timeLeft,
                            snapshotSubmissions()
                    ));
                }
            }, elapsed, TimeUnit.SECONDS);
        }

        roundEndTask = scheduler.schedule(this::endRound, roundDurationSeconds, TimeUnit.SECONDS);
    }

    private void endRound() {
        if (!running.get()) {
            return;
        }

        roundActive.set(false);
        RoundOutcome outcome = resolveRoundWinner();

        broadcast(buildEvent(
                EVENT_RESULT,
                currentRound,
                currentLetters,
                outcome.winnerUsername() == null ? "" : outcome.winnerUsername(),
                outcome.bestWord() == null ? "" : outcome.bestWord(),
                0,
                outcome.playerWords()
        ));

        recordRoundWords(outcome.playerWords());

        if (outcome.hasWinner()) {
            GamePlayer roundWinner = findPlayer(outcome.winnerUsername());
            if (roundWinner != null) {
                roundWinner.addRoundWin();
                if (roundWinner.hasReachedWins(WINS_TO_WIN)) {
                    finishGame(outcome.winnerUsername());
                    return;
                }
            }
        }

        scheduler.schedule(this::beginRound, pauseBetweenRoundsSeconds, TimeUnit.SECONDS);
    }

    private void recordRoundWords(Map<String, String> submissions) {
        for (Map.Entry<String, String> entry : submissions.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isBlank()) {
                leaderboardRepository.insertWord(entry.getKey(), entry.getValue());
            }
        }
    }

    private void finishGame(String overallWinner) {
        if (!finished.compareAndSet(false, true)) {
            return;
        }
        running.set(false);
        roundActive.set(false);
        cancelRoundTimer();
        scheduler.shutdown();

        playerRepository.incrementWins(overallWinner);

        Map<String, String> endSummary = new HashMap<>();
        for (GamePlayer player : players) {
            endSummary.put(player.getUsername(), String.valueOf(player.getRoundWins()));
        }

        broadcast(buildEvent(
                EVENT_END,
                currentRound,
                currentLetters,
                overallWinner,
                submissionsFor(overallWinner),
                0,
                endSummary
        ));

        for (GamePlayer player : players) {
            completeObserver(player.getEventObserver());
        }

        onFinished.run();
    }

    public void abandon(String reason) {
        if (!finished.compareAndSet(false, true)) {
            return;
        }
        running.set(false);
        roundActive.set(false);
        cancelRoundTimer();
        scheduler.shutdownNow();

        broadcast(buildEvent(
                EVENT_END,
                currentRound,
                currentLetters,
                "",
                reason,
                0,
                snapshotSubmissions()
        ));

        for (GamePlayer player : players) {
            if (player.isConnected()) {
                completeObserver(player.getEventObserver());
            }
        }
        onFinished.run();
    }

    private void cancelRoundTimer() {
        if (roundEndTask != null) {
            roundEndTask.cancel(false);
            roundEndTask = null;
        }
    }

    RoundOutcome resolveRoundWinner() {
        return resolveRoundWinnerFrom(snapshotSubmissions());
    }

    RoundOutcome resolveRoundWinnerFrom(Map<String, String> submissions) {
        if (submissions.isEmpty()) {
            return new RoundOutcome(null, "", submissions);
        }

        int maxLength = submissions.values().stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);

        List<String> leaders = new ArrayList<>();
        String bestWord = "";

        for (Map.Entry<String, String> entry : submissions.entrySet()) {
            if (entry.getValue().length() == maxLength) {
                leaders.add(entry.getKey());
                bestWord = entry.getValue();
            }
        }

        if (leaders.size() != 1) {
            return new RoundOutcome(null, "", submissions);
        }

        return new RoundOutcome(leaders.get(0), bestWord, submissions);
    }

    private String submissionsFor(String username) {
        return roundSubmissions.getOrDefault(username, "");
    }

    private Map<String, String> snapshotSubmissions() {
        return Collections.unmodifiableMap(new HashMap<>(roundSubmissions));
    }

    private GamePlayer findPlayer(String username) {
        for (GamePlayer player : players) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    private boolean isParticipant(String username) {
        return findPlayer(username) != null;
    }

    private void broadcast(GameEvent event) {
        for (GamePlayer player : players) {
            if (!player.isConnected()) {
                continue;
            }
            try {
                player.getEventObserver().onNext(event);
            } catch (RuntimeException ex) {
                player.setConnected(false);
            }
        }
    }

    private static GameEvent buildEvent(
            String eventType,
            int round,
            List<String> letters,
            String winner,
            String bestWord,
            int timeLeft,
            Map<String, String> playerWords
    ) {
        return GameEvent.newBuilder()
                .setEventType(eventType)
                .setRound(round)
                .addAllLetters(letters)
                .setWinner(winner == null ? "" : winner)
                .setBestWord(bestWord == null ? "" : bestWord)
                .setTimeLeft(timeLeft)
                .putAllPlayerWords(playerWords)
                .build();
    }

    private static void completeObserver(StreamObserver<GameEvent> observer) {
        observer.onCompleted();
    }
}

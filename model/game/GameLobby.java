package com.wordy.server.model.game;

import com.wordy.grpc.GameEvent;
import com.wordy.server.model.entity.TimeConfig;
import com.wordy.server.model.repository.GameConfigRepository;
import com.wordy.server.model.repository.LeaderboardRepository;
import com.wordy.server.model.repository.PlayerRepository;
import com.wordy.server.model.session.SessionRegistry;
import com.wordy.server.model.session.SessionRevocationListener;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameLobby implements SessionRevocationListener {

    private static final int MIN_PLAYERS = 2;

    private final WordDictionary dictionary;
    private final LetterGenerator letterGenerator;
    private final GameConfigRepository configRepository;
    private final SessionRegistry sessionRegistry;
    private final PlayerRepository playerRepository;
    private final LeaderboardRepository leaderboardRepository;

    private final List<QueuedPlayer> waitingPlayers = new ArrayList<>();
    private final ScheduledExecutorService lobbyScheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "game-lobby");
                thread.setDaemon(true);
                return thread;
            });

    private volatile GameSession activeSession;
    private volatile ScheduledFuture<?> waitTimeoutTask;

    public GameLobby(
            WordDictionary dictionary,
            LetterGenerator letterGenerator,
            GameConfigRepository configRepository,
            SessionRegistry sessionRegistry,
            PlayerRepository playerRepository,
            LeaderboardRepository leaderboardRepository
    ) {
        this.dictionary = dictionary;
        this.letterGenerator = letterGenerator;
        this.configRepository = configRepository;
        this.sessionRegistry = sessionRegistry;
        this.playerRepository = playerRepository;
        this.leaderboardRepository = leaderboardRepository;
        sessionRegistry.addRevocationListener(this);
    }

    public synchronized String join(String username, String sessionId, StreamObserver<GameEvent> observer) {
        if (!sessionRegistry.isCurrentSession(sessionId)) {
            return "Session expired or replaced by a new login";
        }

        if (activeSession != null && activeSession.hasPlayer(username)) {
            return "Already in an active game";
        }

        if (sessionRegistry.isInActiveGame(username)) {
            return "Account already has an active game session";
        }

        if (!sessionRegistry.tryEnterGame(username)) {
            return "Account already has an active game session";
        }

        waitingPlayers.add(new QueuedPlayer(username, sessionId, observer));
        notifyWaiting(observer, "Waiting for another player to join...");

        if (waitingPlayers.size() == 1) {
            scheduleWaitTimeout();
        } else if (waitingPlayers.size() >= MIN_PLAYERS) {
            cancelWaitTimeout();
            startGameFromQueue();
        }

        return "Joined queue";
    }

    public GameSession getActiveSession() {
        return activeSession;
    }

    @Override
    public synchronized void onSessionRevoked(String username, String revokedSessionId) {
        List<QueuedPlayer> removed = new ArrayList<>();
        for (QueuedPlayer queued : waitingPlayers) {
            if (queued.username().equals(username)) {
                removed.add(queued);
            }
        }
        waitingPlayers.removeAll(removed);

        for (QueuedPlayer queued : removed) {
            sessionRegistry.leaveGame(username);
            disconnectObserver(queued.observer(), "Logged in from another location");
        }

        if (activeSession != null && activeSession.hasPlayer(username)) {
            activeSession.disconnectPlayer(username);
            sessionRegistry.leaveGame(username);
        }

        if (waitingPlayers.isEmpty()) {
            cancelWaitTimeout();
        }
    }

    private void scheduleWaitTimeout() {
        cancelWaitTimeout();
        int waitSeconds = Math.max(1, configRepository.getConfig().getWaitingTime());
        waitTimeoutTask = lobbyScheduler.schedule(this::onWaitTimeout, waitSeconds, TimeUnit.SECONDS);
    }

    private synchronized void onWaitTimeout() {
        waitTimeoutTask = null;
        if (activeSession != null || waitingPlayers.size() >= MIN_PLAYERS) {
            return;
        }

        int waitSeconds = configRepository.getConfig().getWaitingTime();
        String message = "No other player joined within " + waitSeconds + " seconds";
        List<QueuedPlayer> expired = drainWaitingQueue();
        for (QueuedPlayer player : expired) {
            sessionRegistry.leaveGame(player.username());
            disconnectObserver(player.observer(), message);
        }
    }

    private synchronized void startGameFromQueue() {
        if (activeSession != null || waitingPlayers.size() < MIN_PLAYERS) {
            return;
        }

        List<QueuedPlayer> matchedPlayers = drainWaitingQueue();
        List<GamePlayer> players = matchedPlayers.stream()
                .map(q -> new GamePlayer(q.username(), q.sessionId(), q.observer()))
                .toList();

        TimeConfig config = configRepository.getConfig();
        activeSession = new GameSession(
                players,
                dictionary,
                letterGenerator,
                config,
                this::clearActiveSession,
                playerRepository,
                leaderboardRepository
        );
        activeSession.start();
    }

    private synchronized void clearActiveSession() {
        if (activeSession != null) {
            for (String username : activeSession.getPlayerUsernames()) {
                sessionRegistry.leaveGame(username);
            }
        }
        activeSession = null;

        if (!waitingPlayers.isEmpty()) {
            if (waitingPlayers.size() == 1) {
                scheduleWaitTimeout();
            } else {
                startGameFromQueue();
            }
        }
    }

    private synchronized List<QueuedPlayer> drainWaitingQueue() {
        List<QueuedPlayer> copy = new ArrayList<>(waitingPlayers);
        waitingPlayers.clear();
        return copy;
    }

    private void cancelWaitTimeout() {
        if (waitTimeoutTask != null) {
            waitTimeoutTask.cancel(false);
            waitTimeoutTask = null;
        }
    }

    private static void notifyWaiting(StreamObserver<GameEvent> observer, String message) {
        try {
            observer.onNext(GameEvent.newBuilder()
                    .setEventType("WAITING")
                    .setWinner(message)
                    .build());
        } catch (RuntimeException ignored) {
            // Client disconnected.
        }
    }

    private static void disconnectObserver(StreamObserver<GameEvent> observer, String message) {
        observer.onError(Status.FAILED_PRECONDITION.withDescription(message).asRuntimeException());
    }

    private record QueuedPlayer(String username, String sessionId, StreamObserver<GameEvent> observer) {
    }
}

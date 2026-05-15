package com.wordy.server.game;

import com.wordy.grpc.GameEvent;
import com.wordy.server.database.TimeConfig;
import com.wordy.server.service.SessionRegistry;
import com.wordy.server.service.SessionRevocationListener;
import com.wordy.server.store.InMemoryConfigStore;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameLobby implements SessionRevocationListener {

    private static final int MIN_PLAYERS = 2;

    private final ConcurrentLinkedQueue<QueuedPlayer> waitingPlayers = new ConcurrentLinkedQueue<>();
    private final WordDictionary dictionary;
    private final LetterGenerator letterGenerator;
    private final InMemoryConfigStore configStore;
    private final SessionRegistry sessionRegistry;

    private volatile GameSession activeSession;

    public GameLobby(
            WordDictionary dictionary,
            LetterGenerator letterGenerator,
            InMemoryConfigStore configStore,
            SessionRegistry sessionRegistry
    ) {
        this.dictionary = dictionary;
        this.letterGenerator = letterGenerator;
        this.configStore = configStore;
        this.sessionRegistry = sessionRegistry;
        sessionRegistry.addRevocationListener(this);
    }

    public synchronized String join(String username, String sessionId, StreamObserver<GameEvent> observer) {
        if (!sessionRegistry.isCurrentSession(sessionId)) {
            return "Session expired or replaced by a new login";
        }

        if (activeSession != null && activeSession.hasPlayer(username)) {
            return "Already in an active game";
        }

        if (!sessionRegistry.tryEnterGame(username)) {
            return "Account already has an active game session";
        }

        waitingPlayers.add(new QueuedPlayer(username, sessionId, observer));
        tryStartGame();
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
            disconnectObserver(queued.observer());
        }

        if (activeSession != null && activeSession.hasPlayer(username)) {
            activeSession.disconnectPlayer(username);
            sessionRegistry.leaveGame(username);
        }
    }

    private void tryStartGame() {
        if (activeSession != null) {
            return;
        }

        if (waitingPlayers.size() < MIN_PLAYERS) {
            return;
        }

        List<QueuedPlayer> matchedPlayers = new ArrayList<>();
        QueuedPlayer next;
        while ((next = waitingPlayers.poll()) != null) {
            matchedPlayers.add(next);
        }

        if (matchedPlayers.size() < MIN_PLAYERS) {
            waitingPlayers.addAll(matchedPlayers);
            for (QueuedPlayer player : matchedPlayers) {
                sessionRegistry.leaveGame(player.username());
            }
            return;
        }

        List<GamePlayer> players = matchedPlayers.stream()
                .map(q -> new GamePlayer(q.username(), q.sessionId(), q.observer()))
                .toList();

        TimeConfig config = configStore.getConfig();
        activeSession = new GameSession(
                players,
                dictionary,
                letterGenerator,
                config,
                this::clearActiveSession
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
        tryStartGame();
    }

    private static void disconnectObserver(StreamObserver<GameEvent> observer) {
        observer.onError(Status.UNAUTHENTICATED
                .withDescription("Logged in from another location")
                .asRuntimeException());
    }

    private record QueuedPlayer(String username, String sessionId, StreamObserver<GameEvent> observer) {
    }
}

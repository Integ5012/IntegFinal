package com.wordy.server.game;

import io.grpc.stub.StreamObserver;
import com.wordy.grpc.GameEvent;

public class GamePlayer {

    private final String username;
    private final String sessionId;
    private final StreamObserver<GameEvent> eventObserver;
    private int roundWins;

    public GamePlayer(String username, String sessionId, StreamObserver<GameEvent> eventObserver) {
        this.username = username;
        this.sessionId = sessionId;
        this.eventObserver = eventObserver;
        this.roundWins = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public StreamObserver<GameEvent> getEventObserver() {
        return eventObserver;
    }

    public int getRoundWins() {
        return roundWins;
    }

    public void addRoundWin() {
        roundWins++;
    }

    public boolean hasReachedWins(int targetWins) {
        return roundWins >= targetWins;
    }
}

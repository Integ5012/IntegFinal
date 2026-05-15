package com.wordy.server.store;

import com.wordy.server.database.TimeConfig;

/**
 * In-memory game timing configuration (defaults: 10s wait, 30s round).
 */
public class InMemoryConfigStore {

    private static final InMemoryConfigStore INSTANCE = new InMemoryConfigStore();

    private volatile int waitingTimeSeconds = 10;
    private volatile int roundDurationSeconds = 30;

    public static InMemoryConfigStore getInstance() {
        return INSTANCE;
    }

    public TimeConfig getConfig() {
        return new TimeConfig(waitingTimeSeconds, roundDurationSeconds);
    }

    public boolean setConfig(int waitingTimeSeconds, int roundDurationSeconds) {
        if (waitingTimeSeconds <= 0 || roundDurationSeconds <= 0) {
            return false;
        }
        this.waitingTimeSeconds = waitingTimeSeconds;
        this.roundDurationSeconds = roundDurationSeconds;
        return true;
    }
}

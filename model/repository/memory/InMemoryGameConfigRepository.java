package com.wordy.server.model.repository.memory;

import com.wordy.server.model.entity.TimeConfig;
import com.wordy.server.model.repository.GameConfigRepository;

public class InMemoryGameConfigRepository implements GameConfigRepository {

    private static final InMemoryGameConfigRepository INSTANCE = new InMemoryGameConfigRepository();

    private volatile int waitingTimeSeconds = 10;
    private volatile int roundDurationSeconds = 30;

    public static InMemoryGameConfigRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public TimeConfig getConfig() {
        return new TimeConfig(waitingTimeSeconds, roundDurationSeconds);
    }

    @Override
    public boolean setConfig(int waitingTimeSeconds, int roundDurationSeconds) {
        if (waitingTimeSeconds <= 0 || roundDurationSeconds <= 0) {
            return false;
        }
        this.waitingTimeSeconds = waitingTimeSeconds;
        this.roundDurationSeconds = roundDurationSeconds;
        return true;
    }
}

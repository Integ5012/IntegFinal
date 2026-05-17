package com.wordy.server.model.repository;

import com.wordy.server.model.entity.TimeConfig;

public interface GameConfigRepository {

    TimeConfig getConfig();

    boolean setConfig(int waitingTimeSeconds, int roundDurationSeconds);
}

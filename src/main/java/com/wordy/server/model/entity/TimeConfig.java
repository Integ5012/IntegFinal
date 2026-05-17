package com.wordy.server.model.entity;

public class TimeConfig {

    private final int waitingTime;
    private final int roundDuration;

    public TimeConfig(int waitingTime, int roundDuration) {
        this.waitingTime = waitingTime;
        this.roundDuration = roundDuration;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getRoundDuration() {
        return roundDuration;
    }
}

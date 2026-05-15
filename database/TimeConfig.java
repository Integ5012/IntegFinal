package com.wordy.server.database;

public class TimeConfig {
    private int waitingTime;
    private int roundDuration;

    public TimeConfig(int waitingTime, int roundDuration) {
        this.waitingTime = waitingTime;
        this.roundDuration = roundDuration;
    }

    public int getWaitingTime() { return waitingTime; }
    public int getRoundDuration() { return roundDuration; }
}

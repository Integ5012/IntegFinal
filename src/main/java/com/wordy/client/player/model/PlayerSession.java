package com.wordy.client.player.model;

/**
 * MVC model: logged-in player session state (no network I/O).
 */
public class PlayerSession {

    private String sessionId;
    private String username;

    public void applyLogin(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
    }

    public void clear() {
        this.username = null;
        this.sessionId = null;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isLoggedIn() {
        return sessionId != null && !sessionId.isBlank();
    }
}

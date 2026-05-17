package com.wordy.server.model.session;

public interface SessionRevocationListener {

    void onSessionRevoked(String username, String revokedSessionId);
}

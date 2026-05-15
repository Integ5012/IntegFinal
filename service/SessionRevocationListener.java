package com.wordy.server.service;

/**
 * Notified when a user logs in again and their previous session is revoked.
 */
public interface SessionRevocationListener {

    void onSessionRevoked(String username, String revokedSessionId);
}

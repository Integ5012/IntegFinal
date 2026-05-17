package com.wordy.server.model.session;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SessionRegistry {

    private static final SessionRegistry INSTANCE = new SessionRegistry();

    private final Map<String, String> sessionToUsername = new ConcurrentHashMap<>();
    private final Map<String, String> usernameToSession = new ConcurrentHashMap<>();
    private final Set<String> usernamesInActiveGame = ConcurrentHashMap.newKeySet();
    private final List<SessionRevocationListener> revocationListeners = new CopyOnWriteArrayList<>();

    public static SessionRegistry getInstance() {
        return INSTANCE;
    }

    SessionRegistry() {
    }

    public void addRevocationListener(SessionRevocationListener listener) {
        revocationListeners.add(listener);
    }

    public String createSession(String username) {
        String revokedSessionId = usernameToSession.get(username);
        if (revokedSessionId != null) {
            sessionToUsername.remove(revokedSessionId);
            usernameToSession.remove(username, revokedSessionId);
            usernamesInActiveGame.remove(username);
            notifyRevoked(username, revokedSessionId);
        }

        String sessionId = UUID.randomUUID().toString();
        sessionToUsername.put(sessionId, username);
        usernameToSession.put(username, sessionId);
        return sessionId;
    }

    public Optional<String> getCurrentSessionId(String username) {
        return Optional.ofNullable(usernameToSession.get(username));
    }

    public Optional<String> resolveUsername(String sessionId) {
        if (!isCurrentSession(sessionId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(sessionToUsername.get(sessionId));
    }

    public boolean isCurrentSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return false;
        }
        String username = sessionToUsername.get(sessionId);
        if (username == null) {
            return false;
        }
        return sessionId.equals(usernameToSession.get(username));
    }

    public boolean isInActiveGame(String username) {
        return usernamesInActiveGame.contains(username);
    }

    public boolean tryEnterGame(String username) {
        return usernamesInActiveGame.add(username);
    }

    public void leaveGame(String username) {
        usernamesInActiveGame.remove(username);
    }

    public void removeSession(String sessionId) {
        String username = sessionToUsername.remove(sessionId);
        if (username != null) {
            usernameToSession.remove(username, sessionId);
            usernamesInActiveGame.remove(username);
        }
    }

    public boolean isOnline(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return username.equals(usernameToSession.get(username));
    }

    public Set<String> getOnlineUsernames() {
        return new HashSet<>(usernameToSession.keySet());
    }

    private void notifyRevoked(String username, String revokedSessionId) {
        for (SessionRevocationListener listener : revocationListeners) {
            try {
                listener.onSessionRevoked(username, revokedSessionId);
            } catch (RuntimeException ignored) {
                // Keep other listeners running.
            }
        }
    }
}

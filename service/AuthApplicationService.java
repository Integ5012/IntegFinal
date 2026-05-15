package com.wordy.server.service;

import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.LoginRequest;
import com.wordy.grpc.LoginResponse;
import com.wordy.grpc.LogoutRequest;
import com.wordy.server.store.InMemoryPlayerStore;

import java.util.Objects;

public class AuthApplicationService {

    private final InMemoryPlayerStore playerStore;
    private final SessionRegistry sessionRegistry;

    public AuthApplicationService() {
        this(InMemoryPlayerStore.getInstance(), SessionRegistry.getInstance());
    }

    public AuthApplicationService(InMemoryPlayerStore playerStore, SessionRegistry sessionRegistry) {
        this.playerStore = playerStore;
        this.sessionRegistry = sessionRegistry;
    }

    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            return LoginResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Username and password are required")
                    .build();
        }

        try {
            InMemoryPlayerStore.PlayerRecord player = playerStore.getPlayerByUsername(username);
            if (player == null) {
                return LoginResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Invalid username or password")
                        .build();
            }

            if (!Objects.equals(player.password(), password)) {
                return LoginResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Invalid username or password")
                        .build();
            }

            boolean hadPreviousSession = sessionRegistry.getCurrentSessionId(username).isPresent();
            String sessionId = sessionRegistry.createSession(username);
            String role = player.role() == null ? "PLAYER" : player.role();

            String message = hadPreviousSession
                    ? "Login successful. Previous session was disconnected."
                    : "Login successful";

            return LoginResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage(message)
                    .setRole(role)
                    .setSessionId(sessionId)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return LoginResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Login error: " + e.getMessage())
                    .build();
        }
    }

    public BasicResponse logout(LogoutRequest request) {
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            return BasicResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Session id is required")
                    .build();
        }

        if (!sessionRegistry.isCurrentSession(sessionId)) {
            return BasicResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Invalid or expired session")
                    .build();
        }

        sessionRegistry.removeSession(sessionId);
        return BasicResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Logged out successfully")
                .build();
    }
}

package com.wordy.server.service;

import com.wordy.grpc.LogoutRequest;
import com.wordy.server.model.entity.Player;
import com.wordy.server.model.repository.PlayerRepository;
import com.wordy.server.model.session.SessionRegistry;
import com.wordy.server.service.dto.AuthResult;
import com.wordy.server.service.dto.OperationResult;

import java.util.Objects;

public class AuthService {

    private final PlayerRepository playerRepository;
    private final SessionRegistry sessionRegistry;

    public AuthService(PlayerRepository playerRepository, SessionRegistry sessionRegistry) {
        this.playerRepository = playerRepository;
        this.sessionRegistry = sessionRegistry;
    }

    public AuthResult login(String username, String password) {
        String trimmedUsername = username == null ? "" : username.trim();
        String trimmedPassword = password == null ? "" : password;

        if (trimmedUsername.isEmpty()) {
            return new AuthResult(false, "Username is required", null, null);
        }

        Player player = playerRepository.findByUsername(trimmedUsername);
        if (player == null) {
            return new AuthResult(false, "Invalid username or password", null, null);
        }

        String role = player.role() == null ? "PLAYER" : player.role();
        boolean playerRole = "PLAYER".equalsIgnoreCase(role);
        if (playerRole) {
            if (!trimmedPassword.isEmpty()
                    && !Objects.equals(player.password(), trimmedPassword)) {
                return new AuthResult(false, "Invalid username or password", null, null);
            }
        } else {
            if (trimmedPassword.isEmpty()
                    || !Objects.equals(player.password(), trimmedPassword)) {
                return new AuthResult(false, "Invalid username or password", null, null);
            }
        }

        // Use canonical username from storage (matches DB / in-memory record)
        String accountName = player.username();
        boolean hadPreviousSession = sessionRegistry.getCurrentSessionId(accountName).isPresent();
        String sessionId = sessionRegistry.createSession(accountName);
        String message = hadPreviousSession
                ? "Login successful. Previous session was disconnected."
                : "Login successful";

        return new AuthResult(true, message, role, sessionId);
    }

    public OperationResult logout(LogoutRequest request) {
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            return OperationResult.failure("Session id is required");
        }

        if (!sessionRegistry.isCurrentSession(sessionId)) {
            return OperationResult.failure("Invalid or expired session");
        }

        sessionRegistry.removeSession(sessionId);
        return OperationResult.success("Logged out successfully");
    }

    public OperationResult registerPlayer(String username) {
        String trimmed = username == null ? "" : username.trim();
        if (trimmed.isEmpty()) {
            return OperationResult.failure("Username is required");
        }
        if (playerRepository.findByUsername(trimmed) != null) {
            return OperationResult.failure("Username already exists");
        }
        return playerRepository.createPlayer(trimmed, "", "PLAYER")
                ? OperationResult.success("Account created. You can log in with your username.")
                : OperationResult.failure("Failed to create account");
    }
}

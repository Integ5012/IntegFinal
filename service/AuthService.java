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

        if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
            return new AuthResult(false, "Username and password are required", null, null);
        }

        Player player = playerRepository.findByUsername(trimmedUsername);
        if (player == null || !Objects.equals(player.password(), trimmedPassword)) {
            return new AuthResult(false, "Invalid username or password", null, null);
        }

        boolean hadPreviousSession = sessionRegistry.getCurrentSessionId(trimmedUsername).isPresent();
        String sessionId = sessionRegistry.createSession(trimmedUsername);
        String role = player.role() == null ? "PLAYER" : player.role();
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
}

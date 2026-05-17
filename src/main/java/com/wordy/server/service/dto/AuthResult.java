package com.wordy.server.service.dto;

public record AuthResult(boolean success, String message, String role, String sessionId) {
}

package com.wordy.server.model.entity;

public record Player(int id, String username, String password, String role, int wins) {
}

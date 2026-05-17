package com.wordy.server;

import com.wordy.server.model.repository.GameConfigRepository;
import com.wordy.server.model.repository.LeaderboardRepository;
import com.wordy.server.model.repository.PlayerRepository;
import com.wordy.server.model.repository.jdbc.DatabaseConnection;
import com.wordy.server.model.repository.jdbc.JdbcGameConfigRepository;
import com.wordy.server.model.repository.jdbc.JdbcLeaderboardRepository;
import com.wordy.server.model.repository.jdbc.JdbcPlayerRepository;
import com.wordy.server.model.repository.memory.InMemoryGameConfigRepository;
import com.wordy.server.model.repository.memory.InMemoryLeaderboardRepository;
import com.wordy.server.model.repository.memory.InMemoryPlayerRepository;

public final class RepositoryProvider {

    public record Repositories(
            PlayerRepository players,
            GameConfigRepository config,
            LeaderboardRepository leaderboard,
            boolean usingDatabase
    ) {
    }

    private RepositoryProvider() {
    }

    public static Repositories create() {
        boolean useMemory = Boolean.parseBoolean(System.getenv().getOrDefault("WORDY_USE_MEMORY", "false"));
        if (!useMemory && DatabaseConnection.isAvailable()) {
            System.out.println("Using MySQL database: " + DatabaseConnection.getUrl());
            return new Repositories(
                    new JdbcPlayerRepository(),
                    new JdbcGameConfigRepository(),
                    new JdbcLeaderboardRepository(),
                    true
            );
        }

        if (!useMemory) {
            System.err.println("WARNING: MySQL is not available. Using in-memory storage.");
            System.err.println("Run wordy.sql and start MySQL, or set WORDY_USE_MEMORY=true for testing.");
        }

        return new Repositories(
                InMemoryPlayerRepository.getInstance(),
                InMemoryGameConfigRepository.getInstance(),
                InMemoryLeaderboardRepository.getInstance(),
                false
        );
    }
}

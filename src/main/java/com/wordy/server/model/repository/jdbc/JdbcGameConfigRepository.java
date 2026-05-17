package com.wordy.server.model.repository.jdbc;

import com.wordy.server.model.entity.TimeConfig;
import com.wordy.server.model.repository.GameConfigRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcGameConfigRepository implements GameConfigRepository {

    private static final int DEFAULT_WAIT = 10;
    private static final int DEFAULT_ROUND = 30;

    @Override
    public TimeConfig getConfig() {
        String sql = "SELECT waiting_time_seconds, round_duration_seconds FROM time_config WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TimeConfig(
                            rs.getInt("waiting_time_seconds"),
                            rs.getInt("round_duration_seconds")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to load game config", e);
        }
        return new TimeConfig(DEFAULT_WAIT, DEFAULT_ROUND);
    }

    @Override
    public boolean setConfig(int waitingTimeSeconds, int roundDurationSeconds) {
        String sql = "UPDATE time_config SET waiting_time_seconds = ?, round_duration_seconds = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, waitingTimeSeconds);
            stmt.setInt(2, roundDurationSeconds);
            stmt.setInt(3, 1);
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                return insertConfig(waitingTimeSeconds, roundDurationSeconds);
            }
            return true;
        } catch (SQLException e) {
            throw new RepositoryException("Failed to update game config", e);
        }
    }

    private boolean insertConfig(int waitingTimeSeconds, int roundDurationSeconds) throws SQLException {
        String sql = "INSERT INTO time_config (id, waiting_time_seconds, round_duration_seconds) VALUES (1, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, waitingTimeSeconds);
            stmt.setInt(2, roundDurationSeconds);
            return stmt.executeUpdate() > 0;
        }
    }
}

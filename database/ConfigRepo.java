package com.wordy.server.database;

import java.sql.*;

public class ConfigRepo {


    public TimeConfig getConfig() throws SQLException {
        String sql = "SELECT waiting_time_seconds, round_duration_seconds FROM time_config WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new TimeConfig(
                        rs.getInt("waiting_time_seconds"),
                        rs.getInt("round_duration_seconds")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new TimeConfig(10, 30);
    }

    public boolean setConfig(int waitingTime, int roundDuration) throws SQLException {
        String sql = "UPDATE time_config SET waiting_time_seconds = ?, round_duration_seconds = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, waitingTime);
            stmt.setInt(2, roundDuration);
            stmt.setInt(3, 1);

            int rowsUpdated = stmt.executeUpdate();

            // If no row exists
            if (rowsUpdated == 0) {
                return insertConfig(waitingTime, roundDuration);
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean insertConfig(int waitingTime, int roundDuration) {
        String sql = "INSERT INTO time_config (id, waiting_time_seconds, round_duration_seconds) VALUES (1, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, waitingTime);
            stmt.setInt(2, roundDuration);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }}

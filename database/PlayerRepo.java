package com.wordy.server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlayerRepo {
    public boolean createPlayer(String username, String password, String role) throws SQLException {
        String sql = "INSERT INTO players (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            return stmt.executeUpdate() > 0;
        }
    }

    public PlayerRecord getPlayerByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM players WHERE username = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public PlayerRecord getPlayerById(int id) throws SQLException {
        String sql = "SELECT * FROM players WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public List<PlayerRecord> getAllPlayers() throws SQLException {
        List<PlayerRecord> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE role = 'PLAYER'";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) players.add(mapRow(rs));
        }
        return players;
    }

    public List<PlayerRecord> searchPlayers(String keyword) throws SQLException {
        List<PlayerRecord> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE role = 'PLAYER' AND username LIKE ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) players.add(mapRow(rs));
        }
        return players;
    }

    public boolean updatePlayer(int id, String username, String password) throws SQLException {
        return updatePlayer(id, username, password, -1);
    }

    public boolean updatePlayer(int id, String username, String password, int wins) throws SQLException {
        if (password != null && !password.isBlank()) {
            String sql = "UPDATE players SET username = ?, password = ?, wins = ? WHERE id = ?";
            try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setInt(3, wins);
                stmt.setInt(4, id);
                return stmt.executeUpdate() > 0;
            }
        }

        String sql = "UPDATE players SET username = ?, wins = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, wins);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletePlayer(int id) throws SQLException {
        String sql = "DELETE FROM players WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean incrementWins(String username) throws SQLException {
        String sql = "UPDATE players SET wins = wins + 1 WHERE username = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<PlayerRecord> getTopPlayersByWins(int limit) throws SQLException {
        List<PlayerRecord> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE role = 'PLAYER' ORDER BY wins DESC LIMIT ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) players.add(mapRow(rs));
        }
        return players;
    }

    private PlayerRecord mapRow(ResultSet rs) throws SQLException {
        return new PlayerRecord(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getInt("wins")
        );
    }

    public record PlayerRecord(int id, String username, String password, String role, int wins) {}
}

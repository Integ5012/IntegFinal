package com.wordy.server.model.repository.jdbc;

import com.wordy.server.model.entity.Player;
import com.wordy.server.model.repository.PlayerRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcPlayerRepository implements PlayerRepository {

    @Override
    public Player findByUsername(String username) {
        String sql = "SELECT id, username, password, role, wins FROM players WHERE username = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to find player", e);
        }
        return null;
    }

    @Override
    public Player findById(int id) {
        String sql = "SELECT id, username, password, role, wins FROM players WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to find player by id", e);
        }
        return null;
    }

    @Override
    public List<Player> findAllPlayers() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT id, username, password, role, wins FROM players WHERE role = 'PLAYER' ORDER BY username";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                players.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to list players", e);
        }
        return players;
    }

    @Override
    public List<Player> searchPlayers(String keyword) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT id, username, password, role, wins FROM players WHERE role = 'PLAYER' AND username LIKE ? ORDER BY username";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    players.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to search players", e);
        }
        return players;
    }

    @Override
    public boolean createPlayer(String username, String password, String role) {
        String sql = "INSERT INTO players (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Failed to create player", e);
        }
    }

    @Override
    public boolean updatePlayer(int id, String username, String password, int wins) {
        if (password != null && !password.isBlank()) {
            String sql = "UPDATE players SET username = ?, password = ?, wins = ? WHERE id = ?";
            try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setInt(3, wins);
                stmt.setInt(4, id);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RepositoryException("Failed to update player", e);
            }
        }

        String sql = "UPDATE players SET username = ?, wins = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, wins);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Failed to update player", e);
        }
    }

    @Override
    public boolean deletePlayer(int id) {
        String sql = "DELETE FROM players WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Failed to delete player", e);
        }
    }

    @Override
    public boolean incrementWins(String username) {
        String sql = "UPDATE players SET wins = wins + 1 WHERE username = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Failed to increment wins", e);
        }
    }

    @Override
    public List<Player> findTopPlayersByWins(int limit) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT id, username, password, role, wins FROM players WHERE role = 'PLAYER' ORDER BY wins DESC, username LIMIT ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    players.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to load top players", e);
        }
        return players;
    }

    private static Player mapRow(ResultSet rs) throws SQLException {
        return new Player(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getInt("wins")
        );
    }
}

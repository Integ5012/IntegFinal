package com.wordy.server.model.repository.jdbc;

import com.wordy.server.model.entity.LongestWordEntry;
import com.wordy.server.model.repository.LeaderboardRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcLeaderboardRepository implements LeaderboardRepository {

    @Override
    public void insertWord(String username, String word) {
        String sql = "INSERT INTO leaderboard_words (username, word, word_length) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, word);
            stmt.setInt(3, word.length());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to insert leaderboard word", e);
        }
        trimToTop(5);
    }

    @Override
    public List<LongestWordEntry> getTopWords(int limit) {
        List<LongestWordEntry> entries = new ArrayList<>();
        String sql = """
                SELECT username, word
                FROM leaderboard_words
                ORDER BY word_length DESC, id ASC
                LIMIT ?
                """;
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(new LongestWordEntry(rs.getString("username"), rs.getString("word")));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to load longest words", e);
        }
        return entries;
    }

    private void trimToTop(int limit) {
        String sql = """
                DELETE FROM leaderboard_words
                WHERE id NOT IN (
                    SELECT id FROM (
                        SELECT id
                        FROM leaderboard_words
                        ORDER BY word_length DESC, id ASC
                        LIMIT ?
                    ) AS top_words
                )
                """;
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to trim leaderboard", e);
        }
    }
}

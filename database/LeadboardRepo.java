package com.wordy.server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LeadboardRepo {
    public void insertWord(String username, String word) throws SQLException {
        String sql = " INSERT INTO leaderboard_words (username, word, word_length) VALUES (?, ?, ?) ";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, word);
            stmt.setInt(3, word.length());
            stmt.executeUpdate();
        }
        trimToTop5();
    }

    private void trimToTop5() throws SQLException {
        String sql = """
        DELETE FROM leaderboard_words
        WHERE id NOT IN (
            SELECT id FROM (
                SELECT id
                FROM leaderboard_words
                ORDER BY word_length DESC, id ASC
                LIMIT 5
            ) AS top5
        )
        """;

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public List<WordEntry> getTop5Words() throws SQLException {
        List<WordEntry> entries = new ArrayList<>();
        String sql = """
        SELECT username, word
        FROM leaderboard_words
        ORDER BY word_length DESC, id ASC
        LIMIT 5
        """;

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                entries.add(new WordEntry(
                        rs.getString("username"),
                        rs.getString("word")
                ));
            }
        }

        return entries;
    }

    public record WordEntry(String username, String word) {}
}

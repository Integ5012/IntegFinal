package com.wordy.client.player.view;

import com.wordy.client.common.UiTheme;
import com.wordy.grpc.LeaderboardResponse;
import com.wordy.grpc.LongestWordRecord;
import com.wordy.grpc.Player;

import javax.swing.*;
import java.awt.*;

public class LeaderboardView extends JFrame {

    public LeaderboardView(LeaderboardResponse response) {
        setTitle("WORDY — Leaderboard");
        setSize(480, 520);
        setLocationRelativeTo(null);
        UiTheme.styleRoot(this);

        JTextArea area = new JTextArea();
        UiTheme.styleTextArea(area);
        area.setText(formatLeaderboard(response));

        JPanel card = UiTheme.cardPanel(new BorderLayout(0, 10));
        card.add(UiTheme.titleLabel("Leaderboard"), BorderLayout.NORTH);
        card.add(UiTheme.styleScrollPane(area), BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UiTheme.BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        wrapper.add(card);
        add(wrapper);
    }

    private static String formatLeaderboard(LeaderboardResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("TOP PLAYERS (by wins)\n");
        builder.append("─".repeat(36)).append("\n\n");

        int rank = 1;
        for (Player player : response.getTopPlayersList()) {
            builder.append(String.format("  %d. %-16s %d wins%n",
                    rank++, player.getUsername(), player.getWins()));
        }
        if (response.getTopPlayersCount() == 0) {
            builder.append("  No wins recorded yet.\n");
        }

        builder.append("\nLONGEST WORDS\n");
        builder.append("─".repeat(36)).append("\n\n");

        rank = 1;
        for (LongestWordRecord record : response.getLongestWordsList()) {
            builder.append(String.format("  %d. %-12s \"%s\" (%d letters)%n",
                    rank++, record.getUsername(), record.getWord(), record.getWord().length()));
        }
        if (response.getLongestWordsCount() == 0) {
            builder.append("  No words recorded yet.\n");
        }

        return builder.toString();
    }
}

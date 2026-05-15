package com.wordy.client.player.view;

import com.wordy.grpc.LeaderboardResponse;
import com.wordy.grpc.LongestWordRecord;
import com.wordy.grpc.Player;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LeaderboardView extends JFrame {

    public LeaderboardView(LeaderboardResponse response) {
        setTitle("Leaderboard");
        setSize(450, 450);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setEditable(false);

        StringBuilder builder = new StringBuilder("TOP PLAYERS (by wins)\n\n");
        int rank = 1;
        for (Player player : response.getTopPlayersList()) {
            builder.append(rank++)
                    .append(". ")
                    .append(player.getUsername())
                    .append(" - ")
                    .append(player.getWins())
                    .append(" wins\n");
        }

        builder.append("\nLONGEST WORDS\n\n");
        rank = 1;
        for (LongestWordRecord record : response.getLongestWordsList()) {
            builder.append(rank++)
                    .append(". ")
                    .append(record.getUsername())
                    .append(" - ")
                    .append(record.getWord())
                    .append(" (")
                    .append(record.getWord().length())
                    .append(" letters)\n");
        }

        if (response.getTopPlayersCount() == 0 && response.getLongestWordsCount() == 0) {
            builder.append("No records yet.");
        }

        area.setText(builder.toString());
        add(new JScrollPane(area));
    }
}

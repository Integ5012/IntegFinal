package com.wordy.client.player.view;

import com.wordy.client.common.UiTheme;
import com.wordy.grpc.LeaderboardResponse;
import com.wordy.grpc.LongestWordRecord;
import com.wordy.grpc.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LeaderboardView extends JFrame {

    public LeaderboardView(LeaderboardResponse response) {
        setTitle("WORDY — Leaderboard");
        setMinimumSize(new Dimension(620, 480));
        setSize(680, 560);
        setLocationRelativeTo(null);
        UiTheme.styleRoot(this);

        JPanel winsSection = buildSection(
                "Top players (by wins)",
                new String[]{"Rank", "Player", "Wins"},
                buildWinsRows(response));

        JPanel wordsSection = buildSection(
                "Longest words",
                new String[]{"Rank", "Player", "Word", "Letters"},
                buildWordRows(response));

        JPanel sections = new JPanel();
        sections.setLayout(new BoxLayout(sections, BoxLayout.Y_AXIS));
        UiTheme.applyDarkSurface(sections);
        sections.add(winsSection);
        sections.add(Box.createVerticalStrut(20));
        sections.add(wordsSection);

        JScrollPane scroll = UiTheme.styleScrollPane(sections);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel card = UiTheme.cardPanel(new BorderLayout(0, 12));
        card.add(UiTheme.titleLabel("Leaderboard"), BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        UiTheme.stylePrimaryButton(closeBtn);
        closeBtn.addActionListener(e -> dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(closeBtn);
        card.add(footer, BorderLayout.SOUTH);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiTheme.BG);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.add(card, BorderLayout.CENTER);
        add(root);
    }

    private static JPanel buildSection(String title, String[] columns, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Object[] row : rows) {
            model.addRow(row);
        }

        JTable table = new JTable(model);
        UiTheme.styleTable(table);
        table.setPreferredScrollableViewportSize(new Dimension(560, Math.max(80, rows.length * 34 + 8)));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        UiTheme.applyDarkSurface(panel);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel heading = UiTheme.sectionHeading(title);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(UiTheme.styleScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static Object[][] buildWinsRows(LeaderboardResponse response) {
        if (response.getTopPlayersCount() == 0) {
            return new Object[][]{{"—", "No records yet", ""}};
        }
        Object[][] rows = new Object[response.getTopPlayersCount()][3];
        int rank = 1;
        for (int i = 0; i < response.getTopPlayersCount(); i++) {
            Player player = response.getTopPlayers(i);
            rows[i] = new Object[]{rank++, player.getUsername(), player.getWins()};
        }
        return rows;
    }

    private static Object[][] buildWordRows(LeaderboardResponse response) {
        if (response.getLongestWordsCount() == 0) {
            return new Object[][]{{"—", "No records yet", "", ""}};
        }
        Object[][] rows = new Object[response.getLongestWordsCount()][4];
        int rank = 1;
        for (int i = 0; i < response.getLongestWordsCount(); i++) {
            LongestWordRecord record = response.getLongestWords(i);
            String word = record.getWord() == null ? "" : record.getWord().trim();
            rows[i] = new Object[]{
                    rank++,
                    record.getUsername(),
                    word,
                    word.length()
            };
        }
        return rows;
    }
}

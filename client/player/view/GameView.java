package com.wordy.client.player.view;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {

    private static final Font LETTERS_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final JLabel welcomeLabel;
    private final JLabel roundLabel;
    private final JLabel lettersLabel;

    private final JTextArea gameLog;

    private final JTextField wordField;

    private final JButton startButton;
    private final JButton submitButton;
    private final JButton leaderboardButton;
    private final JButton logoutButton;

    public GameView() {

        setTitle("WORDY Game");
        setSize(780, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        welcomeLabel = new JLabel("Welcome Player");
        welcomeLabel.setFont(HEADER_FONT);
        roundLabel = new JLabel("Round: 0");
        roundLabel.setFont(HEADER_FONT);

        lettersLabel = new JLabel("Letters:", SwingConstants.CENTER);
        lettersLabel.setFont(LETTERS_FONT);
        lettersLabel.setOpaque(true);
        lettersLabel.setBackground(new Color(245, 247, 250));
        lettersLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 210)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        wordField = new JTextField(15);

        startButton = new JButton("Start Game");
        submitButton = new JButton("Submit Word");
        leaderboardButton = new JButton("Leaderboard");
        logoutButton = new JButton("Logout");

        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 8, 12));

        JPanel headerRow = new JPanel(new GridLayout(2, 1, 4, 4));
        headerRow.add(welcomeLabel);
        headerRow.add(roundLabel);
        topPanel.add(headerRow, BorderLayout.NORTH);
        topPanel.add(lettersLabel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(gameLog);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(wordField);
        bottomPanel.add(submitButton);
        bottomPanel.add(startButton);
        bottomPanel.add(leaderboardButton);
        bottomPanel.add(logoutButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public JLabel getWelcomeLabel() {
        return welcomeLabel;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getSubmitButton() {
        return submitButton;
    }

    public JButton getLeaderboardButton() {
        return leaderboardButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JTextField getWordField() {
        return wordField;
    }

    public void setLetters(String letters) {
        if (letters == null || letters.isBlank()) {
            lettersLabel.setText("Letters:");
            return;
        }
        lettersLabel.setText(letters.trim());
    }

    public void setRoundLabel(String text) {
        roundLabel.setText(text);
    }

    public void appendGameLog(String text) {
        gameLog.append(text);
    }
}

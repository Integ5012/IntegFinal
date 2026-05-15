package com.wordy.client.player.view;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {

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
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // INITIALIZE COMPONENTS
        welcomeLabel = new JLabel("Welcome Player");
        roundLabel = new JLabel("Round: 0");
        lettersLabel = new JLabel("Letters:");

        gameLog = new JTextArea();
        gameLog.setEditable(false);

        wordField = new JTextField(15);

        startButton = new JButton("Start Game");
        submitButton = new JButton("Submit Word");
        leaderboardButton = new JButton("Leaderboard");
        logoutButton = new JButton("Logout");

        // TOP PANEL
        JPanel topPanel = new JPanel(new GridLayout(3, 1));

        topPanel.add(welcomeLabel);
        topPanel.add(roundLabel);
        topPanel.add(lettersLabel);

        // CENTER
        JScrollPane scrollPane = new JScrollPane(gameLog);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel();

        bottomPanel.add(wordField);
        bottomPanel.add(submitButton);
        bottomPanel.add(startButton);
        bottomPanel.add(leaderboardButton);
        bottomPanel.add(logoutButton);

        // FRAME
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
        lettersLabel.setText("Letters: " + letters);
    }

    public void setRoundLabel(String text) {
        roundLabel.setText(text);
    }

    public void appendGameLog(String text) {
        gameLog.append(text);
    }
}
package com.wordy.client.player.view;

import com.wordy.client.common.UiTheme;

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
        setTitle("WORDY — Game");
        setSize(820, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UiTheme.styleRoot(this);

        welcomeLabel = UiTheme.subtitleLabel("Welcome Player");
        roundLabel = UiTheme.fieldLabel("Round: 0");
        lettersLabel = new JLabel("—", SwingConstants.CENTER);

        gameLog = new JTextArea();
        UiTheme.styleTextArea(gameLog);

        wordField = new JTextField(18);
        UiTheme.styleTextField(wordField);

        startButton = new JButton("Start Game");
        submitButton = new JButton("Submit Word");
        leaderboardButton = new JButton("Leaderboard");
        logoutButton = new JButton("Logout");

        UiTheme.styleSecondaryButton(startButton);
        UiTheme.styleSuccessButton(submitButton);
        UiTheme.styleSecondaryButton(leaderboardButton);
        UiTheme.styleDangerButton(logoutButton);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UiTheme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel headerCard = UiTheme.cardPanel(new BorderLayout(0, 10));
        JPanel headerText = new JPanel(new GridLayout(2, 1, 0, 4));
        headerText.setOpaque(false);
        headerText.add(welcomeLabel);
        headerText.add(roundLabel);

        JLabel lettersCaption = UiTheme.fieldLabel("Available letters");
        lettersCaption.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel lettersBlock = new JPanel(new BorderLayout(0, 8));
        lettersBlock.setOpaque(false);
        lettersBlock.add(lettersCaption, BorderLayout.NORTH);
        lettersBlock.add(UiTheme.lettersPanel(lettersLabel), BorderLayout.CENTER);

        headerCard.add(headerText, BorderLayout.NORTH);
        headerCard.add(lettersBlock, BorderLayout.CENTER);

        JPanel logCard = UiTheme.cardPanel(new BorderLayout());
        JLabel logTitle = UiTheme.fieldLabel("Game log");
        logCard.add(logTitle, BorderLayout.NORTH);
        logCard.add(UiTheme.styleScrollPane(gameLog), BorderLayout.CENTER);

        JPanel actionBar = new JPanel(new BorderLayout(12, 0));
        actionBar.setOpaque(false);
        actionBar.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JPanel wordRow = new JPanel(new BorderLayout(8, 0));
        wordRow.setOpaque(false);
        wordRow.add(UiTheme.fieldLabel("Your word"), BorderLayout.NORTH);
        wordRow.add(wordField, BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(startButton);
        buttonRow.add(submitButton);
        buttonRow.add(leaderboardButton);
        buttonRow.add(logoutButton);

        actionBar.add(wordRow, BorderLayout.CENTER);
        actionBar.add(buttonRow, BorderLayout.EAST);

        root.add(headerCard, BorderLayout.NORTH);
        root.add(logCard, BorderLayout.CENTER);
        root.add(actionBar, BorderLayout.SOUTH);
        add(root);
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
            lettersLabel.setText("—");
            return;
        }
        lettersLabel.setText(letters.trim());
    }

    public void setRoundLabel(String text) {
        roundLabel.setText(text);
    }

    public void appendGameLog(String text) {
        gameLog.append(text);
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }
}

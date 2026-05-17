package com.wordy.client.player.controller;

import com.wordy.client.player.model.PlayerSession;
import com.wordy.client.player.service.PlayerGameService;
import com.wordy.client.player.view.GameView;
import com.wordy.client.player.view.LeaderboardView;
import com.wordy.common.ClientConfig;
import com.wordy.grpc.*;

import javax.swing.*;
import java.util.Iterator;

public class GameController {

    private final PlayerGameService gameService;
    private final PlayerSession session;
    private final GameView view;
    private final Runnable onReturnToLogin;

    private int lastLoggedRound;
    private String lastWaitingMessage;

    public GameController(
            ClientConfig.Settings settings,
            PlayerSession session,
            GameView view,
            Runnable onReturnToLogin) {
        this.gameService = new PlayerGameService(settings, session);
        this.session = session;
        this.view = view;
        this.onReturnToLogin = onReturnToLogin;
        initialize();
    }

    private void initialize() {
        view.getWelcomeLabel().setText("Welcome, " + session.getUsername());
        view.getStartButton().addActionListener(e -> startGame());
        view.getSubmitButton().addActionListener(e -> submitWord());
        view.getLeaderboardButton().addActionListener(e -> showLeaderboard());
        view.getLogoutButton().addActionListener(e -> logout());
    }

    private void startGame() {
        new Thread(() -> {
            try {
                Iterator<GameEvent> events = gameService.joinGame();
                while (events.hasNext()) {
                    GameEvent event = events.next();
                    SwingUtilities.invokeLater(() -> processEvent(event));
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(view, "Disconnected from server"));
            }
        }).start();
    }

    private static String formatLetters(java.util.List<String> letters) {
        return String.join("   ", letters);
    }

    private void appendWaitingMessage(String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        if (message.equals(lastWaitingMessage)) {
            return;
        }
        if (message.contains(" joined the queue for the next game")) {
            return;
        }
        lastWaitingMessage = message;
        view.appendGameLog(message + "\n");
    }

    private void processEvent(GameEvent event) {
        String type = event.getEventType();

        switch (type) {
            case "WAITING" -> appendWaitingMessage(event.getWinner());
            case "PLAYER_JOINED" -> {
                String joinDetail = event.getBestWord();
                if (joinDetail != null && joinDetail.startsWith("Round wins")) {
                    view.appendGameLog("Joined the game in progress. " + joinDetail + "\n");
                } else if (event.getWinner() != null && joinDetail != null && !joinDetail.isBlank()) {
                    view.appendGameLog(event.getWinner() + " " + joinDetail + ".\n");
                }
            }
            case "START" -> {
                view.appendGameLog("Game started.\n");
                view.getStartButton().setEnabled(false);
            }
            case "ROUND" -> {
                int round = event.getRound();
                int timeLeft = event.getTimeLeft();
                view.setRoundLabel("Round: " + round + "  |  Time left: " + timeLeft + "s");
                if (!event.getLettersList().isEmpty()) {
                    view.setLetters(formatLetters(event.getLettersList()));
                }
                if (round != lastLoggedRound) {
                    lastLoggedRound = round;
                    view.appendGameLog("Round " + round + " started (" + timeLeft + "s to submit a word).\n");
                }
            }
            case "RESULT" -> {
                if (event.getWinner() == null || event.getWinner().isBlank()) {
                    view.appendGameLog("Round " + event.getRound()
                            + " ended — no winner (tie or no valid words).\n");
                } else {
                    view.appendGameLog("Round " + event.getRound() + " winner: " + event.getWinner()
                            + " | Word: " + event.getBestWord() + "\n");
                }
            }
            case "END" -> {
                view.appendGameLog("Game finished. Overall winner: " + event.getWinner() + "\n");
                view.getStartButton().setEnabled(true);
                lastLoggedRound = 0;
            }
            default -> {
                // ignore unknown events
            }
        }
    }

    private void submitWord() {
        String word = view.getWordField().getText();
        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Enter a word");
            return;
        }

        try {
            SubmitWordResponse response = gameService.submitWord(word);
            String title = response.getValid() ? "Word accepted" : "Invalid word";
            JOptionPane.showMessageDialog(view, response.getMessage(), title,
                    response.getValid() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            view.getWordField().setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Failed to submit word");
        }
    }

    private void showLeaderboard() {
        try {
            LeaderboardResponse response = gameService.getLeaderboard();
            LeaderboardView leaderboardView = new LeaderboardView(response);
            leaderboardView.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Failed to load leaderboard");
        }
    }

    private void logout() {
        try {
            gameService.logout();
        } catch (Exception ignored) {
            // still return to login
        }
        gameService.close();
        view.dispose();
        onReturnToLogin.run();
    }
}

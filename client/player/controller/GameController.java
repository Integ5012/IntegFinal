package com.wordy.client.player.controller;

import com.wordy.client.player.model.PlayerModel;
import com.wordy.client.player.view.GameView;
import com.wordy.client.player.view.LeaderboardView;
import com.wordy.grpc.*;

import javax.swing.*;
import java.util.Iterator;

public class GameController {

    private final PlayerModel model;
    private final GameView view;

    public GameController(PlayerModel model, GameView view) {
        this.model = model;
        this.view = view;

        initialize();
    }

    private void initialize() {

        view.getWelcomeLabel().setText("Welcome " + model.getUsername());

        view.getStartButton().addActionListener(e -> startGame());

        view.getSubmitButton().addActionListener(e -> submitWord());

        view.getLeaderboardButton().addActionListener(e -> showLeaderboard());

        view.getLogoutButton().addActionListener(e -> logout());
    }
    private void startGame() {

        new Thread(() -> {

            try {
                Iterator<GameEvent> events = model.joinGame();

                while (events.hasNext()) {
                    GameEvent event = events.next();

                    SwingUtilities.invokeLater(() -> processEvent(event));
                }

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(view, "Disconnected from server")
                );
            }

        }).start();
    }

    private void processEvent(GameEvent event) {

        String type = event.getEventType();

        switch (type) {
            case "WAITING":
                view.appendGameLog(event.getWinner() + "\n");
                break;

            case "START":
                view.appendGameLog("Game Started\n");
                break;

            case "ROUND":
                view.setRoundLabel("Round: " + event.getRound());
                view.setLetters(String.join(" ", event.getLettersList()));
                view.appendGameLog("New Round Started\n");
                break;

            case "RESULT":
                view.appendGameLog(
                        "Winner: " + event.getWinner() +
                                " | Word: " + event.getBestWord() + "\n"
                );
                break;

            case "END":
                view.appendGameLog("Game Finished\n");
                break;
        }
    }
    private void submitWord() {

        String word = view.getWordField().getText();

        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Enter a word");
            return;
        }

        try {
            SubmitWordResponse response = model.submitWord(word);

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
            LeaderboardResponse response = model.getLeaderboard();

            LeaderboardView leaderboardView = new LeaderboardView(response);
            leaderboardView.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Failed to load leaderboard");
        }
    }

    private void logout() {

        try {
            model.logout();
            model.shutdown();

        } catch (Exception ignored) {
        }

        System.exit(0);
    }
}

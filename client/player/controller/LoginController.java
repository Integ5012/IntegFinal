package com.wordy.client.player.controller;

import com.wordy.client.player.model.PlayerModel;
import com.wordy.client.player.view.GameView;
import com.wordy.client.player.view.LoginView;
import com.wordy.common.ClientConfig;
import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.LoginResponse;

import javax.swing.*;

public class LoginController {

    private final LoginView view;
    private final Runnable onReturnToLogin;

    public LoginController(LoginView view, Runnable onReturnToLogin) {
        this.view = view;
        this.onReturnToLogin = onReturnToLogin;
        initController();
    }

    private void initController() {
        view.getLoginButton().addActionListener(e -> handleLogin());
        view.getCreateAccountButton().addActionListener(e -> handleCreateAccount());
    }

    private PlayerModel connectModel() throws Exception {
        view.getConnectionPanel().saveSettings();
        ClientConfig.Settings settings = ClientConfig.load();
        return new PlayerModel(settings.host(), settings.port());
    }

    private void handleLogin() {
        String username = view.getUsernameField().getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Enter a username.");
            return;
        }

        PlayerModel model = null;
        try {
            model = connectModel();
            LoginResponse response = model.login(username, "");

            if (response.getSuccess() && "PLAYER".equals(response.getRole())) {
                GameView gameView = new GameView();
                new GameController(model, gameView, onReturnToLogin);
                gameView.setVisible(true);
                view.dispose();
            } else if (response.getSuccess()) {
                model.shutdown();
                JOptionPane.showMessageDialog(view, "Use the admin client to log in as an administrator.");
            } else {
                model.shutdown();
                JOptionPane.showMessageDialog(view, response.getMessage());
            }
        } catch (NumberFormatException ex) {
            if (model != null) {
                model.shutdown();
            }
            JOptionPane.showMessageDialog(view, "Port must be a valid number.");
        } catch (Exception ex) {
            if (model != null) {
                model.shutdown();
            }
            JOptionPane.showMessageDialog(view, "Server connection failed: " + ex.getMessage());
        }
    }

    private void handleCreateAccount() {
        String username = view.getUsernameField().getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Enter a username to register.");
            return;
        }

        PlayerModel model = null;
        try {
            model = connectModel();
            BasicResponse response = model.register(username);
            if (response.getSuccess()) {
                JOptionPane.showMessageDialog(view, response.getMessage());
            } else {
                JOptionPane.showMessageDialog(view, response.getMessage());
            }
            model.shutdown();
        } catch (NumberFormatException ex) {
            if (model != null) {
                model.shutdown();
            }
            JOptionPane.showMessageDialog(view, "Port must be a valid number.");
        } catch (Exception ex) {
            if (model != null) {
                model.shutdown();
            }
            JOptionPane.showMessageDialog(view, "Server connection failed: " + ex.getMessage());
        }
    }
}

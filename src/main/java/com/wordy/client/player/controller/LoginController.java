package com.wordy.client.player.controller;

import com.wordy.client.common.GrpcConnectionFactory;
import com.wordy.client.player.model.PlayerSession;
import com.wordy.client.player.service.PlayerAuthService;
import com.wordy.client.player.service.PlayerGameService;
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

    private ClientConfig.Settings connectionSettings() {
        var panel = view.getConnectionPanel();
        return ClientConfig.resolve(panel.getHost(), panel.getPortText());
    }

    private void handleLogin() {
        String username = view.getUsernameField().getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Enter a username.");
            return;
        }

        ClientConfig.Settings settings = connectionSettings();
        try (PlayerAuthService authService = new PlayerAuthService(settings)) {
            LoginResponse response = authService.login(username, "");

            if (response.getSuccess() && "PLAYER".equals(response.getRole())) {
                PlayerSession session = new PlayerSession();
                session.applyLogin(username, response.getSessionId());

                GameView gameView = new GameView();
                new GameController(settings, session, gameView, onReturnToLogin);
                gameView.setVisible(true);
                view.dispose();
            } else if (response.getSuccess()) {
                JOptionPane.showMessageDialog(view, "Use the admin client to log in as an administrator.");
            } else {
                JOptionPane.showMessageDialog(view, response.getMessage());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Port must be a valid number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Server connection failed:\n" + GrpcConnectionFactory.friendlyConnectionError(ex));
        }
    }

    private void handleCreateAccount() {
        String username = view.getUsernameField().getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Enter a username to register.");
            return;
        }

        ClientConfig.Settings settings = connectionSettings();
        try (PlayerAuthService authService = new PlayerAuthService(settings)) {
            BasicResponse response = authService.register(username);
            JOptionPane.showMessageDialog(view, response.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Port must be a valid number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Server connection failed:\n" + GrpcConnectionFactory.friendlyConnectionError(ex));
        }
    }
}

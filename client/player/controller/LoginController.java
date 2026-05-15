package com.wordy.client.player.controller;

import com.wordy.client.player.model.PlayerModel;
import com.wordy.client.player.view.GameView;
import com.wordy.client.player.view.LoginView;
import com.wordy.grpc.LoginResponse;

import javax.swing.*;

public class LoginController {

    private final PlayerModel model;
    private final LoginView view;

    public LoginController(PlayerModel model, LoginView view) {
        this.model = model;
        this.view = view;

        initController();
    }

    private void initController() {

        view.getLoginButton().addActionListener(e -> {

            String username = view.getUsernameField().getText();
            String password = String.valueOf(view.getPasswordField().getPassword());

            try {
                LoginResponse response = model.login(username, password);

                if (response.getSuccess() && "PLAYER".equals(response.getRole())) {
                    JOptionPane.showMessageDialog(view, response.getMessage());

                    GameView gameView = new GameView();
                    new GameController(model, gameView);

                    gameView.setVisible(true);
                    view.dispose();
                } else if (response.getSuccess()) {
                    JOptionPane.showMessageDialog(view, "Use the admin client to log in as an administrator.");
                } else {
                    JOptionPane.showMessageDialog(view, response.getMessage());
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Server connection failed");
            }
        });
    }
}
package com.wordy.client.player.view;

import com.wordy.client.player.model.PlayerModel;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginView() {

        setTitle("WORDY Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username"));
        panel.add(usernameField);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);

        add(panel, BorderLayout.CENTER);
        add(loginButton, BorderLayout.SOUTH);

        PlayerModel model = new PlayerModel();
        new com.wordy.client.player.controller.LoginController(model, this);
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

}
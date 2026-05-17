package com.wordy.client.player.view;

import com.wordy.client.common.ServerConnectionPanel;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private final ServerConnectionPanel connectionPanel;
    private final JTextField usernameField;
    private final JButton loginButton;
    private final JButton createAccountButton;

    public LoginView() {
        setTitle("WORDY Login");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectionPanel = new ServerConnectionPanel();
        usernameField = new JTextField();
        loginButton = new JButton("Login");
        createAccountButton = new JButton("Create account");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        panel.add(connectionPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(new JLabel("Username"));
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttons.add(loginButton);
        buttons.add(createAccountButton);
        panel.add(buttons);

        add(panel);
    }

    public ServerConnectionPanel getConnectionPanel() {
        return connectionPanel;
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getCreateAccountButton() {
        return createAccountButton;
    }
}

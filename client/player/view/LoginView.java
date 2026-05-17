package com.wordy.client.player.view;

import com.wordy.client.common.ServerConnectionPanel;
import com.wordy.client.common.UiTheme;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private final ServerConnectionPanel connectionPanel;
    private final JTextField usernameField;
    private final JButton loginButton;
    private final JButton createAccountButton;

    public LoginView() {
        setTitle("WORDY — Player Login");
        setSize(440, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UiTheme.styleRoot(this);

        connectionPanel = new ServerConnectionPanel();
        usernameField = new JTextField();
        UiTheme.styleTextField(usernameField);

        loginButton = new JButton("Login");
        createAccountButton = new JButton("Create account");
        UiTheme.stylePrimaryButton(loginButton);
        UiTheme.styleSecondaryButton(createAccountButton);

        JPanel card = UiTheme.cardPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = UiTheme.titleLabel("WORDY");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = UiTheme.subtitleLabel("Player login");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        connectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel userLabel = UiTheme.fieldLabel("Username");
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.setOpaque(false);
        buttons.add(loginButton);
        buttons.add(createAccountButton);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));
        card.add(connectionPanel);
        card.add(Box.createVerticalStrut(16));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(20));
        card.add(buttons);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UiTheme.BG);
        wrapper.add(card);
        add(wrapper);
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

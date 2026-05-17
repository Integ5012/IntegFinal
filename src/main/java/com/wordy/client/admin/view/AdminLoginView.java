package com.wordy.client.admin.view;

import com.wordy.client.common.ServerConnectionPanel;
import com.wordy.client.common.UiTheme;

import javax.swing.*;
import java.awt.*;

public class AdminLoginView extends JFrame {

    public ServerConnectionPanel connectionPanel;
    public JTextField usernameField;
    public JPasswordField passwordField;
    public JButton loginButton;

    public AdminLoginView() {
        setTitle("WORDY — Admin Login");
        setSize(440, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UiTheme.styleRoot(this);

        JPanel card = UiTheme.cardPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = UiTheme.titleLabel("Admin");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = UiTheme.subtitleLabel("Wordy control panel");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        connectionPanel = new ServerConnectionPanel();
        connectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel usernameLbl = UiTheme.fieldLabel("Username");
        usernameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField = new JTextField();
        UiTheme.styleTextField(usernameField);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passwordLbl = UiTheme.fieldLabel("Password");
        passwordLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = new JPasswordField();
        UiTheme.stylePasswordField(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = new JButton("Login");
        UiTheme.stylePrimaryButton(loginButton);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(200, 44));

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));
        card.add(connectionPanel);
        card.add(Box.createVerticalStrut(16));
        card.add(usernameLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(passwordLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(22));
        card.add(loginButton);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UiTheme.BG);
        wrapper.add(card);
        add(wrapper);

        setVisible(true);
    }
}

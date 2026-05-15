package com.wordy.client.admin.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminLoginView extends JFrame {

    public JTextField usernameField;
    public JPasswordField passwordField;
    public JButton loginButton;

    // THEME
    private final Color bg = new Color(46, 51, 66);
    private final Color card = new Color(35, 37, 45);
    private final Color accent = new Color(99, 102, 241);
    private final Color text = Color.WHITE;

    public AdminLoginView() {
        setTitle("Admin Login");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        getContentPane().setBackground(bg);

        add(createLoginCard());

        setVisible(true);
    }

    private JPanel createLoginCard() {
        JPanel panel = new JPanel();
        panel.setBackground(card);
        panel.setPreferredSize(new Dimension(300, 250));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // TITLE
        JLabel title = new JLabel("Admin Login");
        title.setForeground(text);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Wordy System");
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel username = new JLabel("Username: ");
        username.setForeground(Color.WHITE);
        username.setAlignmentX(Component.RIGHT_ALIGNMENT);
        username.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));

        JLabel password = new JLabel("Password: ");
        password.setForeground(Color.WHITE);
        password.setAlignmentX(Component.RIGHT_ALIGNMENT);
        password.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));


        // FIELDS
        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // BUTTON
        loginButton = new JButton("Login");
        loginButton.setFocusPainted(false);
        loginButton.setBackground(accent);
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // SPACING
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(username);
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(password);
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(loginButton);

        return panel;
    }
}

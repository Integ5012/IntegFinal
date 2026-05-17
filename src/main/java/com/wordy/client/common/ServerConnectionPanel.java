package com.wordy.client.common;

import com.wordy.common.ClientConfig;
import com.wordy.common.EndpointConfig;

import javax.swing.*;
import java.awt.*;

public class ServerConnectionPanel extends JPanel {

    private final JTextField hostField;
    private final JTextField portField;

    public ServerConnectionPanel() {
        setLayout(new GridBagLayout());
        setBackground(UiTheme.CARD);
        setBorder(UiTheme.sectionBorder("Server connection"));

        ClientConfig.Settings settings = ClientConfig.load();
        hostField = new JTextField(settings.host());
        portField = new JTextField(String.valueOf(settings.port()));
        UiTheme.styleTextField(hostField);
        UiTheme.styleTextField(portField);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(UiTheme.fieldLabel("Host"), gbc);
        gbc.gridx = 1;
        add(hostField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(UiTheme.fieldLabel("Port"), gbc);
        gbc.gridx = 1;
        add(portField, gbc);

        JButton detectBtn = new JButton("Use server port");
        UiTheme.styleSecondaryButton(detectBtn);
        detectBtn.addActionListener(e -> applyPublishedPort());

        JButton saveBtn = new JButton("Save settings");
        UiTheme.styleSecondaryButton(saveBtn);
        saveBtn.addActionListener(e -> saveSettings());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(detectBtn);
        actions.add(saveBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 4, 0, 4);
        add(actions, gbc);
    }

    public String getHost() {
        return hostField.getText().trim();
    }

    public String getPortText() {
        return portField.getText().trim();
    }

    private void applyPublishedPort() {
        int port = EndpointConfig.readPublishedPort();
        portField.setText(String.valueOf(port));
        JOptionPane.showMessageDialog(this,
                "Port set to " + port + " from .wordy-grpc-port\n(Run the server first if this is still 9090.)");
    }

    public void saveSettings() {
        try {
            ClientConfig.Settings settings = ClientConfig.resolve(getHost(), getPortText());
            if (settings.port() <= 0 || settings.port() > 65535) {
                JOptionPane.showMessageDialog(this, "Port must be between 1 and 65535.");
                return;
            }
            ClientConfig.save(settings.host(), settings.port());
            JOptionPane.showMessageDialog(this, "Connection settings saved.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not save settings: " + ex.getMessage());
        }
    }
}

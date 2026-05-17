package com.wordy.client.common;

import com.wordy.common.ClientConfig;

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

        JButton saveBtn = new JButton("Save settings");
        UiTheme.styleSecondaryButton(saveBtn);
        saveBtn.addActionListener(e -> saveSettings());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 4, 0, 4);
        add(saveBtn, gbc);
    }

    public String getHost() {
        return hostField.getText().trim();
    }

    public int getPort() {
        return Integer.parseInt(portField.getText().trim());
    }

    public void saveSettings() {
        try {
            int port = getPort();
            if (port <= 0 || port > 65535) {
                JOptionPane.showMessageDialog(this, "Port must be between 1 and 65535.");
                return;
            }
            ClientConfig.save(getHost(), port);
            JOptionPane.showMessageDialog(this, "Connection settings saved.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Port must be a number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not save settings: " + ex.getMessage());
        }
    }
}

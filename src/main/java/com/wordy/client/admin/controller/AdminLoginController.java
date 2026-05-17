package com.wordy.client.admin.controller;

import com.wordy.client.admin.AdminMain;
import com.wordy.client.admin.service.AdminAuthService;
import com.wordy.client.admin.service.AdminPlayerService;
import com.wordy.client.admin.view.AdminDashboardView;
import com.wordy.client.admin.view.AdminLoginView;
import com.wordy.client.common.GrpcConnectionFactory;
import com.wordy.common.ClientConfig;
import com.wordy.grpc.LoginResponse;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AdminLoginController {

    private final AdminLoginView view;

    public AdminLoginController(AdminLoginView view) {
        this.view = view;
        this.view.loginButton.addActionListener(this::handleLogin);
    }

    private ClientConfig.Settings connectionSettings() {
        var panel = view.connectionPanel;
        return ClientConfig.resolve(panel.getHost(), panel.getPortText());
    }

    private void handleLogin(ActionEvent e) {
        String username = view.usernameField.getText().trim();
        String password = new String(view.passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter both username and password.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ClientConfig.Settings settings = connectionSettings();
        try (AdminAuthService authService = new AdminAuthService(settings)) {
            LoginResponse response = authService.login(username, password);

            if (response.getSuccess() && "ADMIN".equals(response.getRole())) {
                view.dispose();
                AdminPlayerService playerService =
                        new AdminPlayerService(settings, authService.getSessionId());
                AdminDashboardView dashboardView = new AdminDashboardView();
                new AdminDashboardController(dashboardView, playerService, AdminMain::showLogin);
            } else if (response.getSuccess()) {
                JOptionPane.showMessageDialog(view, "Access Denied: You do not have admin privileges.",
                        "Unauthorized", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Login Failed: " + response.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Connection Error:\n" + GrpcConnectionFactory.friendlyConnectionError(ex),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

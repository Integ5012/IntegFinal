package com.wordy.client.admin.controller;

import com.wordy.client.admin.AdminMain;
import com.wordy.client.admin.model.AdminModel;
import com.wordy.client.admin.view.AdminDashboardView;
import com.wordy.client.admin.view.AdminLoginView;
import com.wordy.common.ClientConfig;
import com.wordy.grpc.LoginResponse;
import io.grpc.StatusRuntimeException;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AdminLoginController {
    private final AdminLoginView view;

    public AdminLoginController(AdminLoginView view) {
        this.view = view;
        this.view.loginButton.addActionListener(this::handleLogin);
    }

    private void handleLogin(ActionEvent e) {
        String username = view.usernameField.getText().trim();
        String password = new String(view.passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter both username and password.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        AdminModel model = null;
        try {
            view.connectionPanel.saveSettings();
            ClientConfig.Settings settings = ClientConfig.load();
            model = new AdminModel(settings.host(), settings.port());
            LoginResponse response = model.login(username, password);

            if (response.getSuccess() && "ADMIN".equals(response.getRole())) {
                view.dispose();
                AdminDashboardView dashboardView = new AdminDashboardView();
                new AdminDashboardController(dashboardView, model, AdminMain::showLogin);
            } else if (response.getSuccess()) {
                model.shutdown();
                JOptionPane.showMessageDialog(view, "Access Denied: You do not have admin privileges.",
                        "Unauthorized", JOptionPane.ERROR_MESSAGE);
            } else {
                model.shutdown();
                JOptionPane.showMessageDialog(view, "Login Failed: " + response.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (StatusRuntimeException ex) {
            if (model != null) {
                model.shutdown();
            }
            String detail = ex.getStatus().getDescription();
            if (detail == null || detail.isBlank()) {
                detail = ex.getMessage();
            }
            JOptionPane.showMessageDialog(
                    view,
                    "Cannot reach server or login failed.\n\n"
                            + detail
                            + "\n\nEnsure WordyServer is running and connection settings are correct.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (NumberFormatException ex) {
            if (model != null) {
                model.shutdown();
            }
            JOptionPane.showMessageDialog(view, "Port must be a valid number.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            if (model != null) {
                model.shutdown();
            }
            JOptionPane.showMessageDialog(view, "Connection Error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

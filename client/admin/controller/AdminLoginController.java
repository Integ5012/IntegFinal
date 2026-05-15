package com.wordy.client.admin.controller;

import com.wordy.client.admin.model.AdminModel;
import com.wordy.client.admin.view.AdminDashboardView;
import com.wordy.client.admin.view.AdminLoginView;
import com.wordy.grpc.LoginResponse;
import io.grpc.StatusRuntimeException;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AdminLoginController {
    private final AdminLoginView view;
    private final AdminModel model;

    public AdminLoginController(AdminLoginView view, AdminModel model) {
        this.view = view;
        this.model = model;

        // Bind action listener directly to your loginButton
        this.view.loginButton.addActionListener(this::handleLogin);
    }

    private void handleLogin(ActionEvent e) {
        String username = view.usernameField.getText().trim();
        String password = new String(view.passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LoginResponse response = model.login(username, password);

            // Validate response and role
            if (response.getSuccess() && "ADMIN".equals(response.getRole())) {
                view.dispose(); // Close login window

                // Open dashboard
                AdminDashboardView dashboardView = new AdminDashboardView();
                new com.wordy.client.admin.controller.AdminDashboardController(dashboardView, model);

            } else if (response.getSuccess()) {
                JOptionPane.showMessageDialog(view, "Access Denied: You do not have admin privileges.", "Unauthorized", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Login Failed: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (StatusRuntimeException ex) {
            String detail = ex.getStatus().getDescription();
            if (detail == null || detail.isBlank()) {
                detail = ex.getMessage();
            }
            JOptionPane.showMessageDialog(
                    view,
                    "Cannot reach server or login failed.\n\n"
                            + detail
                            + "\n\nEnsure WordyServer is running (check .wordy-grpc-port in the project folder for the port).\n"
                            + "Default admin login: username admin, password 1234 (after running wordy.sql).",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Connection Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
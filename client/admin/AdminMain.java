package com.wordy.client.admin;

import com.wordy.client.admin.controller.AdminLoginController;
import com.wordy.client.admin.model.AdminModel;
import com.wordy.client.admin.view.AdminLoginView;

import javax.swing.SwingUtilities;

public final class AdminMain {

    private AdminMain() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminLoginView view = new AdminLoginView();
            AdminModel model = new AdminModel();
            new AdminLoginController(view, model);
        });
    }
}

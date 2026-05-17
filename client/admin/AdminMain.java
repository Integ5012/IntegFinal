package com.wordy.client.admin;

import com.wordy.client.admin.controller.AdminLoginController;
import com.wordy.client.admin.view.AdminLoginView;

import javax.swing.SwingUtilities;

public final class AdminMain {

    private AdminMain() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminMain::showLogin);
    }

    static void showLogin() {
        AdminLoginView view = new AdminLoginView();
        new AdminLoginController(view);
    }
}

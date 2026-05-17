package com.wordy.client.player;

import com.wordy.client.common.UiTheme;
import com.wordy.client.player.view.LoginView;

import javax.swing.SwingUtilities;

public final class PlayerMain {

    private PlayerMain() {
    }

    public static void main(String[] args) {
        UiTheme.install();
        SwingUtilities.invokeLater(PlayerMain::showLogin);
    }

    public static void showLogin() {
        LoginView view = new LoginView();
        new com.wordy.client.player.controller.LoginController(view, PlayerMain::showLogin);
        view.setVisible(true);
    }
}

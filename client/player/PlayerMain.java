package com.wordy.client.player;

import com.wordy.client.player.view.LoginView;

import javax.swing.SwingUtilities;

public final class PlayerMain {

    private PlayerMain() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView view = new LoginView();
            view.setVisible(true);
        });
    }
}

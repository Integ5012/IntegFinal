package com.wordy.client.admin.controller;

import com.wordy.client.admin.service.AdminPlayerService;
import com.wordy.client.common.UiTheme;
import com.wordy.client.admin.view.AdminDashboardView;
import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.Player;
import com.wordy.grpc.SearchPlayerResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;

public class AdminDashboardController {

    private final AdminDashboardView view;
    private final AdminPlayerService playerService;
    private final Runnable onLogout;
    private final Timer refreshTimer;

    public AdminDashboardController(
            AdminDashboardView view,
            AdminPlayerService playerService,
            Runnable onLogout) {
        this.view = view;
        this.playerService = playerService;
        this.onLogout = onLogout;

        this.view.searchBtn.addActionListener(this::handleSearch);
        this.view.refreshBtn.addActionListener(this::handleSearch);
        this.view.addBtn.addActionListener(this::handleAdd);
        this.view.editBtn.addActionListener(this::handleEdit);
        this.view.deleteBtn.addActionListener(this::handleDelete);
        this.view.saveConfigBtn.addActionListener(this::handleSaveConfig);
        this.view.logoutBtn.addActionListener(e -> handleLogout());

        refreshTimer = new Timer(5000, e -> loadTableData(view.searchField.getText().trim()));
        refreshTimer.start();

        loadTableData("");
        loadConfig();
    }

    private void handleLogout() {
        refreshTimer.stop();
        try {
            playerService.logout();
        } catch (Exception ignored) {
            // return to login anyway
        }
        playerService.close();
        view.dispose();
        onLogout.run();
    }

    private void loadConfig() {
        try {
            var config = playerService.getGameConfig();
            view.waitTimeField.setText(String.valueOf(config.getWaitTime()));
            view.roundTimeField.setText(String.valueOf(config.getRoundTime()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Could not load time configuration: " + ex.getMessage(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleSearch(ActionEvent e) {
        loadTableData(view.searchField.getText().trim());
    }

    private void loadTableData(String keyword) {
        try {
            SearchPlayerResponse response = playerService.searchPlayers(keyword);
            DefaultTableModel tableModel = view.tableModel;
            tableModel.setRowCount(0);

            for (Player p : response.getPlayersList()) {
                tableModel.addRow(new Object[]{
                        p.getId(),
                        p.getUsername(),
                        p.getWins(),
                        p.getOnline() ? "Yes" : "No",
                        p.getInGame() ? "Yes" : "No",
                        p.getInQueue() ? "Yes" : "No",
                        formatStatus(p)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error loading players: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String formatStatus(Player p) {
        if (p.getInGame()) {
            return "In game";
        }
        if (p.getInQueue()) {
            return "Queued (next match)";
        }
        if (p.getOnline()) {
            return "Online";
        }
        return "Offline";
    }

    private void handleAdd(ActionEvent e) {
        JTextField usernameField = new JTextField();
        UiTheme.styleTextField(usernameField);
        Object[] inputs = {"Username:", usernameField};

        int option = JOptionPane.showConfirmDialog(view, inputs, "Add New Player",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Username is required.");
                return;
            }
            try {
                BasicResponse response = playerService.createPlayer(username);

                if (response.getSuccess()) {
                    loadTableData(view.searchField.getText().trim());
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to create player: " + response.getMessage());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
            }
        }
    }

    private void handleEdit(ActionEvent e) {
        int selectedRow = view.playerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Please select a player from the table to edit.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) view.tableModel.getValueAt(selectedRow, 0);
        String currentUsername = (String) view.tableModel.getValueAt(selectedRow, 1);
        int currentWins = (int) view.tableModel.getValueAt(selectedRow, 2);

        JTextField usernameField = new JTextField(currentUsername);
        JPasswordField passwordField = new JPasswordField();
        JTextField winsField = new JTextField(String.valueOf(currentWins));

        Object[] inputs = {
                "Username:", usernameField,
                "New Password (leave blank if unchanged):", passwordField,
                "Wins:", winsField
        };

        int option = JOptionPane.showConfirmDialog(view, inputs, "Edit Player", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                BasicResponse response = playerService.updatePlayer(
                        id,
                        usernameField.getText().trim(),
                        new String(passwordField.getPassword()),
                        Integer.parseInt(winsField.getText().trim()));

                if (response.getSuccess()) {
                    loadTableData(view.searchField.getText().trim());
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to update: " + response.getMessage());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Wins must be a valid whole number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
            }
        }
    }

    private void handleDelete(ActionEvent e) {
        int selectedRow = view.playerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Please select a player from the table to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) view.tableModel.getValueAt(selectedRow, 0);
        String username = (String) view.tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete '" + username + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                BasicResponse response = playerService.deletePlayer(id);
                if (response.getSuccess()) {
                    loadTableData(view.searchField.getText().trim());
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to delete: " + response.getMessage());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
            }
        }
    }

    private void handleSaveConfig(ActionEvent e) {
        try {
            int waitTime = Integer.parseInt(view.waitTimeField.getText().trim());
            int roundTime = Integer.parseInt(view.roundTimeField.getText().trim());

            if (waitTime <= 0 || roundTime <= 0) {
                JOptionPane.showMessageDialog(view, "Times must be greater than 0.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BasicResponse response = playerService.updateConfig(waitTime, roundTime);

            if (response.getSuccess()) {
                JOptionPane.showMessageDialog(view, "Game configurations updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Failed to update configs: " + response.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Please enter valid numbers for the timers.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

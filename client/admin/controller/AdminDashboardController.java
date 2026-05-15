package com.wordy.client.admin.controller;

import com.wordy.client.admin.model.AdminModel;
import com.wordy.client.admin.view.AdminDashboardView;
import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.Player;
import com.wordy.grpc.SearchPlayerResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;

public class AdminDashboardController {
    private final AdminDashboardView view;
    private final AdminModel model;

    public AdminDashboardController(AdminDashboardView view, AdminModel model) {
        this.view = view;
        this.model = model;

        // Bind Action Listeners to the components defined in AdminDashboardView
        this.view.searchBtn.addActionListener(this::handleSearch);
        this.view.addBtn.addActionListener(this::handleAdd);
        this.view.editBtn.addActionListener(this::handleEdit);
        this.view.deleteBtn.addActionListener(this::handleDelete);
        this.view.saveConfigBtn.addActionListener(this::handleSaveConfig);

        loadTableData("");
        loadConfig();
    }

    private void loadConfig() {
        try {
            var config = model.getGameConfig();
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
        String keyword = view.searchField.getText().trim();
        loadTableData(keyword);
    }

    private void loadTableData(String keyword) {
        try {
            SearchPlayerResponse response = model.searchPlayer(keyword);
            DefaultTableModel tableModel = view.tableModel;
            tableModel.setRowCount(0); // Clear the table

            // Populate table rows with data from gRPC response
            for (Player p : response.getPlayersList()) {
                tableModel.addRow(new Object[]{p.getId(), p.getUsername(), p.getWins()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error loading players: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdd(ActionEvent e) {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] inputs = { "Username:", usernameField, "Password:", passwordField };

        int option = JOptionPane.showConfirmDialog(view, inputs, "Add New Player", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                BasicResponse response = model.createPlayer(
                        usernameField.getText().trim(),
                        new String(passwordField.getPassword())
                );

                if (response.getSuccess()) {
                    loadTableData(view.searchField.getText().trim()); // Refresh table
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
            JOptionPane.showMessageDialog(view, "Please select a player from the table to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get current values from the selected JTable row
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
                BasicResponse response = model.updatePlayer(
                        id,
                        usernameField.getText().trim(),
                        new String(passwordField.getPassword()),
                        Integer.parseInt(winsField.getText().trim())
                );

                if (response.getSuccess()) {
                    loadTableData(view.searchField.getText().trim()); // Refresh table
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
            JOptionPane.showMessageDialog(view, "Please select a player from the table to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) view.tableModel.getValueAt(selectedRow, 0);
        String username = (String) view.tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to delete '" + username + "'?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                BasicResponse response = model.deletePlayer(id);
                if (response.getSuccess()) {
                    loadTableData(view.searchField.getText().trim()); // Refresh table
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
                JOptionPane.showMessageDialog(view, "Times must be greater than 0.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BasicResponse response = model.updateConfig(waitTime, roundTime);

            if (response.getSuccess()) {
                JOptionPane.showMessageDialog(view, "Game configurations updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Failed to update configs: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Please enter valid numbers for the timers.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
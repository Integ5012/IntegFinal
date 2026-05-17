package com.wordy.client.admin.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminDashboardView extends JFrame {

    public JTable playerTable;
    public DefaultTableModel tableModel;

    public JButton searchBtn, refreshBtn, addBtn, editBtn, deleteBtn, logoutBtn;
    public JTextField searchField;
    public JLabel statusLabel;

    public JTextField waitTimeField;
    public JTextField roundTimeField;
    public JButton saveConfigBtn;

    private final Color bg = new Color(46, 51, 66);
    private final Color card = new Color(35, 37, 45);
    private final Color accent = new Color(99, 102, 241);
    private final Color text = Color.WHITE;

    public DefaultTableModel model;
    public JTable table;

    public AdminDashboardView() {
        setTitle("Wordy Admin Dashboard");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(card);
        panel.setPreferredSize(new Dimension(230, 0));
        panel.setBorder(new EmptyBorder(20, 15, 20, 15));

        // Dashboard and Config
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(card);

        // DASHBOARD TITLE
        JLabel dashboardLabel = new JLabel("Dashboard");
        dashboardLabel.setForeground(text);
        dashboardLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dashboardLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topContainer.add(dashboardLabel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        //CONFIG PANEL
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(card);
        configPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        configPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(text),
                "Time Configuration",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                text
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // WAITING TIME
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel waitLabel = new JLabel("Wait (s):");
        waitLabel.setForeground(text);
        configPanel.add(waitLabel, gbc);

        gbc.gridx = 1;
        waitTimeField = new JTextField("10");
        waitTimeField.setPreferredSize(new Dimension(60, 28));
        configPanel.add(waitTimeField, gbc);

        // ROUND TIME
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel roundLabel = new JLabel("Round (s):");
        roundLabel.setForeground(text);
        configPanel.add(roundLabel, gbc);

        gbc.gridx = 1;
        roundTimeField = new JTextField("30");
        configPanel.add(roundTimeField, gbc);

        // SAVE BUTTON
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);

        saveConfigBtn = createButton("Save");
        configPanel.add(saveConfigBtn, gbc);

        topContainer.add(configPanel);

        panel.add(topContainer, BorderLayout.NORTH);

        logoutBtn = createMenuButton("Logout");
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(logoutBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(this.text);
        btn.setBackground(card);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(accent);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(card);
            }
        });

        return btn;
    }

    // MAIN PANEL
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(createHeader(), BorderLayout.NORTH);
        panel.add(createTableSection(), BorderLayout.CENTER);
        panel.add(createButtons(), BorderLayout.SOUTH);

        return panel;
    }

    // HEADER
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);

        JLabel title = new JLabel("Player Management");
        title.setForeground(text);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        searchField = new JTextField(15);
        searchBtn = createButton("Search");
        refreshBtn = createButton("Refresh");

        JPanel right = new JPanel();
        right.setBackground(bg);
        right.add(searchField);
        right.add(searchBtn);
        right.add(refreshBtn);

        panel.add(title, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    // TABLE
    private JScrollPane createTableSection() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Username", "Wins", "Online", "In game", "Queued", "Status"}, 0);
        playerTable = new JTable(tableModel);

        playerTable.setRowHeight(28);
        playerTable.setBackground(card);
        playerTable.setForeground(text);
        playerTable.setGridColor(Color.GRAY);
        playerTable.setSelectionBackground(accent);

        JScrollPane scroll = new JScrollPane(playerTable);
        scroll.getViewport().setBackground(card);

        return scroll;
    }

    // BUTTONS
    private JPanel createButtons() {
        JPanel panel = new JPanel();
        panel.setBackground(bg);

        addBtn = createButton("Add");
        editBtn = createButton("Edit");
        deleteBtn = createButton("Delete");

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        return panel;
    }

    // BUTTON STYLE
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(accent);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }
}
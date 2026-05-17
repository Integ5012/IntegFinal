package com.wordy.client.admin.view;

import com.wordy.client.common.UiTheme;

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

    public DefaultTableModel model;
    public JTable table;

    public AdminDashboardView() {
        setTitle("WORDY — Admin Dashboard");
        setSize(1150, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UiTheme.styleRoot(this);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UiTheme.CARD);
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(new EmptyBorder(22, 18, 22, 18));

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UiTheme.CARD);
        topContainer.setOpaque(true);

        JLabel brand = UiTheme.titleLabel("WORDY");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel dashboardLabel = UiTheme.fieldLabel("Admin dashboard");
        dashboardLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        topContainer.add(brand);
        topContainer.add(dashboardLabel);

        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(UiTheme.CARD_ALT);
        configPanel.setBorder(UiTheme.sectionBorder("Time configuration"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        configPanel.add(UiTheme.fieldLabel("Wait (s)"), gbc);

        gbc.gridx = 1;
        waitTimeField = new JTextField("10");
        UiTheme.styleTextField(waitTimeField);
        configPanel.add(waitTimeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        configPanel.add(UiTheme.fieldLabel("Round (s)"), gbc);

        gbc.gridx = 1;
        roundTimeField = new JTextField("30");
        UiTheme.styleTextField(roundTimeField);
        configPanel.add(roundTimeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 4, 8);
        saveConfigBtn = createButton("Save settings");
        configPanel.add(saveConfigBtn, gbc);

        topContainer.add(configPanel);

        panel.add(topContainer, BorderLayout.NORTH);

        logoutBtn = new JButton("Logout");
        UiTheme.styleDangerButton(logoutBtn);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        panel.add(logoutBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(UiTheme.BG);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        panel.add(createHeader(), BorderLayout.NORTH);

        JPanel tableCard = UiTheme.cardPanel(new BorderLayout());
        tableCard.add(UiTheme.fieldLabel("Players"), BorderLayout.NORTH);
        tableCard.add(createTableSection(), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(createButtons(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(UiTheme.BG);
        panel.setBorder(new EmptyBorder(0, 0, 8, 0));

        panel.add(UiTheme.titleLabel("Player management"), BorderLayout.WEST);

        searchField = new JTextField(16);
        UiTheme.styleTextField(searchField);
        searchBtn = createButton("Search");
        refreshBtn = createButton("Refresh");

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(searchField);
        right.add(searchBtn);
        right.add(refreshBtn);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane createTableSection() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Username", "Wins", "Online", "In game", "Queued", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        playerTable = new JTable(tableModel);
        UiTheme.styleTable(playerTable);

        JScrollPane scroll = UiTheme.styleScrollPane(playerTable);
        scroll.setPreferredSize(new Dimension(0, 380));
        return scroll;
    }

    private JPanel createButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(UiTheme.BG);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        addBtn = createButton("Add player");
        editBtn = createButton("Edit");
        deleteBtn = new JButton("Delete");
        UiTheme.styleDangerButton(deleteBtn);

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        return panel;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        UiTheme.stylePrimaryButton(btn);
        return btn;
    }
}

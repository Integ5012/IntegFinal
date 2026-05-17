package com.wordy.client.common;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Swing on Windows ignores custom button colors when using the system Look &amp; Feel.
 * We use Basic UI delegates so foreground/background colors are always applied.
 */
public final class UiTheme {

    public static final Color BG = new Color(46, 51, 66);
    public static final Color CARD = new Color(35, 37, 45);
    public static final Color CARD_ALT = new Color(42, 45, 56);
    public static final Color ACCENT = new Color(99, 102, 241);
    public static final Color TEXT = Color.WHITE;
    public static final Color TEXT_ON_DARK = new Color(241, 245, 249);
    public static final Color TEXT_MUTED = new Color(156, 163, 175);
    public static final Color SECTION_HEADING = new Color(199, 210, 254);
    public static final Color BORDER = new Color(75, 80, 96);
    public static final Color LETTERS_BG = new Color(30, 33, 42);
    public static final Color LETTERS_FG = new Color(167, 243, 208);
    public static final Color LOG_BG = new Color(28, 30, 38);
    public static final Color DANGER = new Color(220, 53, 69);
    public static final Color SUCCESS = new Color(34, 197, 94);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_LETTERS = new Font("Segoe UI", Font.BOLD, 34);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 13);

    private UiTheme() {
    }

    /** Call once before creating any Swing windows (e.g. in main). */
    public static void install() {
        // Do not use system L&F — it paints white buttons and ignores our colors on Windows.
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // keep default
        }
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());

        Color panelBg = CARD;
        UIManager.put("Panel.background", panelBg);
        UIManager.put("Viewport.background", panelBg);
        UIManager.put("ScrollPane.background", panelBg);
        UIManager.put("ScrollBar.track", CARD_ALT);
        UIManager.put("Label.background", panelBg);
        UIManager.put("Label.foreground", TEXT_ON_DARK);
        UIManager.put("Table.background", CARD);
        UIManager.put("Table.foreground", TEXT_ON_DARK);
        UIManager.put("Table.selectionBackground", ACCENT);
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("TableHeader.background", CARD_ALT);
        UIManager.put("TableHeader.foreground", TEXT_ON_DARK);
    }

    public static void styleRoot(JFrame frame) {
        frame.getContentPane().setBackground(BG);
    }

    public static JPanel cardPanel(LayoutManager layout) {
        JPanel panel = layout == null ? new JPanel() : new JPanel(layout);
        panel.setBackground(CARD);
        panel.setOpaque(true);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(20, 22, 20, 22)
        ));
        return panel;
    }

    public static Border sectionBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(BORDER, 1, true),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONT_LABEL,
                TEXT
        );
        border.setTitlePosition(TitledBorder.ABOVE_TOP);
        return new CompoundBorder(border, new EmptyBorder(8, 8, 8, 8));
    }

    public static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT);
        label.setOpaque(false);
        return label;
    }

    public static JLabel subtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_MUTED);
        label.setOpaque(false);
        return label;
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_ON_DARK);
        label.setOpaque(false);
        return label;
    }

    public static JLabel sectionHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(SECTION_HEADING);
        label.setOpaque(true);
        label.setBackground(CARD);
        label.setBorder(new EmptyBorder(0, 0, 6, 0));
        return label;
    }

    public static void applyDarkSurface(JComponent component) {
        component.setOpaque(true);
        component.setBackground(CARD);
        component.setForeground(TEXT_ON_DARK);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setOpaque(true);
        field.setUI(new BasicTextFieldUI());
        field.setBackground(CARD_ALT);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setSelectedTextColor(TEXT);
        field.setSelectionColor(ACCENT);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    public static void stylePasswordField(JPasswordField field) {
        styleTextField(field);
    }

    public static void styleTextArea(JTextArea area) {
        area.setFont(FONT_MONO);
        area.setOpaque(true);
        area.setBackground(LOG_BG);
        area.setForeground(new Color(226, 232, 240));
        area.setCaretColor(TEXT);
        area.setBorder(new EmptyBorder(12, 14, 12, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    public static JScrollPane styleScrollPane(JComponent view) {
        if (!view.isOpaque()) {
            applyDarkSurface(view);
        }
        JScrollPane scroll = new JScrollPane(view);
        scroll.setOpaque(true);
        scroll.setBackground(CARD);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        return scroll;
    }

    public static void stylePrimaryButton(JButton button) {
        styleButton(button, ACCENT, TEXT);
    }

    public static void styleSecondaryButton(JButton button) {
        styleButton(button, CARD_ALT, TEXT);
    }

    public static void styleSuccessButton(JButton button) {
        styleButton(button, SUCCESS, Color.BLACK);
    }

    public static void styleDangerButton(JButton button) {
        styleButton(button, DANGER, TEXT);
    }

    private static void styleButton(JButton button, Color bg, Color fg) {
        button.setFont(FONT_BUTTON);
        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setBorder(new CompoundBorder(
                new LineBorder(darker(bg), 1, true),
                new EmptyBorder(10, 18, 10, 18)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color hover = brighter(bg);
        Color normal = bg;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hover);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(normal);
            }
        });
    }

    private static Color darker(Color color) {
        return new Color(
                Math.max(0, color.getRed() - 25),
                Math.max(0, color.getGreen() - 25),
                Math.max(0, color.getBlue() - 25));
    }

    private static Color brighter(Color color) {
        return new Color(
                Math.min(255, color.getRed() + 20),
                Math.min(255, color.getGreen() + 20),
                Math.min(255, color.getBlue() + 20));
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setOpaque(true);
        table.setRowHeight(32);
        table.setBackground(CARD);
        table.setForeground(TEXT_ON_DARK);
        table.setGridColor(BORDER);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(TEXT);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        TableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    component.setBackground(ACCENT);
                    component.setForeground(TEXT);
                } else {
                    component.setBackground(row % 2 == 0 ? CARD : CARD_ALT);
                    component.setForeground(TEXT_ON_DARK);
                }
                if (component instanceof JComponent jc) {
                    jc.setOpaque(true);
                }
                return component;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);
        table.setDefaultRenderer(Number.class, cellRenderer);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_LABEL);
        header.setUI(new BasicTableHeaderUI());
        header.setOpaque(true);
        header.setBackground(CARD_ALT);
        header.setForeground(TEXT_ON_DARK);
        header.setBorder(new LineBorder(BORDER));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                component.setBackground(CARD_ALT);
                component.setForeground(TEXT_ON_DARK);
                if (component instanceof JComponent jc) {
                    jc.setOpaque(true);
                    jc.setBorder(new EmptyBorder(6, 10, 6, 10));
                }
                return component;
            }
        });
    }

    public static JPanel lettersPanel(JLabel lettersLabel) {
        lettersLabel.setFont(FONT_LETTERS);
        lettersLabel.setForeground(LETTERS_FG);
        lettersLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lettersLabel.setOpaque(true);
        lettersLabel.setBackground(LETTERS_BG);
        lettersLabel.setBorder(new CompoundBorder(
                new LineBorder(ACCENT, 2, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setOpaque(true);
        wrapper.setBorder(new EmptyBorder(4, 0, 0, 0));
        wrapper.add(lettersLabel, BorderLayout.CENTER);
        return wrapper;
    }
}

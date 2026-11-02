package traceit.ui.components;

import traceit.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StatCard extends JPanel {

    private final JLabel valueLabel;
    private final JLabel titleLabel;
    private final JLabel subLabel;
    private final Color accentColor;
    private Runnable clickAction;

    public StatCard(String title, String initialValue, String subtitle, Color accentColor) {
        this.accentColor = accentColor;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_CARD);
        setBorder(new EmptyBorder(16, 18, 16, 18));
        setPreferredSize(new Dimension(170, 105));

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        titleLabel.setForeground(UITheme.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(UITheme.FONT_STAT_NUM);
        valueLabel.setForeground(UITheme.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        subLabel = new JLabel(subtitle);
        subLabel.setFont(UITheme.FONT_SMALL);
        subLabel.setForeground(UITheme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalStrut(2));
        contentPanel.add(subLabel);

        add(contentPanel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickAction != null) {
                    clickAction.run();
                }
            }
        });
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String sub) {
        subLabel.setText(sub);
    }

    public void setClickAction(Runnable action) {
        this.clickAction = action;
        if (action != null) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntialiasing(g2);

        // Card background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        // Card border
        g2.setColor(UITheme.BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        // Left accent bar
        if (accentColor != null) {
            g2.setColor(accentColor);
            g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);
        }

        g2.dispose();
    }
}


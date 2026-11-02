package traceit.ui.components;

import traceit.model.ItemStatus;
import traceit.model.MatchLevel;
import traceit.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BadgeLabel extends JLabel {

    private Color backgroundColor;
    private Color borderColor;
    private int cornerRadius = 12;

    public BadgeLabel(String text, Color fgColor, Color bgColor) {
        this(text, fgColor, bgColor, null);
    }

    public BadgeLabel(String text, Color fgColor, Color bgColor, Color borderColor) {
        super(text, SwingConstants.CENTER);
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        setForeground(fgColor);
        setFont(UITheme.FONT_SMALL_BOLD);
        setOpaque(false);
        setBorder(new EmptyBorder(3, 10, 3, 10));
    }

    public void setColors(Color fg, Color bg) {
        setForeground(fg);
        this.backgroundColor = bg;
        repaint();
    }

    public static BadgeLabel forStatus(ItemStatus status) {
        if (status == null) status = ItemStatus.LOST;
        switch (status) {
            case RETURNED:
                return new BadgeLabel(status.getDisplayName(), UITheme.SUCCESS_TEXT, UITheme.SUCCESS_BG);
            case POSSIBLE_MATCH:
                return new BadgeLabel(status.getDisplayName(), UITheme.WARNING_TEXT, UITheme.WARNING_BG);
            case VERIFIED:
                return new BadgeLabel(status.getDisplayName(), UITheme.PURPLE_TEXT, UITheme.PURPLE_BG);
            case FOUND:
                return new BadgeLabel(status.getDisplayName(), UITheme.TEAL_TEXT, UITheme.TEAL_BG);
            case CLOSED:
                return new BadgeLabel(status.getDisplayName(), UITheme.TEXT_SECONDARY, UITheme.BG_CARD_ALT);
            case LOST:
            default:
                return new BadgeLabel(status.getDisplayName(), UITheme.INFO_TEXT, UITheme.INFO_BG);
        }
    }

    public static BadgeLabel forMatchLevel(MatchLevel level) {
        if (level == null) level = MatchLevel.UNLIKELY_MATCH;
        switch (level) {
            case VERY_STRONG_MATCH:
                return new BadgeLabel(level.getLabel(), UITheme.SUCCESS_TEXT, UITheme.SUCCESS_BG);
            case STRONG_MATCH:
                return new BadgeLabel(level.getLabel(), UITheme.TEAL_TEXT, UITheme.TEAL_BG);
            case POSSIBLE_MATCH:
                return new BadgeLabel(level.getLabel(), UITheme.WARNING_TEXT, UITheme.WARNING_BG);
            case WEAK_MATCH:
                return new BadgeLabel(level.getLabel(), UITheme.DANGER_TEXT, UITheme.DANGER_BG);
            case UNLIKELY_MATCH:
            default:
                return new BadgeLabel(level.getLabel(), UITheme.TEXT_SECONDARY, UITheme.BG_CARD_ALT);
        }
    }

    public static BadgeLabel forScore(int score) {
        String label = score + " / 100";
        if (score >= 90) {
            return new BadgeLabel(label, UITheme.SUCCESS_TEXT, UITheme.SUCCESS_BG);
        } else if (score >= 75) {
            return new BadgeLabel(label, UITheme.TEAL_TEXT, UITheme.TEAL_BG);
        } else if (score >= 60) {
            return new BadgeLabel(label, UITheme.WARNING_TEXT, UITheme.WARNING_BG);
        } else {
            return new BadgeLabel(label, UITheme.DANGER_TEXT, UITheme.DANGER_BG);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntialiasing(g2);

        if (backgroundColor != null) {
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}


package traceit.ui.theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UITheme {

    // Color Palette
    public static final Color PRIMARY = new Color(37, 99, 235);         // Royal Blue #2563EB
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);    // Blue 700 #1D4ED8
    public static final Color PRIMARY_LIGHT = new Color(239, 246, 255); // Blue 50 #EFF6FF
    public static final Color PRIMARY_HOVER = new Color(59, 130, 246);  // Blue 500 #3B82F6

    public static final Color BG_MAIN = new Color(248, 250, 252);       // Slate 50 #F8FAFC
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_CARD_ALT = new Color(241, 245, 249);   // Slate 100 #F1F5F9

    public static final Color SIDEBAR_BG = new Color(15, 23, 42);       // Slate 900 #0F172A
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);    // Slate 800 #1E293B
    public static final Color SIDEBAR_TEXT = new Color(148, 163, 184);  // Slate 400 #94A3B8
    public static final Color SIDEBAR_TEXT_ACTIVE = Color.WHITE;

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);     // Slate 900
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105);  // Slate 600
    public static final Color TEXT_MUTED = new Color(148, 163, 184);    // Slate 400

    public static final Color BORDER = new Color(226, 232, 240);        // Slate 200
    public static final Color BORDER_LIGHT = new Color(241, 245, 249);  // Slate 100
    public static final Color BORDER_FOCUS = new Color(59, 130, 246);   // Blue 500

    // Semantic Status Colors
    public static final Color SUCCESS_TEXT = new Color(4, 120, 87);     // Emerald 700
    public static final Color SUCCESS_BG = new Color(209, 250, 229);    // Emerald 100
    public static final Color SUCCESS_SOLID = new Color(16, 185, 129);  // Emerald 500

    public static final Color WARNING_TEXT = new Color(180, 83, 9);     // Amber 700
    public static final Color WARNING_BG = new Color(254, 243, 199);    // Amber 100
    public static final Color WARNING_SOLID = new Color(245, 158, 11);  // Amber 500

    public static final Color INFO_TEXT = new Color(29, 78, 216);       // Blue 700
    public static final Color INFO_BG = new Color(219, 234, 254);       // Blue 100

    public static final Color DANGER_TEXT = new Color(185, 28, 28);     // Red 700
    public static final Color DANGER_BG = new Color(254, 226, 226);     // Red 100
    public static final Color DANGER_SOLID = new Color(239, 68, 68);    // Red 500

    public static final Color PURPLE_TEXT = new Color(109, 40, 217);    // Violet 700
    public static final Color PURPLE_BG = new Color(237, 233, 254);     // Violet 100

    public static final Color TEAL_TEXT = new Color(15, 118, 110);      // Teal 700
    public static final Color TEAL_BG = new Color(204, 251, 241);       // Teal 100

    // Fonts
    private static final String FONT_FAMILY = getSystemFontFamily();

    public static final Font FONT_HEADER_LARGE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font(FONT_FAMILY, Font.BOLD, 17);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font(FONT_FAMILY, Font.BOLD, 11);
    public static final Font FONT_STAT_NUM = new Font(FONT_FAMILY, Font.BOLD, 28);

    private static String getSystemFontFamily() {
        String[] preferred = {"Segoe UI", "Inter", "San Francisco", "Helvetica Neue", "Arial"};
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        for (String p : preferred) {
            for (String f : fontNames) {
                if (f.equalsIgnoreCase(p)) return f;
            }
        }
        return Font.SANS_SERIF;
    }

    public static void applyAntialiasing(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        }
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    }

    public static Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        );
    }

    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY, Color.WHITE, PRIMARY_DARK);
    }

    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, BG_CARD_ALT, TEXT_PRIMARY, BORDER);
    }

    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS_SOLID, Color.WHITE, new Color(5, 150, 105));
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER_SOLID, Color.WHITE, new Color(220, 38, 38));
    }

    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PRIMARY_LIGHT);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    public static JButton createStyledButton(String text, Color bg, Color fg, Color hoverBg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                applyAntialiasing(g2);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
                btn.repaint();
            }
        });

        return btn;
    }
}


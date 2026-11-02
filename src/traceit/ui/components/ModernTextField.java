package traceit.ui.components;

import traceit.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ModernTextField extends JTextField {

    private String placeholder;
    private boolean isFocused = false;

    public ModernTextField() {
        this("", "");
    }

    public ModernTextField(String placeholder) {
        this("", placeholder);
    }

    public ModernTextField(String text, String placeholder) {
        super(text);
        this.placeholder = placeholder;
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_PRIMARY);
        setCaretColor(UITheme.PRIMARY);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setPreferredSize(new Dimension(getPreferredSize().width, 38));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntialiasing(g2);

        // Background
        g2.setColor(isEnabled() ? UITheme.BG_CARD : UITheme.BG_CARD_ALT);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        // Border
        g2.setColor(isFocused ? UITheme.BORDER_FOCUS : UITheme.BORDER);
        g2.setStroke(new BasicStroke(isFocused ? 1.5f : 1.0f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        super.paintComponent(g);

        // Placeholder
        if ((getText() == null || getText().isEmpty()) && placeholder != null && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 12, y);
        }

        g2.dispose();
    }
}


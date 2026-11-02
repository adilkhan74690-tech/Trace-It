package traceit.ui.components;

import traceit.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ModernTextArea extends JTextArea {

    private boolean isFocused = false;

    public ModernTextArea(int rows, int cols) {
        super(rows, cols);
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_PRIMARY);
        setCaretColor(UITheme.PRIMARY);
        setLineWrap(true);
        setWrapStyleWord(true);
        setBorder(new EmptyBorder(8, 10, 8, 10));

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

    public static JScrollPane createScrollPane(ModernTextArea area) {
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        sp.setBackground(UITheme.BG_CARD);
        return sp;
    }
}


package traceit.ui.components;

import traceit.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModernComboBox<E> extends JComboBox<E> {

    public ModernComboBox(E[] items) {
        super(items);
        init();
    }

    public ModernComboBox(ComboBoxModel<E> model) {
        super(model);
        init();
    }

    public ModernComboBox() {
        super();
        init();
    }

    private void init() {
        setFont(UITheme.FONT_BODY);
        setBackground(UITheme.BG_CARD);
        setForeground(UITheme.TEXT_PRIMARY);
        setPreferredSize(new Dimension(getPreferredSize().width, 38));
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(UITheme.FONT_BODY);
                lbl.setBorder(new EmptyBorder(6, 10, 6, 10));
                if (isSelected) {
                    lbl.setBackground(UITheme.PRIMARY_LIGHT);
                    lbl.setForeground(UITheme.PRIMARY_DARK);
                } else {
                    lbl.setBackground(UITheme.BG_CARD);
                    lbl.setForeground(UITheme.TEXT_PRIMARY);
                }
                return lbl;
            }
        });
    }
}


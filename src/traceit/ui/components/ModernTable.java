package traceit.ui.components;

import traceit.model.ItemStatus;
import traceit.model.MatchLevel;
import traceit.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class ModernTable extends JTable {

    public ModernTable(TableModel model) {
        super(model);
        initStyling();
    }

    public ModernTable() {
        super();
        initStyling();
    }

    private void initStyling() {
        setRowHeight(36);
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_PRIMARY);
        setSelectionBackground(UITheme.PRIMARY_LIGHT);
        setSelectionForeground(UITheme.TEXT_PRIMARY);
        setGridColor(UITheme.BORDER_LIGHT);
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setFillsViewportHeight(true);
        setBackground(UITheme.BG_CARD);

        // Header Styling
        JTableHeader header = getTableHeader();
        header.setFont(UITheme.FONT_BODY_BOLD);
        header.setBackground(UITheme.BG_CARD_ALT);
        header.setForeground(UITheme.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setFont(UITheme.FONT_SMALL_BOLD);
                lbl.setForeground(UITheme.TEXT_SECONDARY);
                lbl.setBackground(UITheme.BG_CARD_ALT);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                        new EmptyBorder(8, 10, 8, 10)
                ));
                return lbl;
            }
        });

        // Default Cell Padding Renderer
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.BG_MAIN);
                }
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                    ((JLabel) c).setFont(UITheme.FONT_BODY);
                }
                return c;
            }
        });

        // Register custom renderers
        setDefaultRenderer(ItemStatus.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                ItemStatus status = (ItemStatus) value;
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? UITheme.BG_CARD : UITheme.BG_MAIN));
                if (status != null) {
                    p.add(BadgeLabel.forStatus(status));
                }
                return p;
            }
        });

        setDefaultRenderer(MatchLevel.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                MatchLevel level = (MatchLevel) value;
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? UITheme.BG_CARD : UITheme.BG_MAIN));
                if (level != null) {
                    p.add(BadgeLabel.forMatchLevel(level));
                }
                return p;
            }
        });
    }

    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);
        return scrollPane;
    }
}


package traceit.ui.components;

import traceit.exception.TraceItException;
import traceit.model.FoundItem;
import traceit.model.Item;
import traceit.model.ItemStatus;
import traceit.model.LostItem;
import traceit.ui.AppContext;
import traceit.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatusUpdateDialog extends JDialog {

    private final Item item;
    private final AppContext context;

    public StatusUpdateDialog(Window owner, Item item, AppContext context) {
        super(owner, "Update Status - " + item.getId(), ModalityType.APPLICATION_MODAL);
        this.item = item;
        this.context = context;
        initComponents();
    }

    private void initComponents() {
        setSize(420, 280);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_CARD);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel titleLbl = new JLabel("Update Item Status");
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel descLbl = new JLabel(item.getName() + " (#" + item.getId() + ")");
        descLbl.setFont(UITheme.FONT_BODY);
        descLbl.setForeground(UITheme.TEXT_SECONDARY);

        JLabel currentStatusLbl = new JLabel("Current Status: " + item.getStatus().getDisplayName());
        currentStatusLbl.setFont(UITheme.FONT_SMALL_BOLD);
        currentStatusLbl.setForeground(UITheme.PRIMARY_DARK);

        JLabel selectLbl = new JLabel("Select New Status:");
        selectLbl.setFont(UITheme.FONT_SMALL_BOLD);
        selectLbl.setForeground(UITheme.TEXT_SECONDARY);

        ModernComboBox<ItemStatus> statusCombo = new ModernComboBox<>(ItemStatus.values());
        statusCombo.setSelectedItem(item.getStatus());

        content.add(titleLbl);
        content.add(Box.createVerticalStrut(4));
        content.add(descLbl);
        content.add(Box.createVerticalStrut(10));
        content.add(currentStatusLbl);
        content.add(Box.createVerticalStrut(16));
        content.add(selectLbl);
        content.add(Box.createVerticalStrut(6));
        content.add(statusCombo);

        add(content, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(UITheme.BG_CARD_ALT);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = UITheme.createPrimaryButton("Update Status");
        saveBtn.addActionListener(e -> {
            ItemStatus newStatus = (ItemStatus) statusCombo.getSelectedItem();
            if (newStatus != null) {
                try {
                    if (item instanceof LostItem) {
                        context.getLostItemService().updateStatus(item.getId(), newStatus);
                    } else if (item instanceof FoundItem) {
                        context.getFoundItemService().updateStatus(item.getId(), newStatus);
                    }
                    context.notifyDataChanged();
                    JOptionPane.showMessageDialog(this,
                            "Status for #" + item.getId() + " updated to " + newStatus.getDisplayName(),
                            "Status Updated",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (TraceItException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error updating status: " + ex.getMessage(),
                            "Update Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        footer.add(cancelBtn);
        footer.add(saveBtn);
        add(footer, BorderLayout.SOUTH);
    }
}


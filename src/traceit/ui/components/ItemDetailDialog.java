package traceit.ui.components;

import traceit.model.FoundItem;
import traceit.model.Item;
import traceit.model.LostItem;
import traceit.model.User;
import traceit.ui.AppContext;
import traceit.ui.theme.UITheme;
import traceit.util.DateTimeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ItemDetailDialog extends JDialog {

    private final Item item;
    private final AppContext context;

    public ItemDetailDialog(Window owner, Item item, AppContext context) {
        super(owner, "Item Details - " + item.getId(), ModalityType.APPLICATION_MODAL);
        this.item = item;
        this.context = context;
        initComponents();
    }

    private void initComponents() {
        setSize(560, 620);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_CARD);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.BG_CARD_ALT);
        headerPanel.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(item.getName());
        titleLabel.setFont(UITheme.FONT_HEADER);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subLabel = new JLabel(item.getReportType() + " ITEM • #" + item.getId() + " • " + item.getCategory().getDisplayName());
        subLabel.setFont(UITheme.FONT_SMALL_BOLD);
        subLabel.setForeground(UITheme.TEXT_SECONDARY);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subLabel);

        JPanel badgesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        badgesPanel.setOpaque(false);
        badgesPanel.add(BadgeLabel.forStatus(item.getStatus()));

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(badgesPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main Details Body
        JPanel bodyPanel = new JPanel();
        bodyPanel.setOpaque(false);
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        addSectionHeader(bodyPanel, "Item Information");
        JPanel itemGrid = new JPanel(new GridLayout(0, 2, 12, 10));
        itemGrid.setOpaque(false);
        addField(itemGrid, "Color", item.getColor());
        addField(itemGrid, "Brand", item.getBrand());
        addField(itemGrid, "Location", item.getLocation() != null ? item.getLocation().getRawLocation() : "N/A");
        addField(itemGrid, "Date & Time", DateTimeUtils.formatDisplay(item.getDateTime()));
        addField(itemGrid, "Distinctive Features", item.getDistinctiveFeatures());
        bodyPanel.add(itemGrid);
        bodyPanel.add(Box.createVerticalStrut(12));

        addFieldFullWidth(bodyPanel, "Description", item.getDescription());
        bodyPanel.add(Box.createVerticalStrut(14));

        if (item instanceof LostItem) {
            LostItem lost = (LostItem) item;
            addSectionHeader(bodyPanel, "Lost Report Details");
            JPanel lostGrid = new JPanel(new GridLayout(0, 2, 12, 10));
            lostGrid.setOpaque(false);
            addField(lostGrid, "Reward Offered", "$" + String.format("%.2f", lost.getRewardAmount()));
            addField(lostGrid, "Contact Preference", lost.getContactPreference());
            bodyPanel.add(lostGrid);
            bodyPanel.add(Box.createVerticalStrut(14));
        } else if (item instanceof FoundItem) {
            FoundItem found = (FoundItem) item;
            addSectionHeader(bodyPanel, "Custody & Verification");
            JPanel foundGrid = new JPanel(new GridLayout(0, 2, 12, 10));
            foundGrid.setOpaque(false);
            addField(foundGrid, "Storage / Custody", found.getStorageLocation());
            addField(foundGrid, "Verification Question", found.getVerificationQuestion());
            bodyPanel.add(foundGrid);
            bodyPanel.add(Box.createVerticalStrut(14));
        }

        // Reporter Details
        User reporter = item.getReporter();
        if (reporter != null) {
            addSectionHeader(bodyPanel, item instanceof LostItem ? "Owner / Reporter Contact" : "Finder Contact");
            JPanel contactGrid = new JPanel(new GridLayout(0, 2, 12, 10));
            contactGrid.setOpaque(false);
            addField(contactGrid, "Name", reporter.getName());
            addField(contactGrid, "Role", reporter.getRole());
            addField(contactGrid, "Email", reporter.getEmail());
            addField(contactGrid, "Phone", reporter.getPhone());
            bodyPanel.add(contactGrid);
        }

        JScrollPane scrollPane = new JScrollPane(bodyPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Actions
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footerPanel.setBackground(UITheme.BG_CARD_ALT);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        if (item instanceof LostItem) {
            JButton matchBtn = UITheme.createPrimaryButton("Run Smart Matching");
            matchBtn.addActionListener(e -> {
                dispose();
                context.navigateToSmartMatching(item.getId());
            });
            footerPanel.add(matchBtn);
        }

        JButton updateBtn = UITheme.createSecondaryButton("Update Status");
        updateBtn.addActionListener(e -> {
            StatusUpdateDialog dlg = new StatusUpdateDialog(this, item, context);
            dlg.setVisible(true);
            // Refresh dialog badges if updated
            dispose();
        });
        footerPanel.add(updateBtn);

        JButton closeBtn = UITheme.createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footerPanel.add(closeBtn);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void addSectionHeader(JPanel parent, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.FONT_SUBTITLE);
        lbl.setForeground(UITheme.PRIMARY_DARK);
        lbl.setBorder(new EmptyBorder(6, 0, 6, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
    }

    private void addField(JPanel parent, String label, String value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_SMALL_BOLD);
        l.setForeground(UITheme.TEXT_MUTED);

        JLabel v = new JLabel((value == null || value.trim().isEmpty()) ? "N/A" : value);
        v.setFont(UITheme.FONT_BODY);
        v.setForeground(UITheme.TEXT_PRIMARY);

        p.add(l);
        p.add(Box.createVerticalStrut(2));
        p.add(v);
        parent.add(p);
    }

    private void addFieldFullWidth(JPanel parent, String label, String value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_SMALL_BOLD);
        l.setForeground(UITheme.TEXT_MUTED);

        JTextArea ta = new JTextArea((value == null || value.trim().isEmpty()) ? "None provided" : value);
        ta.setFont(UITheme.FONT_BODY);
        ta.setForeground(UITheme.TEXT_PRIMARY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setBorder(null);

        p.add(l);
        p.add(Box.createVerticalStrut(2));
        p.add(ta);
        parent.add(p);
    }
}


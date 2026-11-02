package traceit.ui.screens;

import traceit.exception.ItemNotFoundException;
import traceit.exception.TraceItException;
import traceit.model.*;
import traceit.ui.AppContext;
import traceit.ui.components.*;
import traceit.ui.theme.UITheme;
import traceit.util.DateTimeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecoveryManagementScreen extends JPanel implements AppContext.DataChangeListener {

    private final AppContext context;

    // Active Pipeline Table
    private ModernTable pipelineTable;
    private DefaultTableModel pipelineModel;
    private List<Item> pipelineItems = new ArrayList<>();

    // Pair Resolution Card
    private ModernComboBox<LostItemComboItem> cbLost;
    private ModernComboBox<FoundItemComboItem> cbFound;
    private JLabel lblPairScore;
    private BadgeLabel badgePairLevel;

    // Direct Status Override Card
    private ModernComboBox<ItemComboItem> cbAllItems;
    private ModernComboBox<ItemStatus> cbOverrideStatus;

    public RecoveryManagementScreen(AppContext context) {
        this.context = context;
        context.addDataChangeListener(this);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel titleLbl = new JLabel("Recovery & Custody Management");
        titleLbl.setFont(UITheme.FONT_HEADER_LARGE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitleLbl = new JLabel("Manage item verification lifecycles, pair matching reports, and execute owner handovers");
        subtitleLbl.setFont(UITheme.FONT_BODY);
        subtitleLbl.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(titleLbl);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLbl);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(18));

        // Active Pipeline Table Card
        JPanel pipelineCard = new JPanel(new BorderLayout(0, 10));
        pipelineCard.setBackground(UITheme.BG_CARD);
        pipelineCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel pipeHead = new JPanel(new BorderLayout());
        pipeHead.setOpaque(false);

        JLabel pipeTitle = new JLabel("Active Pipeline Cases (Possible & Verified Matches)");
        pipeTitle.setFont(UITheme.FONT_SUBTITLE);
        pipeTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel pipeSub = new JLabel("Items currently undergoing ownership verification or awaiting handover");
        pipeSub.setFont(UITheme.FONT_SMALL);
        pipeSub.setForeground(UITheme.TEXT_MUTED);

        pipeHead.add(pipeTitle, BorderLayout.WEST);
        pipeHead.add(pipeSub, BorderLayout.EAST);
        pipelineCard.add(pipeHead, BorderLayout.NORTH);

        pipelineModel = new DefaultTableModel(
                new Object[]{"Type", "ID", "Name", "Category", "Location / Custody", "Date", "Current Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) return ItemStatus.class;
                return String.class;
            }
        };

        pipelineTable = new ModernTable(pipelineModel);
        pipelineTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        pipelineTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        pipelineTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        pipelineTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        pipelineTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        pipelineTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        pipelineTable.getColumnModel().getColumn(6).setPreferredWidth(140);

        // Type badge renderer
        pipelineTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String type = String.valueOf(value);
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? UITheme.BG_CARD : UITheme.BG_MAIN));
                if ("LOST".equalsIgnoreCase(type)) {
                    p.add(new BadgeLabel("LOST", UITheme.INFO_TEXT, UITheme.INFO_BG));
                } else {
                    p.add(new BadgeLabel("FOUND", UITheme.TEAL_TEXT, UITheme.TEAL_BG));
                }
                return p;
            }
        });

        JScrollPane pipeScroll = ModernTable.createScrollPane(pipelineTable);
        pipeScroll.setPreferredSize(new Dimension(pipeScroll.getPreferredSize().width, 180));
        pipelineCard.add(pipeScroll, BorderLayout.CENTER);

        JPanel pipeActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pipeActions.setOpaque(false);

        JButton btnInspect = UITheme.createOutlineButton("Inspect Selected Item");
        btnInspect.addActionListener(e -> inspectPipelineItem());

        JButton btnChangeStatus = UITheme.createSecondaryButton("Update Selected Status");
        btnChangeStatus.addActionListener(e -> updatePipelineItemStatus());

        pipeActions.add(btnInspect);
        pipeActions.add(btnChangeStatus);
        pipelineCard.add(pipeActions, BorderLayout.SOUTH);

        mainPanel.add(pipelineCard);
        mainPanel.add(Box.createVerticalStrut(18));

        // Pair Resolution Card
        JPanel pairCard = new JPanel(new BorderLayout(0, 14));
        pairCard.setBackground(UITheme.BG_CARD);
        pairCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel pairTitle = new JLabel("Direct Pair Resolution & Custody Handover Workflow");
        pairTitle.setFont(UITheme.FONT_SUBTITLE);
        pairTitle.setForeground(UITheme.TEXT_PRIMARY);
        pairCard.add(pairTitle, BorderLayout.NORTH);

        JPanel pairBody = new JPanel(new GridLayout(1, 2, 16, 0));
        pairBody.setOpaque(false);

        // Selectors Panel
        JPanel selectors = new JPanel(new GridLayout(2, 1, 0, 10));
        selectors.setOpaque(false);

        JPanel lostRow = new JPanel(new BorderLayout(0, 4));
        lostRow.setOpaque(false);
        JLabel lLost = new JLabel("1. Select Lost Item to Link:");
        lLost.setFont(UITheme.FONT_SMALL_BOLD);
        lLost.setForeground(UITheme.TEXT_SECONDARY);
        cbLost = new ModernComboBox<>();
        cbLost.addActionListener(e -> updateLivePairScore());
        lostRow.add(lLost, BorderLayout.NORTH);
        lostRow.add(cbLost, BorderLayout.CENTER);

        JPanel foundRow = new JPanel(new BorderLayout(0, 4));
        foundRow.setOpaque(false);
        JLabel lFound = new JLabel("2. Select Matching Found Item in Custody:");
        lFound.setFont(UITheme.FONT_SMALL_BOLD);
        lFound.setForeground(UITheme.TEXT_SECONDARY);
        cbFound = new ModernComboBox<>();
        cbFound.addActionListener(e -> updateLivePairScore());
        foundRow.add(lFound, BorderLayout.NORTH);
        foundRow.add(cbFound, BorderLayout.CENTER);

        selectors.add(lostRow);
        selectors.add(foundRow);
        pairBody.add(selectors);

        // Live Compatibility Preview Box
        JPanel scorePreview = new JPanel(new BorderLayout());
        scorePreview.setBackground(UITheme.BG_MAIN);
        scorePreview.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel spTop = new JPanel();
        spTop.setOpaque(false);
        spTop.setLayout(new BoxLayout(spTop, BoxLayout.Y_AXIS));

        JLabel spTitle = new JLabel("Pair Compatibility Score:");
        spTitle.setFont(UITheme.FONT_SMALL_BOLD);
        spTitle.setForeground(UITheme.TEXT_SECONDARY);

        lblPairScore = new JLabel("-- / 100");
        lblPairScore.setFont(UITheme.FONT_STAT_NUM);
        lblPairScore.setForeground(UITheme.PRIMARY);

        badgePairLevel = new BadgeLabel("PENDING", UITheme.TEXT_MUTED, UITheme.BG_CARD_ALT);

        spTop.add(spTitle);
        spTop.add(Box.createVerticalStrut(4));
        spTop.add(lblPairScore);
        spTop.add(Box.createVerticalStrut(4));
        spTop.add(badgePairLevel);

        scorePreview.add(spTop, BorderLayout.CENTER);
        pairBody.add(scorePreview);

        pairCard.add(pairBody, BorderLayout.CENTER);

        // Workflow Action Buttons Bar
        JPanel workflowButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        workflowButtons.setOpaque(false);

        JButton btnStep1 = UITheme.createSecondaryButton("Step 1: Mark Possible Match");
        btnStep1.addActionListener(e -> executeMarkPossibleMatch());

        JButton btnStep2 = UITheme.createSecondaryButton("Step 2: Mark Verified Match");
        btnStep2.addActionListener(e -> executeMarkVerifiedMatch());

        JButton btnStep3 = UITheme.createSuccessButton("Step 3: Complete Handover & Return");
        btnStep3.addActionListener(e -> executeCompleteReturn());

        workflowButtons.add(btnStep1);
        workflowButtons.add(btnStep2);
        workflowButtons.add(btnStep3);

        pairCard.add(workflowButtons, BorderLayout.SOUTH);
        mainPanel.add(pairCard);
        mainPanel.add(Box.createVerticalStrut(18));

        // Quick Status Override Section
        JPanel overrideCard = new JPanel(new BorderLayout(0, 10));
        overrideCard.setBackground(UITheme.BG_CARD);
        overrideCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JLabel ovTitle = new JLabel("Direct Status Override Tool");
        ovTitle.setFont(UITheme.FONT_SUBTITLE);
        ovTitle.setForeground(UITheme.TEXT_PRIMARY);
        overrideCard.add(ovTitle, BorderLayout.NORTH);

        JPanel ovGrid = new JPanel(new GridLayout(1, 3, 14, 0));
        ovGrid.setOpaque(false);

        cbAllItems = new ModernComboBox<>();
        cbOverrideStatus = new ModernComboBox<>(ItemStatus.values());

        JButton btnApplyOverride = UITheme.createPrimaryButton("Apply Status Change");
        btnApplyOverride.addActionListener(e -> executeDirectOverride());

        JPanel p1 = new JPanel(new BorderLayout(0, 4));
        p1.setOpaque(false);
        p1.add(new JLabel("Select Any Item:"), BorderLayout.NORTH);
        p1.add(cbAllItems, BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout(0, 4));
        p2.setOpaque(false);
        p2.add(new JLabel("Set Target Status:"), BorderLayout.NORTH);
        p2.add(cbOverrideStatus, BorderLayout.CENTER);

        JPanel p3 = new JPanel(new BorderLayout(0, 4));
        p3.setOpaque(false);
        p3.add(new JLabel(" "), BorderLayout.NORTH);
        p3.add(btnApplyOverride, BorderLayout.CENTER);

        ovGrid.add(p1);
        ovGrid.add(p2);
        ovGrid.add(p3);

        overrideCard.add(ovGrid, BorderLayout.CENTER);
        mainPanel.add(overrideCard);

        JScrollPane outerScroll = new JScrollPane(mainPanel);
        outerScroll.setBorder(null);
        outerScroll.getVerticalScrollBar().setUnitIncrement(14);
        add(outerScroll, BorderLayout.CENTER);
    }

    private void updateLivePairScore() {
        LostItemComboItem l = (LostItemComboItem) cbLost.getSelectedItem();
        FoundItemComboItem f = (FoundItemComboItem) cbFound.getSelectedItem();

        if (l == null || f == null || l.item == null || f.item == null) {
            lblPairScore.setText("-- / 100");
            badgePairLevel.setText("PENDING");
            badgePairLevel.setColors(UITheme.TEXT_MUTED, UITheme.BG_CARD_ALT);
            return;
        }

        try {
            MatchResult mr = context.getRecoveryService().analyzePair(l.item.getId(), f.item.getId());
            lblPairScore.setText(mr.getTotalScore() + " / 100");
            lblPairScore.setForeground(mr.getTotalScore() >= 75 ? UITheme.SUCCESS_TEXT : (mr.getTotalScore() >= 50 ? UITheme.WARNING_TEXT : UITheme.DANGER_TEXT));

            badgePairLevel.setText(mr.getMatchLevel().getLabel());
            switch (mr.getMatchLevel()) {
                case VERY_STRONG_MATCH:
                    badgePairLevel.setColors(UITheme.SUCCESS_TEXT, UITheme.SUCCESS_BG);
                    break;
                case STRONG_MATCH:
                    badgePairLevel.setColors(UITheme.TEAL_TEXT, UITheme.TEAL_BG);
                    break;
                case POSSIBLE_MATCH:
                    badgePairLevel.setColors(UITheme.WARNING_TEXT, UITheme.WARNING_BG);
                    break;
                default:
                    badgePairLevel.setColors(UITheme.DANGER_TEXT, UITheme.DANGER_BG);
                    break;
            }
        } catch (Exception e) {
            lblPairScore.setText("ERR");
        }
    }

    private void executeMarkPossibleMatch() {
        LostItemComboItem l = (LostItemComboItem) cbLost.getSelectedItem();
        FoundItemComboItem f = (FoundItemComboItem) cbFound.getSelectedItem();
        if (l == null || f == null) return;

        try {
            context.getRecoveryService().markAsPossibleMatch(l.item.getId(), f.item.getId());
            context.notifyDataChanged();
            JOptionPane.showMessageDialog(this,
                    "Marked both #" + l.item.getId() + " and #" + f.item.getId() + " as POSSIBLE_MATCH",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (TraceItException e) {
            JOptionPane.showMessageDialog(this, "Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeMarkVerifiedMatch() {
        LostItemComboItem l = (LostItemComboItem) cbLost.getSelectedItem();
        FoundItemComboItem f = (FoundItemComboItem) cbFound.getSelectedItem();
        if (l == null || f == null) return;

        try {
            context.getRecoveryService().markAsVerified(l.item.getId(), f.item.getId());
            context.notifyDataChanged();
            JOptionPane.showMessageDialog(this,
                    "Marked both #" + l.item.getId() + " and #" + f.item.getId() + " as VERIFIED MATCH",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (TraceItException e) {
            JOptionPane.showMessageDialog(this, "Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeCompleteReturn() {
        LostItemComboItem l = (LostItemComboItem) cbLost.getSelectedItem();
        FoundItemComboItem f = (FoundItemComboItem) cbFound.getSelectedItem();
        if (l == null || f == null) return;

        Window win = SwingUtilities.getWindowAncestor(this);
        RecoveryReturnDialog dlg = new RecoveryReturnDialog(win, l.item.getId(), f.item.getId(), context);
        dlg.setVisible(true);
    }

    private void inspectPipelineItem() {
        int sel = pipelineTable.getSelectedRow();
        if (sel >= 0 && sel < pipelineItems.size()) {
            Item item = pipelineItems.get(sel);
            Window win = SwingUtilities.getWindowAncestor(this);
            ItemDetailDialog dlg = new ItemDetailDialog(win, item, context);
            dlg.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item from the pipeline table first.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updatePipelineItemStatus() {
        int sel = pipelineTable.getSelectedRow();
        if (sel >= 0 && sel < pipelineItems.size()) {
            Item item = pipelineItems.get(sel);
            Window win = SwingUtilities.getWindowAncestor(this);
            StatusUpdateDialog dlg = new StatusUpdateDialog(win, item, context);
            dlg.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item from the pipeline table first.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void executeDirectOverride() {
        ItemComboItem it = (ItemComboItem) cbAllItems.getSelectedItem();
        ItemStatus st = (ItemStatus) cbOverrideStatus.getSelectedItem();
        if (it == null || st == null || it.item == null) return;

        try {
            if (it.item instanceof LostItem) {
                context.getLostItemService().updateStatus(it.item.getId(), st);
            } else if (it.item instanceof FoundItem) {
                context.getFoundItemService().updateStatus(it.item.getId(), st);
            }
            context.notifyDataChanged();
            JOptionPane.showMessageDialog(this,
                    "Status for #" + it.item.getId() + " changed to " + st.getDisplayName(),
                    "Status Overridden", JOptionPane.INFORMATION_MESSAGE);
        } catch (TraceItException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onDataChanged() {
        refreshData();
    }

    private void refreshData() {
        // Pipeline items (POSSIBLE_MATCH or VERIFIED)
        pipelineItems.clear();
        pipelineModel.setRowCount(0);

        for (LostItem item : context.getLostItemService().getAllLostItems()) {
            if (item.getStatus() == ItemStatus.POSSIBLE_MATCH || item.getStatus() == ItemStatus.VERIFIED) {
                pipelineItems.add(item);
                pipelineModel.addRow(new Object[]{
                        "LOST",
                        item.getId(),
                        item.getName(),
                        item.getCategory().getDisplayName(),
                        item.getLocation() != null ? item.getLocation().getRawLocation() : "N/A",
                        DateTimeUtils.formatDisplay(item.getDateTime()),
                        item.getStatus()
                });
            }
        }

        for (FoundItem item : context.getFoundItemService().getAllFoundItems()) {
            if (item.getStatus() == ItemStatus.POSSIBLE_MATCH || item.getStatus() == ItemStatus.VERIFIED) {
                pipelineItems.add(item);
                pipelineModel.addRow(new Object[]{
                        "FOUND",
                        item.getId(),
                        item.getName(),
                        item.getCategory().getDisplayName(),
                        item.getStorageLocation(),
                        DateTimeUtils.formatDisplay(item.getDateTime()),
                        item.getStatus()
                });
            }
        }

        // Combos
        cbLost.removeAllItems();
        for (LostItem l : context.getLostItemService().getAllLostItems()) {
            cbLost.addItem(new LostItemComboItem(l));
        }

        cbFound.removeAllItems();
        for (FoundItem f : context.getFoundItemService().getAllFoundItems()) {
            cbFound.addItem(new FoundItemComboItem(f));
        }

        cbAllItems.removeAllItems();
        for (LostItem l : context.getLostItemService().getAllLostItems()) {
            cbAllItems.addItem(new ItemComboItem(l));
        }
        for (FoundItem f : context.getFoundItemService().getAllFoundItems()) {
            cbAllItems.addItem(new ItemComboItem(f));
        }

        updateLivePairScore();
    }

    private static class LostItemComboItem {
        final LostItem item;

        LostItemComboItem(LostItem item) {
            this.item = item;
        }

        @Override
        public String toString() {
            if (item == null) return "None";
            return "#" + item.getId() + " - " + item.getName() + " [" + item.getStatus().getDisplayName() + "]";
        }
    }

    private static class FoundItemComboItem {
        final FoundItem item;

        FoundItemComboItem(FoundItem item) {
            this.item = item;
        }

        @Override
        public String toString() {
            if (item == null) return "None";
            return "#" + item.getId() + " - " + item.getName() + " [" + item.getStatus().getDisplayName() + "]";
        }
    }

    private static class ItemComboItem {
        final Item item;

        ItemComboItem(Item item) {
            this.item = item;
        }

        @Override
        public String toString() {
            if (item == null) return "None";
            return "[" + item.getReportType() + "] #" + item.getId() + " - " + item.getName() + " (" + item.getStatus().getDisplayName() + ")";
        }
    }
}


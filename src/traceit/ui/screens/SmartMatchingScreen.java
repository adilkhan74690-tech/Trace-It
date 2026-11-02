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
import java.util.List;

public class SmartMatchingScreen extends JPanel implements AppContext.DataChangeListener {

    private final AppContext context;

    private ModernComboBox<LostItemComboItem> cbLostItems;
    private JButton btnRunMatch;

    // Target Lost Item Summary Card
    private JLabel lblLostTitle;
    private JLabel lblLostSub;
    private JLabel lblLostLoc;
    private JLabel lblLostDate;
    private JLabel lblLostDesc;
    private BadgeLabel lblLostStatus;

    // Results Table
    private JLabel lblResultsCount;
    private ModernTable resultsTable;
    private DefaultTableModel resultsModel;
    private List<MatchResult> currentMatches;

    // Action Buttons
    private JButton btnViewAnalysis;
    private JButton btnMarkPossible;
    private JButton btnMarkVerified;
    private JButton btnCompleteReturn;

    public SmartMatchingScreen(AppContext context) {
        this.context = context;
        context.addDataChangeListener(this);
        initComponents();
        reloadLostItemsCombo();
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

        JLabel titleLbl = new JLabel("Smart Matching Engine");
        titleLbl.setFont(UITheme.FONT_HEADER_LARGE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitleLbl = new JLabel("Rule-based multi-attribute compatibility scoring across 6 weighted evaluation dimensions");
        subtitleLbl.setFont(UITheme.FONT_BODY);
        subtitleLbl.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(titleLbl);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLbl);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(18));

        // Selection & Summary Card
        JPanel selectCard = new JPanel(new BorderLayout(14, 12));
        selectCard.setBackground(UITheme.BG_CARD);
        selectCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);

        JLabel selectLbl = new JLabel("Select Target Lost Item:");
        selectLbl.setFont(UITheme.FONT_BODY_BOLD);
        selectLbl.setForeground(UITheme.TEXT_PRIMARY);

        cbLostItems = new ModernComboBox<>();
        cbLostItems.addActionListener(e -> updateSelectedLostItemDisplay());

        btnRunMatch = UITheme.createPrimaryButton("⚡ Run Smart Matching");
        btnRunMatch.addActionListener(e -> executeMatching());

        topBar.add(selectLbl, BorderLayout.WEST);
        topBar.add(cbLostItems, BorderLayout.CENTER);
        topBar.add(btnRunMatch, BorderLayout.EAST);

        selectCard.add(topBar, BorderLayout.NORTH);

        // Details summary of chosen lost item
        JPanel summaryBox = new JPanel(new BorderLayout(14, 8));
        summaryBox.setBackground(UITheme.BG_MAIN);
        summaryBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JPanel sumLeft = new JPanel();
        sumLeft.setOpaque(false);
        sumLeft.setLayout(new BoxLayout(sumLeft, BoxLayout.Y_AXIS));

        lblLostTitle = new JLabel("No item selected");
        lblLostTitle.setFont(UITheme.FONT_SUBTITLE);
        lblLostTitle.setForeground(UITheme.TEXT_PRIMARY);

        lblLostSub = new JLabel("");
        lblLostSub.setFont(UITheme.FONT_SMALL_BOLD);
        lblLostSub.setForeground(UITheme.TEXT_SECONDARY);

        lblLostDesc = new JLabel("");
        lblLostDesc.setFont(UITheme.FONT_SMALL);
        lblLostDesc.setForeground(UITheme.TEXT_MUTED);

        sumLeft.add(lblLostTitle);
        sumLeft.add(Box.createVerticalStrut(2));
        sumLeft.add(lblLostSub);
        sumLeft.add(Box.createVerticalStrut(2));
        sumLeft.add(lblLostDesc);

        JPanel sumRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        sumRight.setOpaque(false);

        lblLostLoc = new JLabel("");
        lblLostLoc.setFont(UITheme.FONT_SMALL);
        lblLostLoc.setForeground(UITheme.TEXT_SECONDARY);

        lblLostDate = new JLabel("");
        lblLostDate.setFont(UITheme.FONT_SMALL);
        lblLostDate.setForeground(UITheme.TEXT_SECONDARY);

        lblLostStatus = new BadgeLabel("Active", UITheme.INFO_TEXT, UITheme.INFO_BG);

        sumRight.add(lblLostLoc);
        sumRight.add(lblLostDate);
        sumRight.add(lblLostStatus);

        summaryBox.add(sumLeft, BorderLayout.CENTER);
        summaryBox.add(sumRight, BorderLayout.EAST);

        selectCard.add(summaryBox, BorderLayout.SOUTH);
        mainPanel.add(selectCard);
        mainPanel.add(Box.createVerticalStrut(18));

        // Results Card
        JPanel resultsCard = new JPanel(new BorderLayout(0, 12));
        resultsCard.setBackground(UITheme.BG_CARD);
        resultsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel resHeader = new JPanel(new BorderLayout());
        resHeader.setOpaque(false);

        JLabel resTitle = new JLabel("Ranked Found Item Candidates");
        resTitle.setFont(UITheme.FONT_SUBTITLE);
        resTitle.setForeground(UITheme.TEXT_PRIMARY);

        lblResultsCount = new JLabel("Select an item above and run matching to evaluate compatibility");
        lblResultsCount.setFont(UITheme.FONT_SMALL);
        lblResultsCount.setForeground(UITheme.TEXT_MUTED);

        resHeader.add(resTitle, BorderLayout.WEST);
        resHeader.add(lblResultsCount, BorderLayout.EAST);
        resultsCard.add(resHeader, BorderLayout.NORTH);

        // Modern Table for Matches
        resultsModel = new DefaultTableModel(
                new Object[]{"Rank", "Found ID", "Item Name", "Category", "Location", "Date Found", "Match Score", "Match Level"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) return Integer.class;
                if (columnIndex == 7) return MatchLevel.class;
                return String.class;
            }
        };

        resultsTable = new ModernTable(resultsModel);
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        resultsTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        resultsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        resultsTable.getColumnModel().getColumn(7).setPreferredWidth(140);

        // Score renderer
        resultsTable.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                int score = (Integer) value;
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? UITheme.BG_CARD : UITheme.BG_MAIN));
                p.add(BadgeLabel.forScore(score));
                return p;
            }
        });

        resultsTable.getSelectionModel().addListSelectionListener(e -> updateActionButtonStates());

        JScrollPane tableScroll = ModernTable.createScrollPane(resultsTable);
        tableScroll.setPreferredSize(new Dimension(tableScroll.getPreferredSize().width, 260));
        resultsCard.add(tableScroll, BorderLayout.CENTER);

        // Action Toolbar Below Table
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actionToolbar.setOpaque(false);

        btnViewAnalysis = UITheme.createPrimaryButton("View Full Match Analysis");
        btnViewAnalysis.setEnabled(false);
        btnViewAnalysis.addActionListener(e -> viewAnalysisForSelectedMatch());

        btnMarkPossible = UITheme.createSecondaryButton("Mark Possible Match");
        btnMarkPossible.setEnabled(false);
        btnMarkPossible.addActionListener(e -> markSelectedAsPossibleMatch());

        btnMarkVerified = UITheme.createSecondaryButton("Mark Verified Match");
        btnMarkVerified.setEnabled(false);
        btnMarkVerified.addActionListener(e -> markSelectedAsVerifiedMatch());

        btnCompleteReturn = UITheme.createSuccessButton("Complete Recovery & Return");
        btnCompleteReturn.setEnabled(false);
        btnCompleteReturn.addActionListener(e -> completeReturnForSelectedMatch());

        actionToolbar.add(btnMarkPossible);
        actionToolbar.add(btnMarkVerified);
        actionToolbar.add(btnCompleteReturn);
        actionToolbar.add(btnViewAnalysis);

        resultsCard.add(actionToolbar, BorderLayout.SOUTH);
        mainPanel.add(resultsCard);

        JScrollPane outerScroll = new JScrollPane(mainPanel);
        outerScroll.setBorder(null);
        outerScroll.getVerticalScrollBar().setUnitIncrement(14);
        add(outerScroll, BorderLayout.CENTER);
    }

    public void setTargetLostItem(String lostItemId) {
        for (int i = 0; i < cbLostItems.getItemCount(); i++) {
            LostItemComboItem item = cbLostItems.getItemAt(i);
            if (item != null && item.item.getId().equalsIgnoreCase(lostItemId)) {
                cbLostItems.setSelectedIndex(i);
                executeMatching();
                return;
            }
        }
    }

    private void reloadLostItemsCombo() {
        LostItemComboItem previousSelected = (LostItemComboItem) cbLostItems.getSelectedItem();
        cbLostItems.removeAllItems();

        List<LostItem> list = context.getLostItemService().getAllLostItems();
        for (LostItem item : list) {
            cbLostItems.addItem(new LostItemComboItem(item));
        }

        if (previousSelected != null) {
            for (int i = 0; i < cbLostItems.getItemCount(); i++) {
                if (cbLostItems.getItemAt(i).item.getId().equals(previousSelected.item.getId())) {
                    cbLostItems.setSelectedIndex(i);
                    break;
                }
            }
        }

        updateSelectedLostItemDisplay();
    }

    private void updateSelectedLostItemDisplay() {
        LostItemComboItem sel = (LostItemComboItem) cbLostItems.getSelectedItem();
        if (sel == null || sel.item == null) {
            lblLostTitle.setText("No lost items registered.");
            lblLostSub.setText("");
            lblLostDesc.setText("");
            lblLostLoc.setText("");
            lblLostDate.setText("");
            return;
        }

        LostItem lost = sel.item;
        lblLostTitle.setText("#" + lost.getId() + " - " + lost.getName());
        lblLostSub.setText(lost.getCategory().getDisplayName() + " • Color: " + lost.getColor() + " • Brand: " + lost.getBrand());
        lblLostDesc.setText("Description: " + lost.getDescription());
        lblLostLoc.setText("Location: " + (lost.getLocation() != null ? lost.getLocation().getRawLocation() : "N/A"));
        lblLostDate.setText("Reported: " + DateTimeUtils.formatDisplay(lost.getDateTime()));
        lblLostStatus.setText(lost.getStatus().getDisplayName());

        switch (lost.getStatus()) {
            case RETURNED:
                lblLostStatus.setColors(UITheme.SUCCESS_TEXT, UITheme.SUCCESS_BG);
                break;
            case POSSIBLE_MATCH:
                lblLostStatus.setColors(UITheme.WARNING_TEXT, UITheme.WARNING_BG);
                break;
            case VERIFIED:
                lblLostStatus.setColors(UITheme.PURPLE_TEXT, UITheme.PURPLE_BG);
                break;
            default:
                lblLostStatus.setColors(UITheme.INFO_TEXT, UITheme.INFO_BG);
                break;
        }
    }

    public void executeMatching() {
        LostItemComboItem sel = (LostItemComboItem) cbLostItems.getSelectedItem();
        if (sel == null || sel.item == null) return;

        LostItem lost = sel.item;
        try {
            currentMatches = context.getRecoveryService().findMatchesForLostItem(lost.getId());
            resultsModel.setRowCount(0);

            if (currentMatches.isEmpty()) {
                lblResultsCount.setText("No candidates found in inventory.");
                return;
            }

            lblResultsCount.setText("Evaluated against " + context.getFoundItemService().getAllFoundItems().size() +
                    " items in custody. Found " + currentMatches.size() + " ranked candidate(s):");

            for (int i = 0; i < currentMatches.size(); i++) {
                MatchResult mr = currentMatches.get(i);
                FoundItem f = mr.getFoundItem();
                resultsModel.addRow(new Object[]{
                        "#" + (i + 1),
                        f.getId(),
                        f.getName(),
                        f.getCategory().getDisplayName(),
                        f.getLocation() != null ? f.getLocation().getRawLocation() : "N/A",
                        DateTimeUtils.formatDisplay(f.getDateTime()),
                        mr.getTotalScore(),
                        mr.getMatchLevel()
                });
            }

            if (resultsTable.getRowCount() > 0) {
                resultsTable.setRowSelectionInterval(0, 0);
            }

        } catch (ItemNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Lost item not found: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateActionButtonStates() {
        boolean hasSel = resultsTable.getSelectedRow() >= 0 && currentMatches != null && !currentMatches.isEmpty();
        btnViewAnalysis.setEnabled(hasSel);
        btnMarkPossible.setEnabled(hasSel);
        btnMarkVerified.setEnabled(hasSel);
        btnCompleteReturn.setEnabled(hasSel);
    }

    private MatchResult getSelectedMatchResult() {
        int row = resultsTable.getSelectedRow();
        if (row >= 0 && currentMatches != null && row < currentMatches.size()) {
            return currentMatches.get(row);
        }
        return null;
    }

    private void viewAnalysisForSelectedMatch() {
        MatchResult mr = getSelectedMatchResult();
        if (mr != null) {
            context.navigateToMatchAnalysis(mr.getLostItem().getId(), mr.getFoundItem().getId());
        }
    }

    private void markSelectedAsPossibleMatch() {
        MatchResult mr = getSelectedMatchResult();
        if (mr != null) {
            try {
                context.getRecoveryService().markAsPossibleMatch(mr.getLostItem().getId(), mr.getFoundItem().getId());
                context.notifyDataChanged();
                JOptionPane.showMessageDialog(this,
                        "Status updated to POSSIBLE_MATCH for Lost #" + mr.getLostItem().getId() +
                                " and Found #" + mr.getFoundItem().getId(),
                        "Match Status Updated",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (TraceItException e) {
                JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void markSelectedAsVerifiedMatch() {
        MatchResult mr = getSelectedMatchResult();
        if (mr != null) {
            try {
                context.getRecoveryService().markAsVerified(mr.getLostItem().getId(), mr.getFoundItem().getId());
                context.notifyDataChanged();
                JOptionPane.showMessageDialog(this,
                        "Status updated to VERIFIED MATCH for Lost #" + mr.getLostItem().getId() +
                                " and Found #" + mr.getFoundItem().getId(),
                        "Match Verified",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (TraceItException e) {
                JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void completeReturnForSelectedMatch() {
        MatchResult mr = getSelectedMatchResult();
        if (mr != null) {
            Window win = SwingUtilities.getWindowAncestor(this);
            RecoveryReturnDialog dlg = new RecoveryReturnDialog(win, mr.getLostItem().getId(), mr.getFoundItem().getId(), context);
            dlg.setVisible(true);
            if (dlg.isRecoveryCompleted()) {
                executeMatching();
            }
        }
    }

    @Override
    public void onDataChanged() {
        reloadLostItemsCombo();
    }

    private static class LostItemComboItem {
        final LostItem item;

        LostItemComboItem(LostItem item) {
            this.item = item;
        }

        @Override
        public String toString() {
            if (item == null) return "None";
            return "#" + item.getId() + " - " + item.getName() + " (" + item.getCategory().getDisplayName() + ") [" + item.getStatus().getDisplayName() + "]";
        }
    }
}


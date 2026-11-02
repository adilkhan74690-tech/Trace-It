package traceit.ui.screens;

import traceit.exception.ItemNotFoundException;
import traceit.exception.TraceItException;
import traceit.model.*;
import traceit.ui.AppContext;
import traceit.ui.components.BadgeLabel;
import traceit.ui.components.ModernComboBox;
import traceit.ui.components.ModernTable;
import traceit.ui.components.RecoveryReturnDialog;
import traceit.ui.theme.UITheme;
import traceit.util.DateTimeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MatchAnalysisScreen extends JPanel implements AppContext.DataChangeListener {

    private final AppContext context;

    private ModernComboBox<LostItemComboItem> cbLostItems;
    private ModernComboBox<FoundItemComboItem> cbFoundItems;
    private JButton btnAnalyze;

    // Score Header Banner
    private JPanel scoreBanner;
    private JLabel lblTotalScore;
    private JLabel lblScoreSubtitle;
    private BadgeLabel badgeMatchLevel;

    // Comparison Dual Cards
    private JLabel lblLostInfo;
    private JLabel lblFoundInfo;
    private JTextArea txtLostDetails;
    private JTextArea txtFoundDetails;

    // Rules Breakdown Table
    private ModernTable breakdownTable;
    private DefaultTableModel breakdownModel;

    // Categorized Insights
    private JPanel matchedPointsPanel;
    private JPanel partialPointsPanel;
    private JPanel diffPointsPanel;

    // Actions
    private JButton btnMarkPossible;
    private JButton btnMarkVerified;
    private JButton btnCompleteReturn;

    private MatchResult currentResult;

    public MatchAnalysisScreen(AppContext context) {
        this.context = context;
        context.addDataChangeListener(this);
        initComponents();
        reloadItemCombos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel titleLbl = new JLabel("Match Analysis & Explainability Engine");
        titleLbl.setFont(UITheme.FONT_HEADER_LARGE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitleLbl = new JLabel("Inspect rule-by-rule scoring justification, feature overlap, and attribute variance");
        subtitleLbl.setFont(UITheme.FONT_BODY);
        subtitleLbl.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(titleLbl);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLbl);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(18));

        // Pair Selection Card
        JPanel selectionCard = new JPanel(new GridLayout(1, 3, 14, 0));
        selectionCard.setBackground(UITheme.BG_CARD);
        selectionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JPanel lostSelPanel = new JPanel(new BorderLayout(0, 4));
        lostSelPanel.setOpaque(false);
        JLabel l1 = new JLabel("Target Lost Item:");
        l1.setFont(UITheme.FONT_SMALL_BOLD);
        l1.setForeground(UITheme.TEXT_SECONDARY);
        cbLostItems = new ModernComboBox<>();
        lostSelPanel.add(l1, BorderLayout.NORTH);
        lostSelPanel.add(cbLostItems, BorderLayout.CENTER);

        JPanel foundSelPanel = new JPanel(new BorderLayout(0, 4));
        foundSelPanel.setOpaque(false);
        JLabel l2 = new JLabel("Candidate Found Item:");
        l2.setFont(UITheme.FONT_SMALL_BOLD);
        l2.setForeground(UITheme.TEXT_SECONDARY);
        cbFoundItems = new ModernComboBox<>();
        foundSelPanel.add(l2, BorderLayout.NORTH);
        foundSelPanel.add(cbFoundItems, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new BorderLayout(0, 4));
        btnPanel.setOpaque(false);
        btnPanel.add(new JLabel(" "), BorderLayout.NORTH);
        btnAnalyze = UITheme.createPrimaryButton("🔍 Run Deep Analysis");
        btnAnalyze.addActionListener(e -> executeAnalysis());
        btnPanel.add(btnAnalyze, BorderLayout.CENTER);

        selectionCard.add(lostSelPanel);
        selectionCard.add(foundSelPanel);
        selectionCard.add(btnPanel);

        mainPanel.add(selectionCard);
        mainPanel.add(Box.createVerticalStrut(18));

        // Score Hero Banner
        scoreBanner = new JPanel(new BorderLayout());
        scoreBanner.setBackground(UITheme.BG_CARD);
        scoreBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(18, 22, 18, 22)
        ));

        JPanel heroLeft = new JPanel();
        heroLeft.setOpaque(false);
        heroLeft.setLayout(new BoxLayout(heroLeft, BoxLayout.Y_AXIS));

        lblTotalScore = new JLabel("-- / 100");
        lblTotalScore.setFont(new Font(UITheme.FONT_STAT_NUM.getName(), Font.BOLD, 36));
        lblTotalScore.setForeground(UITheme.PRIMARY);

        lblScoreSubtitle = new JLabel("Select a Lost Item and a Found Item above to calculate explainable score breakdown");
        lblScoreSubtitle.setFont(UITheme.FONT_BODY);
        lblScoreSubtitle.setForeground(UITheme.TEXT_SECONDARY);

        heroLeft.add(lblTotalScore);
        heroLeft.add(Box.createVerticalStrut(2));
        heroLeft.add(lblScoreSubtitle);

        badgeMatchLevel = new BadgeLabel("NO ANALYSIS", UITheme.TEXT_MUTED, UITheme.BG_CARD_ALT);

        JPanel heroRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        heroRight.setOpaque(false);
        heroRight.add(badgeMatchLevel);

        scoreBanner.add(heroLeft, BorderLayout.CENTER);
        scoreBanner.add(heroRight, BorderLayout.EAST);
        mainPanel.add(scoreBanner);
        mainPanel.add(Box.createVerticalStrut(18));

        // Side-by-side Items Comparison
        JPanel dualCardContainer = new JPanel(new GridLayout(1, 2, 16, 0));
        dualCardContainer.setOpaque(false);

        // Lost Item Card
        JPanel lostCard = createComparisonCard("Lost Item Details", UITheme.PRIMARY);
        lblLostInfo = new JLabel("No lost item selected");
        lblLostInfo.setFont(UITheme.FONT_SUBTITLE);
        lblLostInfo.setForeground(UITheme.TEXT_PRIMARY);
        txtLostDetails = createInfoTextArea();
        lostCard.add(lblLostInfo, BorderLayout.NORTH);
        lostCard.add(txtLostDetails, BorderLayout.CENTER);
        dualCardContainer.add(lostCard);

        // Found Item Card
        JPanel foundCard = createComparisonCard("Found Item Details", UITheme.TEAL_TEXT);
        lblFoundInfo = new JLabel("No found item selected");
        lblFoundInfo.setFont(UITheme.FONT_SUBTITLE);
        lblFoundInfo.setForeground(UITheme.TEXT_PRIMARY);
        txtFoundDetails = createInfoTextArea();
        foundCard.add(lblFoundInfo, BorderLayout.NORTH);
        foundCard.add(txtFoundDetails, BorderLayout.CENTER);
        dualCardContainer.add(foundCard);

        mainPanel.add(dualCardContainer);
        mainPanel.add(Box.createVerticalStrut(18));

        // Rule Breakdown Table Card
        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(UITheme.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel tblTitle = new JLabel("Rule-by-Rule Scoring Breakdown");
        tblTitle.setFont(UITheme.FONT_SUBTITLE);
        tblTitle.setForeground(UITheme.TEXT_PRIMARY);
        tableCard.add(tblTitle, BorderLayout.NORTH);

        breakdownModel = new DefaultTableModel(
                new Object[]{"Rule Name", "Weight Contributed", "Max Weight", "Status", "Evaluation Explanation"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        breakdownTable = new ModernTable(breakdownModel);
        breakdownTable.getColumnModel().getColumn(0).setPreferredWidth(170);
        breakdownTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        breakdownTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        breakdownTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        breakdownTable.getColumnModel().getColumn(4).setPreferredWidth(320);

        // Status Symbol renderer
        breakdownTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String symbol = String.valueOf(value);
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setFont(UITheme.FONT_BODY_BOLD);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("[OK]".equals(symbol) || "✓".equals(symbol)) {
                    lbl.setForeground(UITheme.SUCCESS_TEXT);
                } else if ("[PARTIAL]".equals(symbol) || "△".equals(symbol)) {
                    lbl.setForeground(UITheme.WARNING_TEXT);
                } else {
                    lbl.setForeground(UITheme.DANGER_TEXT);
                }
                return lbl;
            }
        });

        JScrollPane breakdownScroll = ModernTable.createScrollPane(breakdownTable);
        breakdownScroll.setPreferredSize(new Dimension(breakdownScroll.getPreferredSize().width, 220));
        tableCard.add(breakdownScroll, BorderLayout.CENTER);

        mainPanel.add(tableCard);
        mainPanel.add(Box.createVerticalStrut(18));

        // Insights Section (Matched, Partial, Differences)
        JPanel insightsContainer = new JPanel(new GridLayout(1, 3, 14, 0));
        insightsContainer.setOpaque(false);

        matchedPointsPanel = createInsightPanel("✓ Matched Criteria", UITheme.SUCCESS_TEXT, UITheme.SUCCESS_BG);
        partialPointsPanel = createInsightPanel("△ Partial Matches", UITheme.WARNING_TEXT, UITheme.WARNING_BG);
        diffPointsPanel = createInsightPanel("✕ Differences / Gaps", UITheme.DANGER_TEXT, UITheme.DANGER_BG);

        insightsContainer.add(matchedPointsPanel);
        insightsContainer.add(partialPointsPanel);
        insightsContainer.add(diffPointsPanel);

        mainPanel.add(insightsContainer);
        mainPanel.add(Box.createVerticalStrut(20));

        // Action Toolbar
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionToolbar.setOpaque(false);

        btnMarkPossible = UITheme.createSecondaryButton("Mark as Possible Match");
        btnMarkPossible.setEnabled(false);
        btnMarkPossible.addActionListener(e -> markPossible());

        btnMarkVerified = UITheme.createSecondaryButton("Mark as Verified Match");
        btnMarkVerified.setEnabled(false);
        btnMarkVerified.addActionListener(e -> markVerified());

        btnCompleteReturn = UITheme.createSuccessButton("Complete Recovery & Return");
        btnCompleteReturn.setEnabled(false);
        btnCompleteReturn.addActionListener(e -> completeReturn());

        actionToolbar.add(btnMarkPossible);
        actionToolbar.add(btnMarkVerified);
        actionToolbar.add(btnCompleteReturn);

        mainPanel.add(actionToolbar);

        JScrollPane outerScroll = new JScrollPane(mainPanel);
        outerScroll.setBorder(null);
        outerScroll.getVerticalScrollBar().setUnitIncrement(14);
        add(outerScroll, BorderLayout.CENTER);
    }

    private JPanel createComparisonCard(String title, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL_BOLD);
        t.setForeground(accent);
        card.add(t, BorderLayout.NORTH);
        return card;
    }

    private JTextArea createInfoTextArea() {
        JTextArea ta = new JTextArea("No selection");
        ta.setFont(UITheme.FONT_BODY);
        ta.setForeground(UITheme.TEXT_SECONDARY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setBorder(new EmptyBorder(4, 0, 0, 0));
        return ta;
    }

    private JPanel createInsightPanel(String title, Color fg, Color bg) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(UITheme.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL_BOLD);
        titleLbl.setForeground(fg);
        p.add(titleLbl, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        p.add(list, BorderLayout.CENTER);

        return p;
    }

    public void setTargetPair(String lostItemId, String foundItemId) {
        for (int i = 0; i < cbLostItems.getItemCount(); i++) {
            LostItemComboItem it = cbLostItems.getItemAt(i);
            if (it != null && it.item.getId().equalsIgnoreCase(lostItemId)) {
                cbLostItems.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < cbFoundItems.getItemCount(); i++) {
            FoundItemComboItem it = cbFoundItems.getItemAt(i);
            if (it != null && it.item.getId().equalsIgnoreCase(foundItemId)) {
                cbFoundItems.setSelectedIndex(i);
                break;
            }
        }
        executeAnalysis();
    }

    private void reloadItemCombos() {
        cbLostItems.removeAllItems();
        List<LostItem> losts = context.getLostItemService().getAllLostItems();
        for (LostItem lost : losts) {
            cbLostItems.addItem(new LostItemComboItem(lost));
        }

        cbFoundItems.removeAllItems();
        List<FoundItem> founds = context.getFoundItemService().getAllFoundItems();
        for (FoundItem found : founds) {
            cbFoundItems.addItem(new FoundItemComboItem(found));
        }
    }

    public void executeAnalysis() {
        LostItemComboItem selLost = (LostItemComboItem) cbLostItems.getSelectedItem();
        FoundItemComboItem selFound = (FoundItemComboItem) cbFoundItems.getSelectedItem();

        if (selLost == null || selFound == null || selLost.item == null || selFound.item == null) {
            JOptionPane.showMessageDialog(this, "Please select both a Lost Item and a Found Item.", "Missing Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            currentResult = context.getRecoveryService().analyzePair(selLost.item.getId(), selFound.item.getId());
            displayAnalysis(currentResult);
        } catch (ItemNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Item lookup failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayAnalysis(MatchResult result) {
        LostItem lost = result.getLostItem();
        FoundItem found = result.getFoundItem();

        // Total score & level
        int score = result.getTotalScore();
        lblTotalScore.setText(score + " / 100");
        lblTotalScore.setForeground(score >= 75 ? UITheme.SUCCESS_TEXT : (score >= 50 ? UITheme.WARNING_TEXT : UITheme.DANGER_TEXT));
        lblScoreSubtitle.setText("Compatibility evaluation completed: " + result.getMatchLevel().getLabel());

        badgeMatchLevel.setText(result.getMatchLevel().getLabel());
        switch (result.getMatchLevel()) {
            case VERY_STRONG_MATCH:
                badgeMatchLevel.setColors(UITheme.SUCCESS_TEXT, UITheme.SUCCESS_BG);
                break;
            case STRONG_MATCH:
                badgeMatchLevel.setColors(UITheme.TEAL_TEXT, UITheme.TEAL_BG);
                break;
            case POSSIBLE_MATCH:
                badgeMatchLevel.setColors(UITheme.WARNING_TEXT, UITheme.WARNING_BG);
                break;
            case WEAK_MATCH:
            case UNLIKELY_MATCH:
            default:
                badgeMatchLevel.setColors(UITheme.DANGER_TEXT, UITheme.DANGER_BG);
                break;
        }

        // Lost details
        lblLostInfo.setText("#" + lost.getId() + " - " + lost.getName() + " (" + lost.getCategory().getDisplayName() + ")");
        txtLostDetails.setText(
                "Color: " + lost.getColor() + "\n" +
                "Brand: " + lost.getBrand() + "\n" +
                "Location: " + (lost.getLocation() != null ? lost.getLocation().getRawLocation() : "N/A") + "\n" +
                "Date: " + DateTimeUtils.formatDisplay(lost.getDateTime()) + "\n" +
                "Distinctive: " + lost.getDistinctiveFeatures() + "\n" +
                "Description: " + lost.getDescription() + "\n" +
                "Reporter: " + (lost.getReporter() != null ? lost.getReporter().getName() + " (" + lost.getReporter().getEmail() + ")" : "N/A")
        );

        // Found details
        lblFoundInfo.setText("#" + found.getId() + " - " + found.getName() + " (" + found.getCategory().getDisplayName() + ")");
        txtFoundDetails.setText(
                "Color: " + found.getColor() + "\n" +
                "Brand: " + found.getBrand() + "\n" +
                "Location: " + (found.getLocation() != null ? found.getLocation().getRawLocation() : "N/A") + "\n" +
                "Date: " + DateTimeUtils.formatDisplay(found.getDateTime()) + "\n" +
                "Distinctive: " + found.getDistinctiveFeatures() + "\n" +
                "Description: " + found.getDescription() + "\n" +
                "Storage Custody: " + found.getStorageLocation() + "\n" +
                "Verification Q: " + found.getVerificationQuestion()
        );

        // Breakdown table
        breakdownModel.setRowCount(0);
        for (MatchBreakdown b : result.getBreakdowns()) {
            breakdownModel.addRow(new Object[]{
                    b.getRuleName(),
                    "+" + b.getScore() + " pts",
                    b.getMaxWeight() + " pts",
                    b.getSymbol(),
                    b.getExplanation()
            });
        }

        // Categorized insight panels
        populateInsightPanel(matchedPointsPanel, result, "[OK]", "✓");
        populateInsightPanel(partialPointsPanel, result, "[PARTIAL]", "△");
        populateInsightPanel(diffPointsPanel, result, "[x]", "✕");

        btnMarkPossible.setEnabled(true);
        btnMarkVerified.setEnabled(true);
        btnCompleteReturn.setEnabled(true);
    }

    private void populateInsightPanel(JPanel panel, MatchResult res, String sym1, String sym2) {
        JPanel list = (JPanel) panel.getComponent(1);
        list.removeAll();

        int count = 0;
        for (MatchBreakdown b : res.getBreakdowns()) {
            String s = b.getSymbol();
            if (sym1.equals(s) || sym2.equals(s) || ("[x]".equals(sym1) && !"[OK]".equals(s) && !"✓".equals(s) && !"[PARTIAL]".equals(s) && !"△".equals(s))) {
                JLabel item = new JLabel("• " + b.getRuleName() + ": " + b.getExplanation());
                item.setFont(UITheme.FONT_SMALL);
                item.setForeground(UITheme.TEXT_SECONDARY);
                item.setBorder(new EmptyBorder(3, 0, 3, 0));
                list.add(item);
                count++;
            }
        }

        if (count == 0) {
            JLabel none = new JLabel("None detected");
            none.setFont(UITheme.FONT_SMALL);
            none.setForeground(UITheme.TEXT_MUTED);
            list.add(none);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void markPossible() {
        if (currentResult != null) {
            try {
                context.getRecoveryService().markAsPossibleMatch(currentResult.getLostItem().getId(), currentResult.getFoundItem().getId());
                context.notifyDataChanged();
                JOptionPane.showMessageDialog(this, "Status updated to POSSIBLE_MATCH for both items.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (TraceItException e) {
                JOptionPane.showMessageDialog(this, "Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void markVerified() {
        if (currentResult != null) {
            try {
                context.getRecoveryService().markAsVerified(currentResult.getLostItem().getId(), currentResult.getFoundItem().getId());
                context.notifyDataChanged();
                JOptionPane.showMessageDialog(this, "Status updated to VERIFIED MATCH for both items.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (TraceItException e) {
                JOptionPane.showMessageDialog(this, "Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void completeReturn() {
        if (currentResult != null) {
            Window win = SwingUtilities.getWindowAncestor(this);
            RecoveryReturnDialog dlg = new RecoveryReturnDialog(win, currentResult.getLostItem().getId(), currentResult.getFoundItem().getId(), context);
            dlg.setVisible(true);
        }
    }

    @Override
    public void onDataChanged() {
        reloadItemCombos();
    }

    private static class LostItemComboItem {
        final LostItem item;

        LostItemComboItem(LostItem item) {
            this.item = item;
        }

        @Override
        public String toString() {
            if (item == null) return "None";
            return "#" + item.getId() + " - " + item.getName() + " (" + item.getCategory().getDisplayName() + ")";
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
            return "#" + item.getId() + " - " + item.getName() + " (" + item.getCategory().getDisplayName() + ")";
        }
    }
}


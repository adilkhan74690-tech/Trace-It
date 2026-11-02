package traceit.ui.screens;

import traceit.model.*;
import traceit.ui.AppContext;
import traceit.ui.components.ItemDetailDialog;
import traceit.ui.components.ModernTable;
import traceit.ui.components.StatCard;
import traceit.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DashboardScreen extends JPanel implements AppContext.DataChangeListener {

    private final AppContext context;

    private StatCard statTotalLost;
    private StatCard statTotalFound;
    private StatCard statActiveLost;
    private StatCard statActiveFound;
    private StatCard statMatches;
    private StatCard statReturned;
    private StatCard statRecoveryRate;

    private ModernTable recentLostTable;
    private DefaultTableModel recentLostModel;
    private ModernTable recentFoundTable;
    private DefaultTableModel recentFoundModel;

    private JLabel recentRecoverySummaryLbl;

    public DashboardScreen(AppContext context) {
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

        // Header Title Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel titleLbl = new JLabel("System Dashboard");
        titleLbl.setFont(UITheme.FONT_HEADER_LARGE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitleLbl = new JLabel("Real-time campus lost-item recovery metrics and active inventory");
        subtitleLbl.setFont(UITheme.FONT_BODY);
        subtitleLbl.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(titleLbl);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLbl);

        // Quick Actions in Header
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        quickActions.setOpaque(false);

        JButton btnReportLost = UITheme.createPrimaryButton("+ Report Lost");
        btnReportLost.addActionListener(e -> context.navigateTo("REPORT_LOST"));

        JButton btnReportFound = UITheme.createSecondaryButton("+ Report Found");
        btnReportFound.addActionListener(e -> context.navigateTo("REPORT_FOUND"));

        JButton btnMatch = UITheme.createOutlineButton("⚡ Run Smart Match");
        btnMatch.addActionListener(e -> context.navigateTo("SMART_MATCHING"));

        quickActions.add(btnReportLost);
        quickActions.add(btnReportFound);
        quickActions.add(btnMatch);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(quickActions, BorderLayout.EAST);

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Stat Cards Panel (Responsive Flow / Grid)
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 14, 14));
        statsPanel.setOpaque(false);

        statTotalLost = new StatCard("Total Lost", "0", "Registered reports", UITheme.PRIMARY);
        statTotalFound = new StatCard("Total Found", "0", "Handed in items", UITheme.TEAL_TEXT);
        statActiveLost = new StatCard("Active Lost Cases", "0", "Awaiting matches", UITheme.INFO_TEXT);
        statActiveFound = new StatCard("Available Found Items", "0", "In campus custody", UITheme.TEAL_TEXT);

        statMatches = new StatCard("In Matching Pipeline", "0", "Possible / Verified", UITheme.WARNING_SOLID);
        statReturned = new StatCard("Returned to Owners", "0", "Recoveries completed", UITheme.SUCCESS_SOLID);
        statRecoveryRate = new StatCard("Resolution Rate", "0%", "Successful recoveries", UITheme.PURPLE_TEXT);

        StatCard statPersist = new StatCard("Data Storage", "Active", "data/ (File Repository)", UITheme.TEXT_SECONDARY);

        statsPanel.add(statTotalLost);
        statsPanel.add(statTotalFound);
        statsPanel.add(statActiveLost);
        statsPanel.add(statActiveFound);
        statsPanel.add(statMatches);
        statsPanel.add(statReturned);
        statsPanel.add(statRecoveryRate);
        statsPanel.add(statPersist);

        mainPanel.add(statsPanel);
        mainPanel.add(Box.createVerticalStrut(22));

        // Split Panes for Recent Tables
        JPanel tablesContainer = new JPanel(new GridLayout(1, 2, 16, 0));
        tablesContainer.setOpaque(false);

        // Recent Lost Panel
        JPanel lostPanel = createTableCard("Recent Lost Items", "Click a row to inspect or match", true);
        recentLostModel = new DefaultTableModel(new Object[]{"ID", "Name", "Category", "Location", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return ItemStatus.class;
                return String.class;
            }
        };
        recentLostTable = new ModernTable(recentLostModel);
        setupTableClick(recentLostTable, true);
        lostPanel.add(ModernTable.createScrollPane(recentLostTable), BorderLayout.CENTER);
        tablesContainer.add(lostPanel);

        // Recent Found Panel
        JPanel foundPanel = createTableCard("Recent Found Items", "Click a row to inspect custody info", false);
        recentFoundModel = new DefaultTableModel(new Object[]{"ID", "Name", "Category", "Storage", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return ItemStatus.class;
                return String.class;
            }
        };
        recentFoundTable = new ModernTable(recentFoundModel);
        setupTableClick(recentFoundTable, false);
        foundPanel.add(ModernTable.createScrollPane(recentFoundTable), BorderLayout.CENTER);
        tablesContainer.add(foundPanel);

        mainPanel.add(tablesContainer);
        mainPanel.add(Box.createVerticalStrut(18));

        // Recent Recovery Activity Footer
        JPanel recentRecoveryPanel = new JPanel(new BorderLayout());
        recentRecoveryPanel.setBackground(UITheme.BG_CARD);
        recentRecoveryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(12, 18, 12, 18)
        ));

        JLabel rrTitle = new JLabel("Latest Recovery Activity:");
        rrTitle.setFont(UITheme.FONT_BODY_BOLD);
        rrTitle.setForeground(UITheme.TEXT_PRIMARY);

        recentRecoverySummaryLbl = new JLabel("No recovery reports yet.");
        recentRecoverySummaryLbl.setFont(UITheme.FONT_BODY);
        recentRecoverySummaryLbl.setForeground(UITheme.TEXT_SECONDARY);

        JButton viewAllReportsBtn = UITheme.createOutlineButton("View All Reports");
        viewAllReportsBtn.addActionListener(e -> context.navigateTo("REPORTS"));

        JPanel rrLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rrLeft.setOpaque(false);
        rrLeft.add(rrTitle);
        rrLeft.add(recentRecoverySummaryLbl);

        recentRecoveryPanel.add(rrLeft, BorderLayout.CENTER);
        recentRecoveryPanel.add(viewAllReportsBtn, BorderLayout.EAST);

        mainPanel.add(recentRecoveryPanel);

        JScrollPane outerScroll = new JScrollPane(mainPanel);
        outerScroll.setBorder(null);
        outerScroll.getVerticalScrollBar().setUnitIncrement(14);
        add(outerScroll, BorderLayout.CENTER);
    }

    private JPanel createTableCard(String title, String subtitle, boolean isLost) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBTITLE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_MUTED);

        top.add(titleLbl, BorderLayout.WEST);
        top.add(subLbl, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        return card;
    }

    private void setupTableClick(JTable table, boolean isLost) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String id = (String) table.getValueAt(row, 0);
                        try {
                            Item item = isLost ? context.getLostItemService().getById(id) : context.getFoundItemService().getById(id);
                            Window win = SwingUtilities.getWindowAncestor(DashboardScreen.this);
                            ItemDetailDialog dlg = new ItemDetailDialog(win, item, context);
                            dlg.setVisible(true);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(DashboardScreen.this, "Item not found: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDataChanged() {
        refreshData();
    }

    public void refreshData() {
        List<LostItem> lostItems = context.getLostItemService().getAllLostItems();
        List<FoundItem> foundItems = context.getFoundItemService().getAllFoundItems();
        List<RecoveryReport> reports = context.getRecoveryService().getAllRecoveryReports();

        int totalLost = lostItems.size();
        int totalFound = foundItems.size();

        int activeLost = 0;
        int activeFound = 0;
        int matchingCount = 0;
        int returnedCount = 0;

        for (LostItem item : lostItems) {
            if (item.getStatus() == ItemStatus.LOST) activeLost++;
            else if (item.getStatus() == ItemStatus.POSSIBLE_MATCH || item.getStatus() == ItemStatus.VERIFIED) matchingCount++;
            else if (item.getStatus() == ItemStatus.RETURNED) returnedCount++;
        }

        for (FoundItem item : foundItems) {
            if (item.getStatus() == ItemStatus.FOUND) activeFound++;
            else if (item.getStatus() == ItemStatus.POSSIBLE_MATCH || item.getStatus() == ItemStatus.VERIFIED) matchingCount++;
            else if (item.getStatus() == ItemStatus.RETURNED) returnedCount++;
        }

        // Halve matching and returned count if counting pairs, or count distinct returned reports
        int resolvedReportsCount = reports.size();

        int recoveryRate = totalLost > 0 ? (int) Math.round(((double) resolvedReportsCount / totalLost) * 100) : 0;

        statTotalLost.setValue(String.valueOf(totalLost));
        statTotalFound.setValue(String.valueOf(totalFound));
        statActiveLost.setValue(String.valueOf(activeLost));
        statActiveFound.setValue(String.valueOf(activeFound));
        statMatches.setValue(String.valueOf(matchingCount));
        statReturned.setValue(String.valueOf(resolvedReportsCount));
        statRecoveryRate.setValue(recoveryRate + "%");

        // Refresh Recent Lost Table
        recentLostModel.setRowCount(0);
        int lostLimit = Math.min(10, lostItems.size());
        for (int i = lostItems.size() - 1; i >= lostItems.size() - lostLimit; i--) {
            LostItem item = lostItems.get(i);
            recentLostModel.addRow(new Object[]{
                    item.getId(),
                    item.getName(),
                    item.getCategory().getDisplayName(),
                    item.getLocation() != null ? item.getLocation().getRawLocation() : "N/A",
                    item.getStatus()
            });
        }

        // Refresh Recent Found Table
        recentFoundModel.setRowCount(0);
        int foundLimit = Math.min(10, foundItems.size());
        for (int i = foundItems.size() - 1; i >= foundItems.size() - foundLimit; i--) {
            FoundItem item = foundItems.get(i);
            recentFoundModel.addRow(new Object[]{
                    item.getId(),
                    item.getName(),
                    item.getCategory().getDisplayName(),
                    item.getStorageLocation(),
                    item.getStatus()
            });
        }

        // Refresh Recent Recovery Activity
        if (!reports.isEmpty()) {
            RecoveryReport latest = reports.get(reports.size() - 1);
            recentRecoverySummaryLbl.setText(String.format("Report #%s: %s (Lost) <-> %s (Found) | Score: %d/100 (%s)",
                    latest.getReportId(), latest.getLostItemName(), latest.getFoundItemName(), latest.getMatchScore(), latest.getMatchLevel().getLabel()));
        } else {
            recentRecoverySummaryLbl.setText("No recovery cases completed yet.");
        }
    }
}


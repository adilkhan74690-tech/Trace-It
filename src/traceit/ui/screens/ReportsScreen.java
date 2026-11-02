package traceit.ui.screens;

import traceit.model.MatchLevel;
import traceit.model.RecoveryReport;
import traceit.ui.AppContext;
import traceit.ui.components.BadgeLabel;
import traceit.ui.components.ModernTable;
import traceit.ui.components.StatCard;
import traceit.ui.theme.UITheme;
import traceit.util.DateTimeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportsScreen extends JPanel implements AppContext.DataChangeListener {

    private final AppContext context;

    private StatCard statTotalReports;
    private StatCard statAvgScore;
    private StatCard statHighestScore;

    private ModernTable reportsTable;
    private DefaultTableModel reportsModel;
    private List<RecoveryReport> reportsList;

    // Detailed Report Viewer Pane
    private JLabel lblReportId;
    private JLabel lblReportItems;
    private JLabel lblReportDate;
    private BadgeLabel badgeReportScore;
    private BadgeLabel badgeReportLevel;
    private JTextArea txtResolutionNotes;
    private JLabel lblOwnerContact;
    private JLabel lblFinderContact;

    private JButton btnExportFile;
    private JButton btnCopyClipboard;

    public ReportsScreen(AppContext context) {
        this.context = context;
        context.addDataChangeListener(this);
        initComponents();
        refreshReports();
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

        JLabel titleLbl = new JLabel("Recovery Reports & Audit Trail");
        titleLbl.setFont(UITheme.FONT_HEADER_LARGE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitleLbl = new JLabel("Audit log of completed property returns, compatibility certifications, and resolution notes");
        subtitleLbl.setFont(UITheme.FONT_BODY);
        subtitleLbl.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(titleLbl);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLbl);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(18));

        // Stats summary row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);

        statTotalReports = new StatCard("Total Recoveries", "0", "Documented handovers", UITheme.PRIMARY);
        statAvgScore = new StatCard("Average Match Score", "0 / 100", "Across all recoveries", UITheme.SUCCESS_SOLID);
        statHighestScore = new StatCard("Top Compatibility", "0 / 100", "Highest recorded match", UITheme.PURPLE_TEXT);

        statsRow.add(statTotalReports);
        statsRow.add(statAvgScore);
        statsRow.add(statHighestScore);

        mainPanel.add(statsRow);
        mainPanel.add(Box.createVerticalStrut(18));

        // Split Container: Reports Table (Left) + Detail Card (Right)
        JPanel splitContainer = new JPanel(new GridLayout(1, 2, 16, 0));
        splitContainer.setOpaque(false);

        // Left: Reports Table Card
        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(UITheme.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel tblTitle = new JLabel("Completed Recovery Log");
        tblTitle.setFont(UITheme.FONT_SUBTITLE);
        tblTitle.setForeground(UITheme.TEXT_PRIMARY);
        tableCard.add(tblTitle, BorderLayout.NORTH);

        reportsModel = new DefaultTableModel(
                new Object[]{"Report ID", "Lost Item", "Found Item", "Score", "Level", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class;
                if (columnIndex == 4) return MatchLevel.class;
                return String.class;
            }
        };

        reportsTable = new ModernTable(reportsModel);
        reportsTable.getColumnModel().getColumn(0).setPreferredWidth(85);
        reportsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        reportsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        reportsTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        reportsTable.getColumnModel().getColumn(4).setPreferredWidth(125);
        reportsTable.getColumnModel().getColumn(5).setPreferredWidth(110);

        reportsTable.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                int score = (Integer) value;
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? UITheme.BG_CARD : UITheme.BG_MAIN));
                p.add(BadgeLabel.forScore(score));
                return p;
            }
        });

        reportsTable.getSelectionModel().addListSelectionListener(e -> showSelectedReportDetails());

        JScrollPane tableScroll = ModernTable.createScrollPane(reportsTable);
        tableScroll.setPreferredSize(new Dimension(tableScroll.getPreferredSize().width, 320));
        tableCard.add(tableScroll, BorderLayout.CENTER);
        splitContainer.add(tableCard);

        // Right: Report Details Card
        JPanel detailCard = new JPanel(new BorderLayout(0, 12));
        detailCard.setBackground(UITheme.BG_CARD);
        detailCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel dtTop = new JPanel(new BorderLayout());
        dtTop.setOpaque(false);

        lblReportId = new JLabel("Select a Report");
        lblReportId.setFont(UITheme.FONT_SUBTITLE);
        lblReportId.setForeground(UITheme.TEXT_PRIMARY);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        badges.setOpaque(false);
        badgeReportScore = new BadgeLabel("-- / 100", UITheme.TEXT_MUTED, UITheme.BG_CARD_ALT);
        badgeReportLevel = new BadgeLabel("N/A", UITheme.TEXT_MUTED, UITheme.BG_CARD_ALT);
        badges.add(badgeReportScore);
        badges.add(badgeReportLevel);

        dtTop.add(lblReportId, BorderLayout.WEST);
        dtTop.add(badges, BorderLayout.EAST);
        detailCard.add(dtTop, BorderLayout.NORTH);

        JPanel dtBody = new JPanel();
        dtBody.setOpaque(false);
        dtBody.setLayout(new BoxLayout(dtBody, BoxLayout.Y_AXIS));

        lblReportItems = new JLabel("Lost: - <-> Found: -");
        lblReportItems.setFont(UITheme.FONT_BODY_BOLD);
        lblReportItems.setForeground(UITheme.TEXT_PRIMARY);

        lblReportDate = new JLabel("Date: -");
        lblReportDate.setFont(UITheme.FONT_SMALL);
        lblReportDate.setForeground(UITheme.TEXT_SECONDARY);

        dtBody.add(lblReportItems);
        dtBody.add(Box.createVerticalStrut(4));
        dtBody.add(lblReportDate);
        dtBody.add(Box.createVerticalStrut(12));

        JLabel lOwner = new JLabel("Owner Contact:");
        lOwner.setFont(UITheme.FONT_SMALL_BOLD);
        lOwner.setForeground(UITheme.TEXT_MUTED);
        lblOwnerContact = new JLabel("-");
        lblOwnerContact.setFont(UITheme.FONT_BODY);

        JLabel lFinder = new JLabel("Finder Contact:");
        lFinder.setFont(UITheme.FONT_SMALL_BOLD);
        lFinder.setForeground(UITheme.TEXT_MUTED);
        lblFinderContact = new JLabel("-");
        lblFinderContact.setFont(UITheme.FONT_BODY);

        dtBody.add(lOwner);
        dtBody.add(lblOwnerContact);
        dtBody.add(Box.createVerticalStrut(8));
        dtBody.add(lFinder);
        dtBody.add(lblFinderContact);
        dtBody.add(Box.createVerticalStrut(12));

        JLabel lNotes = new JLabel("Resolution & Verification Notes:");
        lNotes.setFont(UITheme.FONT_SMALL_BOLD);
        lNotes.setForeground(UITheme.TEXT_MUTED);
        dtBody.add(lNotes);
        dtBody.add(Box.createVerticalStrut(4));

        txtResolutionNotes = new JTextArea("Select a recovery report from the table to view the audit record.");
        txtResolutionNotes.setFont(UITheme.FONT_BODY);
        txtResolutionNotes.setForeground(UITheme.TEXT_SECONDARY);
        txtResolutionNotes.setLineWrap(true);
        txtResolutionNotes.setWrapStyleWord(true);
        txtResolutionNotes.setEditable(false);
        txtResolutionNotes.setBackground(UITheme.BG_MAIN);
        txtResolutionNotes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JScrollPane notesScroll = new JScrollPane(txtResolutionNotes);
        notesScroll.setBorder(null);
        notesScroll.setPreferredSize(new Dimension(notesScroll.getPreferredSize().width, 100));
        dtBody.add(notesScroll);

        detailCard.add(dtBody, BorderLayout.CENTER);

        // Actions
        JPanel dtActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        dtActions.setOpaque(false);

        btnCopyClipboard = UITheme.createSecondaryButton("Copy Summary");
        btnCopyClipboard.setEnabled(false);
        btnCopyClipboard.addActionListener(e -> copySummaryToClipboard());

        btnExportFile = UITheme.createPrimaryButton("Export Report Text");
        btnExportFile.setEnabled(false);
        btnExportFile.addActionListener(e -> exportReportToFile());

        dtActions.add(btnCopyClipboard);
        dtActions.add(btnExportFile);
        detailCard.add(dtActions, BorderLayout.SOUTH);

        splitContainer.add(detailCard);
        mainPanel.add(splitContainer);

        JScrollPane outerScroll = new JScrollPane(mainPanel);
        outerScroll.setBorder(null);
        outerScroll.getVerticalScrollBar().setUnitIncrement(14);
        add(outerScroll, BorderLayout.CENTER);
    }

    private void showSelectedReportDetails() {
        int row = reportsTable.getSelectedRow();
        if (row >= 0 && reportsList != null && row < reportsList.size()) {
            RecoveryReport r = reportsList.get(row);
            lblReportId.setText("Recovery Record #" + r.getReportId());
            lblReportItems.setText("Lost: #" + r.getLostItemId() + " (" + r.getLostItemName() + ") <-> Found: #" + r.getFoundItemId() + " (" + r.getFoundItemName() + ")");
            lblReportDate.setText("Recovered On: " + DateTimeUtils.formatDisplay(r.getRecoveryDate()));

            badgeReportScore.setText(r.getMatchScore() + " / 100");
            badgeReportLevel.setText(r.getMatchLevel().getLabel());
            switch (r.getMatchLevel()) {
                case VERY_STRONG_MATCH:
                    badgeReportScore.setColors(UITheme.SUCCESS_TEXT, UITheme.SUCCESS_BG);
                    badgeReportLevel.setColors(UITheme.SUCCESS_TEXT, UITheme.SUCCESS_BG);
                    break;
                case STRONG_MATCH:
                    badgeReportScore.setColors(UITheme.TEAL_TEXT, UITheme.TEAL_BG);
                    badgeReportLevel.setColors(UITheme.TEAL_TEXT, UITheme.TEAL_BG);
                    break;
                default:
                    badgeReportScore.setColors(UITheme.WARNING_TEXT, UITheme.WARNING_BG);
                    badgeReportLevel.setColors(UITheme.WARNING_TEXT, UITheme.WARNING_BG);
                    break;
            }

            lblOwnerContact.setText(r.getOwnerContact());
            lblFinderContact.setText(r.getFinderContact());
            txtResolutionNotes.setText(r.getResolutionNotes());

            btnCopyClipboard.setEnabled(true);
            btnExportFile.setEnabled(true);
        } else {
            lblReportId.setText("Select a Report");
            lblReportItems.setText("Lost: - <-> Found: -");
            lblReportDate.setText("Date: -");
            badgeReportScore.setText("-- / 100");
            badgeReportLevel.setText("N/A");
            badgeReportScore.setColors(UITheme.TEXT_MUTED, UITheme.BG_CARD_ALT);
            badgeReportLevel.setColors(UITheme.TEXT_MUTED, UITheme.BG_CARD_ALT);
            lblOwnerContact.setText("-");
            lblFinderContact.setText("-");
            txtResolutionNotes.setText("Select a recovery report from the table to view the audit record.");

            btnCopyClipboard.setEnabled(false);
            btnExportFile.setEnabled(false);
        }
    }

    private RecoveryReport getSelectedReport() {
        int row = reportsTable.getSelectedRow();
        if (row >= 0 && reportsList != null && row < reportsList.size()) {
            return reportsList.get(row);
        }
        return null;
    }

    private void copySummaryToClipboard() {
        RecoveryReport r = getSelectedReport();
        if (r != null) {
            String text = r.generateSummary() + "\nOwner: " + r.getOwnerContact() + "\nFinder: " + r.getFinderContact() + "\nNotes: " + r.getResolutionNotes();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            JOptionPane.showMessageDialog(this, "Report summary copied to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportReportToFile() {
        RecoveryReport r = getSelectedReport();
        if (r == null) return;

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("report_" + r.getReportId() + ".txt"));
        int res = fc.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("==================================================\n");
                fw.write("         TRACEIT OFFICIAL RECOVERY REPORT         \n");
                fw.write("==================================================\n");
                fw.write("REPORT ID        : " + r.getReportId() + "\n");
                fw.write("RECOVERY DATE    : " + DateTimeUtils.formatDisplay(r.getRecoveryDate()) + "\n");
                fw.write("MATCH SCORE      : " + r.getMatchScore() + " / 100\n");
                fw.write("MATCH LEVEL      : " + r.getMatchLevel().getLabel() + "\n\n");
                fw.write("LOST ITEM        : #" + r.getLostItemId() + " - " + r.getLostItemName() + "\n");
                fw.write("OWNER CONTACT    : " + r.getOwnerContact() + "\n\n");
                fw.write("FOUND ITEM       : #" + r.getFoundItemId() + " - " + r.getFoundItemName() + "\n");
                fw.write("FINDER CONTACT   : " + r.getFinderContact() + "\n\n");
                fw.write("RESOLUTION NOTES :\n" + r.getResolutionNotes() + "\n");
                fw.write("==================================================\n");
                JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + file.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onDataChanged() {
        refreshReports();
    }

    private void refreshReports() {
        reportsList = context.getRecoveryService().getAllRecoveryReports();
        reportsModel.setRowCount(0);

        int total = reportsList.size();
        int sumScore = 0;
        int maxScore = 0;

        for (RecoveryReport r : reportsList) {
            sumScore += r.getMatchScore();
            if (r.getMatchScore() > maxScore) maxScore = r.getMatchScore();

            reportsModel.addRow(new Object[]{
                    "#" + r.getReportId(),
                    "#" + r.getLostItemId() + " " + r.getLostItemName(),
                    "#" + r.getFoundItemId() + " " + r.getFoundItemName(),
                    r.getMatchScore(),
                    r.getMatchLevel(),
                    DateTimeUtils.formatDisplay(r.getRecoveryDate())
            });
        }

        int avgScore = total > 0 ? Math.round((float) sumScore / total) : 0;
        statTotalReports.setValue(String.valueOf(total));
        statAvgScore.setValue(avgScore + " / 100");
        statHighestScore.setValue(maxScore + " / 100");

        if (reportsModel.getRowCount() > 0) {
            reportsTable.setRowSelectionInterval(0, 0);
        } else {
            showSelectedReportDetails();
        }
    }
}


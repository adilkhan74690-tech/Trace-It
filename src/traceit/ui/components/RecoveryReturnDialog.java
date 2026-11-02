package traceit.ui.components;

import traceit.exception.TraceItException;
import traceit.model.FoundItem;
import traceit.model.LostItem;
import traceit.model.MatchResult;
import traceit.model.RecoveryReport;
import traceit.ui.AppContext;
import traceit.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RecoveryReturnDialog extends JDialog {

    private final String lostItemId;
    private final String foundItemId;
    private final AppContext context;
    private boolean recoveryCompleted = false;

    public RecoveryReturnDialog(Window owner, String lostItemId, String foundItemId, AppContext context) {
        super(owner, "Complete Recovery & Return", ModalityType.APPLICATION_MODAL);
        this.lostItemId = lostItemId;
        this.foundItemId = foundItemId;
        this.context = context;
        initComponents();
    }

    private void initComponents() {
        setSize(520, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_CARD);

        LostItem lost = null;
        FoundItem found = null;
        MatchResult match = null;
        try {
            lost = context.getLostItemService().getById(lostItemId);
            found = context.getFoundItemService().getById(foundItemId);
            match = context.getRecoveryService().analyzePair(lostItemId, foundItemId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load item details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_CARD_ALT);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel titleLbl = new JLabel("Complete Item Return");
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subLbl = new JLabel("Verify custody transfer and log formal recovery report");
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_SECONDARY);

        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subLbl, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Comparison Summary Box
        JPanel summaryBox = new JPanel(new GridLayout(2, 2, 10, 8));
        summaryBox.setBackground(UITheme.BG_MAIN);
        summaryBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel l1 = new JLabel("Lost Item: #" + lostItemId);
        l1.setFont(UITheme.FONT_SMALL_BOLD);
        JLabel l2 = new JLabel(lost != null ? lost.getName() : "");
        l2.setFont(UITheme.FONT_BODY);

        JLabel f1 = new JLabel("Found Item: #" + foundItemId);
        f1.setFont(UITheme.FONT_SMALL_BOLD);
        JLabel f2 = new JLabel(found != null ? found.getName() : "");
        f2.setFont(UITheme.FONT_BODY);

        summaryBox.add(l1);
        summaryBox.add(l2);
        summaryBox.add(f1);
        summaryBox.add(f2);

        content.add(summaryBox);
        content.add(Box.createVerticalStrut(12));

        if (match != null) {
            JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            scoreRow.setOpaque(false);
            scoreRow.add(new JLabel("Match Score:"));
            scoreRow.add(BadgeLabel.forScore(match.getTotalScore()));
            scoreRow.add(BadgeLabel.forMatchLevel(match.getMatchLevel()));
            content.add(scoreRow);
            content.add(Box.createVerticalStrut(14));
        }

        JLabel notesLbl = new JLabel("Resolution & Verification Notes:");
        notesLbl.setFont(UITheme.FONT_SMALL_BOLD);
        notesLbl.setForeground(UITheme.TEXT_SECONDARY);
        content.add(notesLbl);
        content.add(Box.createVerticalStrut(6));

        ModernTextArea notesArea = new ModernTextArea(4, 20);
        notesArea.setText("Owner verified identity and matched distinctive item features. Handover confirmed.");
        JScrollPane notesScroll = ModernTextArea.createScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(notesScroll.getPreferredSize().width, 100));
        content.add(notesScroll);
        content.add(Box.createVerticalStrut(10));

        JLabel noteHint = new JLabel("Note: Both items will be marked RETURNED and stored in recovery reports.");
        noteHint.setFont(UITheme.FONT_SMALL);
        noteHint.setForeground(UITheme.TEXT_MUTED);
        content.add(noteHint);

        add(content, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(UITheme.BG_CARD_ALT);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton confirmBtn = UITheme.createSuccessButton("Confirm Return & Generate Report");
        confirmBtn.addActionListener(e -> {
            String notes = notesArea.getText().trim();
            try {
                RecoveryReport report = context.getRecoveryService().markAsReturned(lostItemId, foundItemId, notes);
                context.notifyDataChanged();
                recoveryCompleted = true;
                JOptionPane.showMessageDialog(this,
                        "Recovery successfully recorded!\nGenerated Report ID: #" + report.getReportId() +
                                "\nStatus for items #" + lostItemId + " and #" + foundItemId + " updated to RETURNED.",
                        "Recovery Completed",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (TraceItException ex) {
                JOptionPane.showMessageDialog(this, "Failed to record recovery: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(cancelBtn);
        footer.add(confirmBtn);
        add(footer, BorderLayout.SOUTH);
    }

    public boolean isRecoveryCompleted() {
        return recoveryCompleted;
    }
}


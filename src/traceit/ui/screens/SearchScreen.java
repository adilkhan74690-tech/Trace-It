package traceit.ui.screens;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SearchScreen extends JPanel implements AppContext.DataChangeListener {

    private final AppContext context;

    // Filters
    private ModernTextField tfKeyword;
    private ModernComboBox<String> cbItemType;
    private ModernComboBox<CategoryFilterItem> cbCategory;
    private ModernComboBox<StatusFilterItem> cbStatus;
    private ModernTextField tfColor;
    private ModernTextField tfBrand;
    private ModernTextField tfLocation;

    private JButton btnSearch;
    private JButton btnReset;

    // Results
    private JLabel lblResultsCount;
    private ModernTable resultsTable;
    private DefaultTableModel resultsModel;
    private List<Item> displayedItems = new ArrayList<>();

    // Action buttons
    private JButton btnViewDetails;
    private JButton btnUpdateStatus;
    private JButton btnFindMatches;

    public SearchScreen(AppContext context) {
        this.context = context;
        context.addDataChangeListener(this);
        initComponents();
        executeSearch();
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

        JLabel titleLbl = new JLabel("Directory & Multi-Attribute Search");
        titleLbl.setFont(UITheme.FONT_HEADER_LARGE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitleLbl = new JLabel("Filter and query through campus lost and found records across multiple attributes");
        subtitleLbl.setFont(UITheme.FONT_BODY);
        subtitleLbl.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(titleLbl);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLbl);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(18));

        // Filter Card
        JPanel filterCard = new JPanel(new BorderLayout(0, 12));
        filterCard.setBackground(UITheme.BG_CARD);
        filterCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel filterTitle = new JLabel("Search Filters");
        filterTitle.setFont(UITheme.FONT_SUBTITLE);
        filterTitle.setForeground(UITheme.TEXT_PRIMARY);
        filterCard.add(filterTitle, BorderLayout.NORTH);

        JPanel filterGrid = new JPanel(new GridLayout(2, 4, 12, 10));
        filterGrid.setOpaque(false);

        tfKeyword = new ModernTextField("Keyword, Name, ID, or Features");
        cbItemType = new ModernComboBox<>(new String[]{"All Item Types", "Lost Items Only", "Found Items Only"});

        cbCategory = new ModernComboBox<>();
        cbCategory.addItem(new CategoryFilterItem(null, "All Categories"));
        for (ItemCategory cat : ItemCategory.values()) {
            cbCategory.addItem(new CategoryFilterItem(cat, cat.getDisplayName()));
        }

        cbStatus = new ModernComboBox<>();
        cbStatus.addItem(new StatusFilterItem(null, "All Statuses"));
        for (ItemStatus stat : ItemStatus.values()) {
            cbStatus.addItem(new StatusFilterItem(stat, stat.getDisplayName()));
        }

        tfColor = new ModernTextField("Filter by Color (e.g. Black)");
        tfBrand = new ModernTextField("Filter by Brand (e.g. Apple)");
        tfLocation = new ModernTextField("Filter by Location (e.g. Library)");

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);

        btnReset = UITheme.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> resetFilters());

        btnSearch = UITheme.createPrimaryButton("Search Directory");
        btnSearch.addActionListener(e -> executeSearch());

        btnBar.add(btnReset);
        btnBar.add(btnSearch);

        filterGrid.add(createField("Search Keyword", tfKeyword));
        filterGrid.add(createField("Item Type", cbItemType));
        filterGrid.add(createField("Category", cbCategory));
        filterGrid.add(createField("Status", cbStatus));
        filterGrid.add(createField("Color", tfColor));
        filterGrid.add(createField("Brand", tfBrand));
        filterGrid.add(createField("Location", tfLocation));
        filterGrid.add(createField("Action", btnBar));

        filterCard.add(filterGrid, BorderLayout.CENTER);
        mainPanel.add(filterCard);
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

        JLabel resTitle = new JLabel("Search Results");
        resTitle.setFont(UITheme.FONT_SUBTITLE);
        resTitle.setForeground(UITheme.TEXT_PRIMARY);

        lblResultsCount = new JLabel("0 items matching criteria");
        lblResultsCount.setFont(UITheme.FONT_SMALL);
        lblResultsCount.setForeground(UITheme.TEXT_MUTED);

        resHeader.add(resTitle, BorderLayout.WEST);
        resHeader.add(lblResultsCount, BorderLayout.EAST);
        resultsCard.add(resHeader, BorderLayout.NORTH);

        // Results Table
        resultsModel = new DefaultTableModel(
                new Object[]{"Type", "ID", "Name", "Category", "Color / Brand", "Location", "Date / Time", "Status", "Contact / Custody"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) return ItemStatus.class;
                return String.class;
            }
        };

        resultsTable = new ModernTable(resultsModel);
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        resultsTable.getColumnModel().getColumn(5).setPreferredWidth(140);
        resultsTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        resultsTable.getColumnModel().getColumn(7).setPreferredWidth(130);
        resultsTable.getColumnModel().getColumn(8).setPreferredWidth(150);

        // Type renderer
        resultsTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
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

        resultsTable.getSelectionModel().addListSelectionListener(e -> updateActionButtons());
        resultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewDetails();
                }
            }
        });

        JScrollPane tableScroll = ModernTable.createScrollPane(resultsTable);
        tableScroll.setPreferredSize(new Dimension(tableScroll.getPreferredSize().width, 320));
        resultsCard.add(tableScroll, BorderLayout.CENTER);

        // Action Toolbar Below Results
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actionToolbar.setOpaque(false);

        btnFindMatches = UITheme.createPrimaryButton("Run Smart Matching");
        btnFindMatches.setEnabled(false);
        btnFindMatches.addActionListener(e -> findMatchesForSelected());

        btnUpdateStatus = UITheme.createSecondaryButton("Update Status");
        btnUpdateStatus.setEnabled(false);
        btnUpdateStatus.addActionListener(e -> updateStatusForSelected());

        btnViewDetails = UITheme.createOutlineButton("View Full Details");
        btnViewDetails.setEnabled(false);
        btnViewDetails.addActionListener(e -> viewDetails());

        actionToolbar.add(btnViewDetails);
        actionToolbar.add(btnUpdateStatus);
        actionToolbar.add(btnFindMatches);

        resultsCard.add(actionToolbar, BorderLayout.SOUTH);
        mainPanel.add(resultsCard);

        JScrollPane outerScroll = new JScrollPane(mainPanel);
        outerScroll.setBorder(null);
        outerScroll.getVerticalScrollBar().setUnitIncrement(14);
        add(outerScroll, BorderLayout.CENTER);
    }

    private JPanel createField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_SMALL_BOLD);
        l.setForeground(UITheme.TEXT_SECONDARY);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void resetFilters() {
        tfKeyword.setText("");
        cbItemType.setSelectedIndex(0);
        cbCategory.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        tfColor.setText("");
        tfBrand.setText("");
        tfLocation.setText("");
        executeSearch();
    }

    public void executeSearch() {
        String keyword = tfKeyword.getText().trim();
        int typeIdx = cbItemType.getSelectedIndex();
        CategoryFilterItem catItem = (CategoryFilterItem) cbCategory.getSelectedItem();
        ItemCategory category = catItem != null ? catItem.category : null;
        StatusFilterItem statItem = (StatusFilterItem) cbStatus.getSelectedItem();
        ItemStatus status = statItem != null ? statItem.status : null;
        String color = tfColor.getText().trim();
        String brand = tfBrand.getText().trim();
        String location = tfLocation.getText().trim();

        List<Item> allResults = context.getSearchService().searchAll(keyword, category, color, brand, location, status);

        displayedItems.clear();
        for (Item it : allResults) {
            if (typeIdx == 1 && !(it instanceof LostItem)) continue;
            if (typeIdx == 2 && !(it instanceof FoundItem)) continue;
            displayedItems.add(it);
        }

        resultsModel.setRowCount(0);
        for (Item item : displayedItems) {
            String contactOrStorage = "N/A";
            if (item instanceof LostItem) {
                LostItem l = (LostItem) item;
                contactOrStorage = l.getReporter() != null ? l.getReporter().getName() + " (" + l.getReporter().getPhone() + ")" : "N/A";
            } else if (item instanceof FoundItem) {
                FoundItem f = (FoundItem) item;
                contactOrStorage = f.getStorageLocation();
            }

            resultsModel.addRow(new Object[]{
                    item.getReportType(),
                    item.getId(),
                    item.getName(),
                    item.getCategory().getDisplayName(),
                    item.getColor() + " / " + item.getBrand(),
                    item.getLocation() != null ? item.getLocation().getRawLocation() : "N/A",
                    DateTimeUtils.formatDisplay(item.getDateTime()),
                    item.getStatus(),
                    contactOrStorage
            });
        }

        lblResultsCount.setText(displayedItems.size() + " item(s) matching criteria");
        updateActionButtons();
    }

    private void updateActionButtons() {
        int sel = resultsTable.getSelectedRow();
        boolean hasSel = sel >= 0 && sel < displayedItems.size();
        btnViewDetails.setEnabled(hasSel);
        btnUpdateStatus.setEnabled(hasSel);

        if (hasSel) {
            Item item = displayedItems.get(sel);
            btnFindMatches.setEnabled(item instanceof LostItem);
        } else {
            btnFindMatches.setEnabled(false);
        }
    }

    private void viewDetails() {
        int sel = resultsTable.getSelectedRow();
        if (sel >= 0 && sel < displayedItems.size()) {
            Item item = displayedItems.get(sel);
            Window win = SwingUtilities.getWindowAncestor(this);
            ItemDetailDialog dlg = new ItemDetailDialog(win, item, context);
            dlg.setVisible(true);
        }
    }

    private void updateStatusForSelected() {
        int sel = resultsTable.getSelectedRow();
        if (sel >= 0 && sel < displayedItems.size()) {
            Item item = displayedItems.get(sel);
            Window win = SwingUtilities.getWindowAncestor(this);
            StatusUpdateDialog dlg = new StatusUpdateDialog(win, item, context);
            dlg.setVisible(true);
        }
    }

    private void findMatchesForSelected() {
        int sel = resultsTable.getSelectedRow();
        if (sel >= 0 && sel < displayedItems.size()) {
            Item item = displayedItems.get(sel);
            if (item instanceof LostItem) {
                context.navigateToSmartMatching(item.getId());
            }
        }
    }

    @Override
    public void onDataChanged() {
        executeSearch();
    }

    private static class CategoryFilterItem {
        final ItemCategory category;
        final String label;

        CategoryFilterItem(ItemCategory category, String label) {
            this.category = category;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class StatusFilterItem {
        final ItemStatus status;
        final String label;

        StatusFilterItem(ItemStatus status, String label) {
            this.status = status;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}

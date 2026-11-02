package traceit.ui;

import traceit.ui.screens.*;
import traceit.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

public class TraceItApp extends JFrame {

    private final AppContext context;

    private JPanel sidebarPanel;
    private JPanel contentContainer;
    private CardLayout cardLayout;

    private final Map<String, SidebarButton> navButtons = new LinkedHashMap<>();
    private String currentScreenKey = "DASHBOARD";

    // Screens
    private DashboardScreen dashboardScreen;
    private ReportLostScreen reportLostScreen;
    private ReportFoundScreen reportFoundScreen;
    private SmartMatchingScreen smartMatchingScreen;
    private MatchAnalysisScreen matchAnalysisScreen;
    private SearchScreen searchScreen;
    private RecoveryManagementScreen recoveryManagementScreen;
    private ReportsScreen reportsScreen;

    public TraceItApp() {
        super("TraceIt - Smart Lost-Item Recovery & Matching Engine");
        this.context = new AppContext();
        initComponents();
        setupNavigationHandlers();
        navigateTo("DASHBOARD");
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 840);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());

        // Root Layout
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);

        // Sidebar
        sidebarPanel = createSidebar();
        root.add(sidebarPanel, BorderLayout.WEST);

        // Content Area with CardLayout
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(UITheme.BG_MAIN);

        dashboardScreen = new DashboardScreen(context);
        reportLostScreen = new ReportLostScreen(context);
        reportFoundScreen = new ReportFoundScreen(context);
        smartMatchingScreen = new SmartMatchingScreen(context);
        matchAnalysisScreen = new MatchAnalysisScreen(context);
        searchScreen = new SearchScreen(context);
        recoveryManagementScreen = new RecoveryManagementScreen(context);
        reportsScreen = new ReportsScreen(context);

        contentContainer.add(dashboardScreen, "DASHBOARD");
        contentContainer.add(reportLostScreen, "REPORT_LOST");
        contentContainer.add(reportFoundScreen, "REPORT_FOUND");
        contentContainer.add(smartMatchingScreen, "SMART_MATCHING");
        contentContainer.add(matchAnalysisScreen, "MATCH_ANALYSIS");
        contentContainer.add(searchScreen, "SEARCH");
        contentContainer.add(recoveryManagementScreen, "RECOVERY_MANAGEMENT");
        contentContainer.add(reportsScreen, "REPORTS");

        root.add(contentContainer, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));

        // Brand Banner Header
        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(new EmptyBorder(24, 20, 24, 20));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoRow.setOpaque(false);

        JLabel logoBadge = new JLabel("TI", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyAntialiasing(g2);
                g2.setColor(UITheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoBadge.setFont(new Font(UITheme.FONT_HEADER.getName(), Font.BOLD, 14));
        logoBadge.setForeground(Color.WHITE);
        logoBadge.setPreferredSize(new Dimension(34, 34));
        logoBadge.setOpaque(false);

        JLabel brandTitle = new JLabel("TraceIt");
        brandTitle.setFont(new Font(UITheme.FONT_HEADER.getName(), Font.BOLD, 20));
        brandTitle.setForeground(Color.WHITE);

        logoRow.add(logoBadge);
        logoRow.add(brandTitle);

        JLabel brandSlogan = new JLabel("\"Lost it? Trace it.\"");
        brandSlogan.setFont(UITheme.FONT_SMALL);
        brandSlogan.setForeground(UITheme.SIDEBAR_TEXT);
        brandSlogan.setBorder(new EmptyBorder(6, 44, 0, 0));

        brandPanel.add(logoRow);
        brandPanel.add(brandSlogan);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Navigation Items
        JPanel navList = new JPanel();
        navList.setOpaque(false);
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));
        navList.setBorder(new EmptyBorder(8, 12, 12, 12));

        addNavItem(navList, "DASHBOARD", "Dashboard", "📊");
        addNavSeparator(navList, "REPORTS & INTAKE");
        addNavItem(navList, "REPORT_LOST", "Report Lost Item", "📦");
        addNavItem(navList, "REPORT_FOUND", "Report Found Item", "📥");
        addNavSeparator(navList, "MATCHING ENGINE");
        addNavItem(navList, "SMART_MATCHING", "Smart Matching", "⚡");
        addNavItem(navList, "MATCH_ANALYSIS", "Match Analysis", "🔍");
        addNavSeparator(navList, "OPERATIONS");
        addNavItem(navList, "SEARCH", "Search Directory", "🔎");
        addNavItem(navList, "RECOVERY_MANAGEMENT", "Recovery Lifecycle", "🛡️");
        addNavItem(navList, "REPORTS", "Recovery Reports", "📋");

        JScrollPane navScroll = new JScrollPane(navList);
        navScroll.setBorder(null);
        navScroll.setOpaque(false);
        navScroll.getViewport().setOpaque(false);
        sidebar.add(navScroll, BorderLayout.CENTER);

        // Sidebar Footer
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new EmptyBorder(16, 20, 20, 20));

        JLabel statusIndicator = new JLabel("● Storage: File DB (data/)");
        statusIndicator.setFont(UITheme.FONT_SMALL);
        statusIndicator.setForeground(UITheme.SUCCESS_SOLID);

        JLabel versionLbl = new JLabel("TraceIt Desktop Edition v2.0");
        versionLbl.setFont(UITheme.FONT_SMALL);
        versionLbl.setForeground(UITheme.TEXT_MUTED);

        footer.add(statusIndicator);
        footer.add(Box.createVerticalStrut(4));
        footer.add(versionLbl);

        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addNavSeparator(JPanel parent, String title) {
        parent.add(Box.createVerticalStrut(10));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font(UITheme.FONT_SMALL.getName(), Font.BOLD, 10));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setBorder(new EmptyBorder(4, 14, 4, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
    }

    private void addNavItem(JPanel parent, String key, String label, String iconSymbol) {
        SidebarButton btn = new SidebarButton(key, label, iconSymbol);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateTo(key);
            }
        });
        navButtons.put(key, btn);
        parent.add(btn);
        parent.add(Box.createVerticalStrut(4));
    }

    public void navigateTo(String screenKey) {
        this.currentScreenKey = screenKey;
        cardLayout.show(contentContainer, screenKey);

        for (Map.Entry<String, SidebarButton> entry : navButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(screenKey));
        }

        // Trigger on-show refresh if screen needs it
        if ("DASHBOARD".equals(screenKey)) {
            dashboardScreen.refreshData();
        } else if ("SEARCH".equals(screenKey)) {
            searchScreen.executeSearch();
        }
    }

    private void setupNavigationHandlers() {
        context.setScreenNavigator(this::navigateTo);

        context.setSmartMatchTargetHandler(lostItemId -> {
            smartMatchingScreen.setTargetLostItem(lostItemId);
        });

        context.setMatchAnalysisTargetHandler((lostItemId, foundItemId) -> {
            matchAnalysisScreen.setTargetPair(lostItemId, foundItemId);
        });
    }

    private Image createAppIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        UITheme.applyAntialiasing(g2);
        g2.setColor(UITheme.PRIMARY);
        g2.fillRoundRect(0, 0, 32, 32, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("T", (32 - fm.stringWidth("T")) / 2, 22);
        g2.dispose();
        return image;
    }

    public static void launch() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            TraceItApp app = new TraceItApp();
            app.setVisible(true);
        });
    }

    // Inner Sidebar Button Component
    private static class SidebarButton extends JPanel {
        private final String key;
        private final String label;
        private final String iconSymbol;
        private boolean isActive = false;
        private boolean isHovered = false;

        private final JLabel textLabel;
        private final JLabel iconLabel;

        SidebarButton(String key, String label, String iconSymbol) {
            this.key = key;
            this.label = label;
            this.iconSymbol = iconSymbol;

            setLayout(new BorderLayout(10, 0));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 14, 9, 14));
            setMaximumSize(new Dimension(230, 40));
            setPreferredSize(new Dimension(225, 40));

            iconLabel = new JLabel(iconSymbol);
            iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            iconLabel.setForeground(UITheme.SIDEBAR_TEXT);

            textLabel = new JLabel(label);
            textLabel.setFont(UITheme.FONT_BODY_BOLD);
            textLabel.setForeground(UITheme.SIDEBAR_TEXT);

            add(iconLabel, BorderLayout.WEST);
            add(textLabel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                textLabel.setForeground(UITheme.SIDEBAR_TEXT_ACTIVE);
                iconLabel.setForeground(UITheme.SIDEBAR_TEXT_ACTIVE);
            } else {
                textLabel.setForeground(UITheme.SIDEBAR_TEXT);
                iconLabel.setForeground(UITheme.SIDEBAR_TEXT);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            UITheme.applyAntialiasing(g2);

            if (isActive) {
                g2.setColor(UITheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            } else if (isHovered) {
                g2.setColor(UITheme.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}


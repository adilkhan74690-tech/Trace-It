package traceit.ui.screens;

import traceit.exception.InvalidItemException;
import traceit.model.ItemCategory;
import traceit.model.Location;
import traceit.model.LostItem;
import traceit.model.User;
import traceit.ui.AppContext;
import traceit.ui.components.ModernComboBox;
import traceit.ui.components.ModernTextArea;
import traceit.ui.components.ModernTextField;
import traceit.ui.theme.UITheme;
import traceit.util.DateTimeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;

public class ReportLostScreen extends JPanel {

    private final AppContext context;

    // Item Fields
    private ModernTextField tfName;
    private ModernComboBox<ItemCategory> cbCategory;
    private ModernTextField tfColor;
    private ModernTextField tfBrand;
    private ModernTextField tfLocation;
    private ModernTextField tfDateTime;
    private ModernTextField tfDistinctiveFeatures;
    private ModernTextArea taDescription;

    // Reporter Fields
    private ModernTextField tfReporterName;
    private ModernTextField tfReporterEmail;
    private ModernTextField tfReporterPhone;
    private ModernComboBox<String> cbReporterRole;
    private ModernTextField tfReward;
    private ModernComboBox<String> cbContactPref;

    public ReportLostScreen(AppContext context) {
        this.context = context;
        initComponents();
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

        JLabel titleLbl = new JLabel("Report Lost Item");
        titleLbl.setFont(UITheme.FONT_HEADER_LARGE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitleLbl = new JLabel("File a detailed lost-item incident to enable automated matching across campus");
        subtitleLbl.setFont(UITheme.FONT_BODY);
        subtitleLbl.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(titleLbl);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLbl);

        JButton fillSampleBtn = UITheme.createOutlineButton("Fill Sample Data");
        fillSampleBtn.addActionListener(e -> prefillSampleData());

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(fillSampleBtn, BorderLayout.EAST);

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Form Cards Container
        JPanel formContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        formContainer.setOpaque(false);

        // Left Card: Item Details
        JPanel itemCard = createCardPanel("Item Information");
        JPanel itemFields = new JPanel(new GridLayout(0, 1, 0, 10));
        itemFields.setOpaque(false);

        tfName = new ModernTextField("e.g. MacBook Pro M2, Leather Wallet");
        cbCategory = new ModernComboBox<>(ItemCategory.values());
        tfColor = new ModernTextField("e.g. Space Gray, Navy Blue");
        tfBrand = new ModernTextField("e.g. Apple, Bellroy, Samsung");
        tfLocation = new ModernTextField("e.g. Central Library 2nd Floor, Science Lab 3");
        tfDateTime = new ModernTextField(DateTimeUtils.formatDisplay(LocalDateTime.now()), "yyyy-MM-dd HH:mm");
        tfDistinctiveFeatures = new ModernTextField("e.g. NASA sticker on lid, scratched corner");

        taDescription = new ModernTextArea(4, 20);
        JScrollPane descScroll = ModernTextArea.createScrollPane(taDescription);
        descScroll.setPreferredSize(new Dimension(descScroll.getPreferredSize().width, 85));

        itemFields.add(createFieldWithLabel("Item Name *", tfName));
        itemFields.add(createFieldWithLabel("Category *", cbCategory));
        itemFields.add(createFieldWithLabel("Color *", tfColor));
        itemFields.add(createFieldWithLabel("Brand / Manufacturer", tfBrand));
        itemFields.add(createFieldWithLabel("Lost Location *", tfLocation));
        itemFields.add(createFieldWithLabel("Estimated Date & Time (yyyy-MM-dd HH:mm) *", tfDateTime));
        itemFields.add(createFieldWithLabel("Distinctive Features / Markings", tfDistinctiveFeatures));

        itemCard.add(itemFields, BorderLayout.CENTER);

        JPanel descPanel = new JPanel(new BorderLayout(0, 4));
        descPanel.setOpaque(false);
        descPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        JLabel descLbl = new JLabel("Description & Circumstances *");
        descLbl.setFont(UITheme.FONT_SMALL_BOLD);
        descLbl.setForeground(UITheme.TEXT_SECONDARY);
        descPanel.add(descLbl, BorderLayout.NORTH);
        descPanel.add(descScroll, BorderLayout.CENTER);
        itemCard.add(descPanel, BorderLayout.SOUTH);

        formContainer.add(itemCard);

        // Right Card: Reporter Contact Details
        JPanel reporterCard = createCardPanel("Reporter Contact Information");
        JPanel reporterFields = new JPanel(new GridLayout(0, 1, 0, 10));
        reporterFields.setOpaque(false);

        tfReporterName = new ModernTextField("Your full name");
        tfReporterEmail = new ModernTextField("campus email address");
        tfReporterPhone = new ModernTextField("active contact phone");
        cbReporterRole = new ModernComboBox<>(new String[]{"Student", "Faculty", "Staff", "Visitor"});
        tfReward = new ModernTextField("0.0", "Reward in USD ($)");
        cbContactPref = new ModernComboBox<>(new String[]{"Email", "Phone", "Either"});

        reporterFields.add(createFieldWithLabel("Reporter Name *", tfReporterName));
        reporterFields.add(createFieldWithLabel("Email Address *", tfReporterEmail));
        reporterFields.add(createFieldWithLabel("Phone Number *", tfReporterPhone));
        reporterFields.add(createFieldWithLabel("Campus Role", cbReporterRole));
        reporterFields.add(createFieldWithLabel("Reward Offered ($)", tfReward));
        reporterFields.add(createFieldWithLabel("Preferred Contact Method", cbContactPref));

        reporterCard.add(reporterFields, BorderLayout.NORTH);

        // Help Information in Right Card
        JPanel helpBox = new JPanel(new BorderLayout());
        helpBox.setBackground(UITheme.BG_MAIN);
        helpBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel helpTitle = new JLabel("Why this information matters:");
        helpTitle.setFont(UITheme.FONT_SMALL_BOLD);
        helpTitle.setForeground(UITheme.PRIMARY_DARK);

        JTextArea helpText = new JTextArea("TraceIt's weighted matching engine compares category, color, brand, location proximity, timestamp variance, and keyword overlap in descriptions. Providing specific details maximizes the likelihood of an automated high-confidence recovery match.");
        helpText.setFont(UITheme.FONT_SMALL);
        helpText.setForeground(UITheme.TEXT_SECONDARY);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setEditable(false);
        helpText.setOpaque(false);

        helpBox.add(helpTitle, BorderLayout.NORTH);
        helpBox.add(helpText, BorderLayout.CENTER);
        reporterCard.add(helpBox, BorderLayout.SOUTH);

        formContainer.add(reporterCard);

        mainPanel.add(formContainer);
        mainPanel.add(Box.createVerticalStrut(20));

        // Submit Action Bar
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionBar.setOpaque(false);

        JButton clearBtn = UITheme.createSecondaryButton("Clear Form");
        clearBtn.addActionListener(e -> clearForm());

        JButton submitBtn = UITheme.createPrimaryButton("Submit Lost Item Report");
        submitBtn.addActionListener(e -> submitForm());

        actionBar.add(clearBtn);
        actionBar.add(submitBtn);

        mainPanel.add(actionBar);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBTITLE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);
        card.add(titleLbl, BorderLayout.NORTH);

        return card;
    }

    private JPanel createFieldWithLabel(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel l = new JLabel(labelText);
        l.setFont(UITheme.FONT_SMALL_BOLD);
        l.setForeground(UITheme.TEXT_SECONDARY);

        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void prefillSampleData() {
        tfName.setText("Sony WH-1000XM5 Headphones");
        cbCategory.setSelectedItem(ItemCategory.EARBUDS);
        tfColor.setText("Silver");
        tfBrand.setText("Sony");
        tfLocation.setText("Campus Library 3rd Floor Quiet Zone");
        tfDateTime.setText(DateTimeUtils.formatDisplay(LocalDateTime.now().minusHours(2)));
        tfDistinctiveFeatures.setText("Carbon black travel case with initials SK");
        taDescription.setText("Silver wireless over-ear noise-cancelling headphones left near carrel 14 in the library 3rd floor.");

        tfReporterName.setText("Siddharth Kumar");
        tfReporterEmail.setText("skumar@campus.edu");
        tfReporterPhone.setText("+1-555-0821");
        cbReporterRole.setSelectedItem("Student");
        tfReward.setText("25.0");
        cbContactPref.setSelectedItem("Email");
    }

    private void clearForm() {
        tfName.setText("");
        cbCategory.setSelectedIndex(0);
        tfColor.setText("");
        tfBrand.setText("");
        tfLocation.setText("");
        tfDateTime.setText(DateTimeUtils.formatDisplay(LocalDateTime.now()));
        tfDistinctiveFeatures.setText("");
        taDescription.setText("");

        tfReporterName.setText("");
        tfReporterEmail.setText("");
        tfReporterPhone.setText("");
        cbReporterRole.setSelectedIndex(0);
        tfReward.setText("0.0");
        cbContactPref.setSelectedIndex(0);
    }

    private void submitForm() {
        String name = tfName.getText().trim();
        ItemCategory category = (ItemCategory) cbCategory.getSelectedItem();
        String color = tfColor.getText().trim();
        String brand = tfBrand.getText().trim();
        if (brand.isEmpty()) brand = "Generic";
        String locationStr = tfLocation.getText().trim();
        String dtStr = tfDateTime.getText().trim();
        String features = tfDistinctiveFeatures.getText().trim();
        if (features.isEmpty()) features = "None";
        String description = taDescription.getText().trim();

        String repName = tfReporterName.getText().trim();
        String repEmail = tfReporterEmail.getText().trim();
        String repPhone = tfReporterPhone.getText().trim();
        String repRole = (String) cbReporterRole.getSelectedItem();
        String rewardStr = tfReward.getText().trim();
        String contactPref = (String) cbContactPref.getSelectedItem();

        // Validation
        if (name.isEmpty()) {
            showError("Please enter the item name.");
            tfName.requestFocus();
            return;
        }
        if (color.isEmpty()) {
            showError("Please specify the item color.");
            tfColor.requestFocus();
            return;
        }
        if (locationStr.isEmpty()) {
            showError("Please specify where the item was lost.");
            tfLocation.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            showError("Please provide a description of the lost item.");
            taDescription.requestFocus();
            return;
        }
        if (repName.isEmpty()) {
            showError("Please provide reporter name.");
            tfReporterName.requestFocus();
            return;
        }
        if (repEmail.isEmpty()) {
            showError("Please provide reporter email.");
            tfReporterEmail.requestFocus();
            return;
        }
        if (repPhone.isEmpty()) {
            showError("Please provide reporter phone.");
            tfReporterPhone.requestFocus();
            return;
        }

        LocalDateTime dt = DateTimeUtils.parse(dtStr);
        if (dt == null) {
            showError("Invalid Date/Time format. Please use 'yyyy-MM-dd HH:mm' (e.g. 2026-11-02 14:30)");
            tfDateTime.requestFocus();
            return;
        }

        double reward = 0.0;
        try {
            if (!rewardStr.isEmpty()) {
                reward = Double.parseDouble(rewardStr);
                if (reward < 0) reward = 0.0;
            }
        } catch (NumberFormatException e) {
            showError("Reward must be a valid number (e.g. 20.0).");
            tfReward.requestFocus();
            return;
        }

        User reporter = new User("U-" + (System.currentTimeMillis() % 10000), repName, repEmail, repPhone, repRole);
        LostItem item = new LostItem(null, name, category, color, brand, new Location(locationStr), dt, description, features, reporter, reward, contactPref);

        try {
            LostItem saved = context.getLostItemService().addLostItem(item);
            context.notifyDataChanged();

            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Lost Item successfully registered with ID: #" + saved.getId() + "!\n\nWould you like to run Smart Matching now?",
                    "Lost Item Reported",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"Run Smart Matching", "Done"},
                    "Run Smart Matching"
            );

            clearForm();

            if (choice == JOptionPane.YES_OPTION) {
                context.navigateToSmartMatching(saved.getId());
            } else {
                context.navigateTo("DASHBOARD");
            }

        } catch (InvalidItemException ex) {
            showError("Validation error: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Failed to save report: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Validation", JOptionPane.WARNING_MESSAGE);
    }
}


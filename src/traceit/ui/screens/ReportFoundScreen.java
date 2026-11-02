package traceit.ui.screens;

import traceit.exception.InvalidItemException;
import traceit.model.FoundItem;
import traceit.model.ItemCategory;
import traceit.model.Location;
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

public class ReportFoundScreen extends JPanel {

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

    // Custody & Finder Fields
    private ModernTextField tfFinderName;
    private ModernTextField tfFinderEmail;
    private ModernTextField tfFinderPhone;
    private ModernComboBox<String> cbFinderRole;
    private ModernTextField tfStorageLocation;
    private ModernTextField tfVerificationQuestion;

    public ReportFoundScreen(AppContext context) {
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

        JLabel titleLbl = new JLabel("Report Found Item");
        titleLbl.setFont(UITheme.FONT_HEADER_LARGE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitleLbl = new JLabel("Log an item turned in to campus custody and establish proof-of-ownership verification");
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

        // Left Card: Found Item Details
        JPanel itemCard = createCardPanel("Item Information");
        JPanel itemFields = new JPanel(new GridLayout(0, 1, 0, 10));
        itemFields.setOpaque(false);

        tfName = new ModernTextField("e.g. Wireless Earbuds, Silver Watch");
        cbCategory = new ModernComboBox<>(ItemCategory.values());
        tfColor = new ModernTextField("e.g. Silver, Black, White");
        tfBrand = new ModernTextField("e.g. Sony, Apple, Timex");
        tfLocation = new ModernTextField("e.g. Library 3rd Floor Quiet Zone");
        tfDateTime = new ModernTextField(DateTimeUtils.formatDisplay(LocalDateTime.now()), "yyyy-MM-dd HH:mm");
        tfDistinctiveFeatures = new ModernTextField("e.g. Initials SK on case, minor scratch");

        taDescription = new ModernTextArea(4, 20);
        JScrollPane descScroll = ModernTextArea.createScrollPane(taDescription);
        descScroll.setPreferredSize(new Dimension(descScroll.getPreferredSize().width, 85));

        itemFields.add(createFieldWithLabel("Item Name *", tfName));
        itemFields.add(createFieldWithLabel("Category *", cbCategory));
        itemFields.add(createFieldWithLabel("Color *", tfColor));
        itemFields.add(createFieldWithLabel("Brand / Manufacturer", tfBrand));
        itemFields.add(createFieldWithLabel("Found Location *", tfLocation));
        itemFields.add(createFieldWithLabel("Date & Time Found (yyyy-MM-dd HH:mm) *", tfDateTime));
        itemFields.add(createFieldWithLabel("Distinctive Features / Markings", tfDistinctiveFeatures));

        itemCard.add(itemFields, BorderLayout.CENTER);

        JPanel descPanel = new JPanel(new BorderLayout(0, 4));
        descPanel.setOpaque(false);
        descPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        JLabel descLbl = new JLabel("Description & Physical Condition *");
        descLbl.setFont(UITheme.FONT_SMALL_BOLD);
        descLbl.setForeground(UITheme.TEXT_SECONDARY);
        descPanel.add(descLbl, BorderLayout.NORTH);
        descPanel.add(descScroll, BorderLayout.CENTER);
        itemCard.add(descPanel, BorderLayout.SOUTH);

        formContainer.add(itemCard);

        // Right Card: Custody & Finder Information
        JPanel finderCard = createCardPanel("Custody & Verification Setup");
        JPanel finderFields = new JPanel(new GridLayout(0, 1, 0, 10));
        finderFields.setOpaque(false);

        tfFinderName = new ModernTextField("Finder / Custodian name");
        tfFinderEmail = new ModernTextField("contact email address");
        tfFinderPhone = new ModernTextField("contact phone number");
        cbFinderRole = new ModernComboBox<>(new String[]{"Staff", "Security", "Student", "Faculty", "Visitor"});
        tfStorageLocation = new ModernTextField("Campus Security Lost & Found Vault Room 102");
        tfVerificationQuestion = new ModernTextField("Describe contents or secret serial number");

        finderFields.add(createFieldWithLabel("Finder / Custodian Name *", tfFinderName));
        finderFields.add(createFieldWithLabel("Finder Email *", tfFinderEmail));
        finderFields.add(createFieldWithLabel("Finder Phone *", tfFinderPhone));
        finderFields.add(createFieldWithLabel("Campus Role", cbFinderRole));
        finderFields.add(createFieldWithLabel("Physical Storage / Custody Location *", tfStorageLocation));
        finderFields.add(createFieldWithLabel("Claim Verification Question *", tfVerificationQuestion));

        finderCard.add(finderFields, BorderLayout.NORTH);

        // Security Notice Box in Right Card
        JPanel noticeBox = new JPanel(new BorderLayout());
        noticeBox.setBackground(UITheme.BG_MAIN);
        noticeBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel noticeTitle = new JLabel("Proof of Ownership Protection:");
        noticeTitle.setFont(UITheme.FONT_SMALL_BOLD);
        noticeTitle.setForeground(UITheme.TEAL_TEXT);

        JTextArea noticeText = new JTextArea("The Verification Question ensures that only the rightful claimant can retrieve the item by asking for details not visible from the exterior (e.g., hidden marks, phone lock screen wallpaper, specific cards inside a wallet).");
        noticeText.setFont(UITheme.FONT_SMALL);
        noticeText.setForeground(UITheme.TEXT_SECONDARY);
        noticeText.setLineWrap(true);
        noticeText.setWrapStyleWord(true);
        noticeText.setEditable(false);
        noticeText.setOpaque(false);

        noticeBox.add(noticeTitle, BorderLayout.NORTH);
        noticeBox.add(noticeText, BorderLayout.CENTER);
        finderCard.add(noticeBox, BorderLayout.SOUTH);

        formContainer.add(finderCard);

        mainPanel.add(formContainer);
        mainPanel.add(Box.createVerticalStrut(20));

        // Submit Action Bar
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionBar.setOpaque(false);

        JButton clearBtn = UITheme.createSecondaryButton("Clear Form");
        clearBtn.addActionListener(e -> clearForm());

        JButton submitBtn = UITheme.createPrimaryButton("Submit Found Item Report");
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
        tfName.setText("Sony Wireless Headphones");
        cbCategory.setSelectedItem(ItemCategory.EARBUDS);
        tfColor.setText("Silver");
        tfBrand.setText("Sony");
        tfLocation.setText("Library Quiet Study Desk");
        tfDateTime.setText(DateTimeUtils.formatDisplay(LocalDateTime.now().minusHours(1)));
        tfDistinctiveFeatures.setText("Silver over-ear with case initials SK");
        taDescription.setText("Silver Sony headphones inside black hard shell zippered case found under desk 14.");

        tfFinderName.setText("Campus Library Staff Desk");
        tfFinderEmail.setText("librarydesk@campus.edu");
        tfFinderPhone.setText("+1-555-0944");
        cbFinderRole.setSelectedItem("Staff");
        tfStorageLocation.setText("Library Circulation Safe Drawer 3");
        tfVerificationQuestion.setText("What initials or monogram are stamped on the carrying case?");
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

        tfFinderName.setText("");
        tfFinderEmail.setText("");
        tfFinderPhone.setText("");
        cbFinderRole.setSelectedIndex(0);
        tfStorageLocation.setText("Campus Security Lost & Found Vault Room 102");
        tfVerificationQuestion.setText("");
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

        String finderName = tfFinderName.getText().trim();
        String finderEmail = tfFinderEmail.getText().trim();
        String finderPhone = tfFinderPhone.getText().trim();
        String finderRole = (String) cbFinderRole.getSelectedItem();
        String storage = tfStorageLocation.getText().trim();
        String verificationQ = tfVerificationQuestion.getText().trim();

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
            showError("Please specify where the item was found.");
            tfLocation.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            showError("Please provide a description of the found item.");
            taDescription.requestFocus();
            return;
        }
        if (finderName.isEmpty()) {
            showError("Please provide finder/custodian name.");
            tfFinderName.requestFocus();
            return;
        }
        if (storage.isEmpty()) {
            showError("Please specify the physical storage / custody location.");
            tfStorageLocation.requestFocus();
            return;
        }

        LocalDateTime dt = DateTimeUtils.parse(dtStr);
        if (dt == null) {
            showError("Invalid Date/Time format. Please use 'yyyy-MM-dd HH:mm'");
            tfDateTime.requestFocus();
            return;
        }

        if (verificationQ.isEmpty()) {
            verificationQ = "Describe distinctive markings or inner contents";
        }

        User reporter = new User("F-" + (System.currentTimeMillis() % 10000), finderName, finderEmail, finderPhone, finderRole);
        FoundItem item = new FoundItem(null, name, category, color, brand, new Location(locationStr), dt, description, features, reporter, storage, verificationQ);

        try {
            FoundItem saved = context.getFoundItemService().addFoundItem(item);
            context.notifyDataChanged();

            JOptionPane.showMessageDialog(
                    this,
                    "Found Item registered successfully!\nAssigned ID: #" + saved.getId() + "\nStorage: " + saved.getStorageLocation(),
                    "Item In Custody",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            context.navigateTo("DASHBOARD");

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


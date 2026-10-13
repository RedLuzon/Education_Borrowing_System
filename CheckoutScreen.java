package EducationBorrowingSystem;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

class ItemCellRenderer extends JPanel implements ListCellRenderer<Borrowitem> {
    private final JLabel name = new JLabel();
    private final JLabel sub = new JLabel();
    private final Chip catChip = new Chip("", UiKit.PRIMARY, UiKit.tint(UiKit.PRIMARY, 22));
    private final Chip stockChip = new Chip("", UiKit.SUCCESS, UiKit.tint(UiKit.SUCCESS, 22));

    ItemCellRenderer() {
        setLayout(new BorderLayout(10, 0)); setBorder(new EmptyBorder(7, 12, 7, 12));
        JPanel left = new JPanel(); left.setOpaque(false); left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        name.setFont(UiKit.BODY_B); sub.setFont(UiKit.CAPTION); sub.setForeground(UiKit.TEXT_SUB);
        left.add(name); left.add(sub);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0)); right.setOpaque(false);
        right.add(catChip); right.add(stockChip);
        add(left, BorderLayout.CENTER); add(right, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Borrowitem> list, Borrowitem item,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        name.setText(item.getItemName());
        sub.setText("ID " + item.getItemId() + "  \u00B7  " + item.getCategory());
        Color catC = UiKit.categoryColor(item.getCategory());
        catChip.setText(item.getCategory()); catChip.setForeground(catC); catChip.setFill(UiKit.tint(catC, 24));
        stockChip.setText("\u00D7" + item.getQuantity() + " in stock"); stockChip.setForeground(UiKit.SUCCESS); stockChip.setFill(UiKit.tint(UiKit.SUCCESS, 24));

        if (isSelected) {
            setBackground(UiKit.SELECT_BLUE);
            name.setForeground(UiKit.PRIMARY_DKR); sub.setForeground(UiKit.PRIMARY_DK);
            catChip.setForeground(UiKit.PRIMARY_DKR); catChip.setFill(new Color(255, 255, 255, 180));
            stockChip.setForeground(UiKit.SUCCESS); stockChip.setFill(new Color(255, 255, 255, 180));
        } else {
            setBackground(index % 2 == 0 ? Color.WHITE : UiKit.ZEBRA);
            name.setForeground(UiKit.TEXT); sub.setForeground(UiKit.TEXT_SUB);
        }
        for (Component c : getComponents()) c.setBackground(getBackground());
        return this;
    }
}

class CheckoutScreen extends JPanel {
    private DefaultListModel<Borrowitem> itemListModel;
    private JList<Borrowitem> availableItemsList;
    private FieldBox borrowerNameBox, sectionFacultyBox, searchBox;
    private JRadioButton adminPurposeRadio, schoolPurposeRadio;
    private JLabel borrowerStatusLabel, messageLabel, selectedCountLabel;
    private Borrower verifiedBorrower = null;
    private final BorrowingSystem appRef;

    public CheckoutScreen(BorrowingSystem app) {
        this.appRef = app;
        setBackground(UiKit.BG); setLayout(new BorderLayout());
        add(new Sidebar(app, "CHECKOUT"), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setOpaque(false); main.setBorder(new EmptyBorder(16, 24, 16, 24));
        add(main, BorderLayout.CENTER);

        main.add(new ScreenHeader("Multi-Item Check-Out", "Select items via highlighted JList (items with 0 stock are hidden)"), BorderLayout.NORTH);

        JPanel wrap = new JPanel(new GridBagLayout()); wrap.setOpaque(false);
        main.add(wrap, BorderLayout.CENTER);

        RoundedPanel card = new RoundedPanel(16, UiKit.SURFACE, true);
        card.setLayout(new GridBagLayout());
        card.setBorder(new CompoundBorder(card.getBorder(), new EmptyBorder(10, 24, 12, 24)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 4, 3, 4); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;

        gbc.gridy = 0; gbc.gridx = 0;
        card.add(new SectionTitle("1 \u00B7 Select Available Items (In-Stock Only)"), gbc);
        JLabel multiHint = Fields.caption("Hold CTRL to select multiple items. Use the search box to filter.");
        gbc.gridy = 1; card.add(multiHint, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(6, 4, 6, 4);
        JPanel searchRow = new JPanel(new BorderLayout(8, 0)); searchRow.setOpaque(false);
        searchBox = new FieldBox("\uD83D\uDD0D", false); searchBox.setPreferredSize(new Dimension(280, 34));
        searchRow.add(searchBox, BorderLayout.WEST);

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0)); linkPanel.setOpaque(false);
        LinkButton selectAllBtn = new LinkButton("Select all"); LinkButton clearBtn = new LinkButton("Clear");
        selectedCountLabel = Fields.caption("0 selected");
        linkPanel.add(selectedCountLabel); linkPanel.add(selectAllBtn); linkPanel.add(clearBtn);
        searchRow.add(linkPanel, BorderLayout.EAST);
        card.add(searchRow, gbc);

        itemListModel = new DefaultListModel<>();
        availableItemsList = new JList<>(itemListModel);
        availableItemsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        availableItemsList.setFont(UiKit.BODY); availableItemsList.setFixedCellHeight(52);
        availableItemsList.setBorder(new EmptyBorder(4, 4, 4, 4));
        availableItemsList.setSelectionBackground(UiKit.SELECT_BLUE);
        availableItemsList.setSelectionForeground(UiKit.PRIMARY_DKR);
        availableItemsList.setCellRenderer(new ItemCellRenderer());

        JScrollPane scrollPane = new JScrollPane(availableItemsList); UiKit.styleScroll(scrollPane);
        scrollPane.setPreferredSize(new Dimension(560, 190)); scrollPane.setMinimumSize(new Dimension(560, 190));
        gbc.gridy = 3; gbc.insets = new Insets(2, 4, 6, 4);
        card.add(scrollPane, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(3, 4, 3, 4);
        card.add(new SectionTitle("2 \u00B7 Borrowing Purpose"), gbc);

        RoundedPanel purposePanel = new RoundedPanel(10, new Color(0xFAFAF9), false);
        purposePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 18, 8));
        adminPurposeRadio = new JRadioButton("Borrow by Admin"); schoolPurposeRadio = new JRadioButton("Borrow to School");
        adminPurposeRadio.setIcon(new RadioIcon()); schoolPurposeRadio.setIcon(new RadioIcon());
        adminPurposeRadio.setOpaque(false); schoolPurposeRadio.setOpaque(false);
        adminPurposeRadio.setFont(UiKit.BODY); schoolPurposeRadio.setFont(UiKit.BODY);
        schoolPurposeRadio.setSelected(true);
        ButtonGroup purposeGroup = new ButtonGroup(); purposeGroup.add(adminPurposeRadio); purposeGroup.add(schoolPurposeRadio);
        purposePanel.add(adminPurposeRadio); purposePanel.add(schoolPurposeRadio);
        gbc.gridy = 5; card.add(purposePanel, gbc);

        gbc.gridy = 6; card.add(new SectionTitle("3 \u00B7 Borrower Details"), gbc);
        gbc.gridy = 7; card.add(Fields.caption("BORROWER NAME"), gbc);
        borrowerNameBox = new FieldBox("\uD83D\uDC64", false); borrowerNameBox.setPreferredSize(new Dimension(560, 36));
        gbc.gridy = 8; card.add(borrowerNameBox, gbc);

        gbc.gridy = 9; card.add(Fields.caption("SECTION / FACULTY ID NO."), gbc);
        JPanel sectionPanel = new JPanel(new BorderLayout(10, 0)); sectionPanel.setOpaque(false);
        sectionFacultyBox = new FieldBox("\uD83C\uDFEB", false); sectionFacultyBox.setPreferredSize(new Dimension(380, 36));
        StyledButton verifyBorrowerBtn = new StyledButton("Validate Borrower", UiKit.SIDEBAR_BG, Color.WHITE);
        sectionPanel.add(sectionFacultyBox, BorderLayout.CENTER); sectionPanel.add(verifyBorrowerBtn, BorderLayout.EAST);
        gbc.gridy = 10; card.add(sectionPanel, gbc);

        borrowerStatusLabel = new JLabel("Status: Enter any valid school borrower info.");
        borrowerStatusLabel.setFont(UiKit.CAPTION); borrowerStatusLabel.setForeground(UiKit.TEXT_SUB);
        gbc.gridy = 11; card.add(borrowerStatusLabel, gbc);

        StyledButton checkoutBtn = new StyledButton("Complete Multi-Item Check-Out");
        checkoutBtn.setFont(new Font(UiKit.FONT, Font.BOLD, 13));
        gbc.gridy = 12; gbc.insets = new Insets(8, 4, 4, 4); card.add(checkoutBtn, gbc);

        messageLabel = new JLabel(" "); messageLabel.setFont(UiKit.BODY_B);
        gbc.gridy = 13; gbc.insets = new Insets(2, 4, 4, 4); card.add(messageLabel, gbc);

        wrap.add(card);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent evt) { refreshAvailableItems(); }
        });

        searchBox.getField().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshAvailableItems(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshAvailableItems(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshAvailableItems(); }
        });

        availableItemsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectedCountLabel.setText(availableItemsList.getSelectedIndices().length + " selected");
        });

        selectAllBtn.addActionListener(e -> availableItemsList.setSelectionInterval(0, itemListModel.size() - 1));
        clearBtn.addActionListener(e -> availableItemsList.clearSelection());

        verifyBorrowerBtn.addActionListener(e -> {
            String name = borrowerNameBox.getText(), secOrFac = sectionFacultyBox.getText();
            if (name.isEmpty() || secOrFac.isEmpty()) {
                borrowerStatusLabel.setText("Error: Name and Section/Faculty ID cannot be empty.");
                borrowerStatusLabel.setForeground(UiKit.DANGER); verifiedBorrower = null;
                Toast.show(app, "Borrower name and ID are required.", Toast.Type.ERROR);
            } else {
                verifiedBorrower = new Borrower(name, secOrFac);
                borrowerStatusLabel.setText("Valid School Member: " + name + " (" + secOrFac + ")");
                borrowerStatusLabel.setForeground(UiKit.SUCCESS);
                Toast.show(app, "Borrower validated: " + name, Toast.Type.SUCCESS);
            }
        });

        checkoutBtn.addActionListener(e -> {
            List<Borrowitem> selectedItems = availableItemsList.getSelectedValuesList();
            if (selectedItems.isEmpty()) {
                messageLabel.setText("Error: Please select at least one item."); messageLabel.setForeground(UiKit.DANGER);
                Toast.show(app, "Select at least one item to check out.", Toast.Type.ERROR); return;
            }
            if (verifiedBorrower == null) {
                messageLabel.setText("Error: Please validate a school borrower first."); messageLabel.setForeground(UiKit.DANGER);
                Toast.show(app, "Please validate a borrower first.", Toast.Type.ERROR); return;
            }

            String purpose = adminPurposeRadio.isSelected() ? "Admin Use" : "School Use";
            for (Borrowitem item : selectedItems) {
                if (item.getQuantity() > 0) item.setQuantity(item.getQuantity() - 1);
            }

            String txId = "TX-" + (app.transactions.size() + 101);
            app.transactions.add(new Transaction(txId, selectedItems, verifiedBorrower, purpose));
            app.logActivity(Activity.Kind.LOAN, txId + " \u00B7 " + selectedItems.size() + " item(s) \u2192 " + verifiedBorrower.getBorrowerName() + " (" + verifiedBorrower.getSectionOrFacultyNo() + ") [by " + app.getCurrentUser() + "]");

            messageLabel.setText("Success! Checked out " + selectedItems.size() + " item(s) to " + verifiedBorrower.getBorrowerName());
            messageLabel.setForeground(UiKit.SUCCESS);
            Toast.show(app, txId + " created \u2014 " + selectedItems.size() + " item(s) checked out", Toast.Type.SUCCESS);

            borrowerNameBox.setText(""); sectionFacultyBox.setText(""); verifiedBorrower = null;
            borrowerStatusLabel.setText("Status: Enter any valid school borrower info."); borrowerStatusLabel.setForeground(UiKit.TEXT_SUB);
            refreshAvailableItems();
        });
    }

    private void refreshAvailableItems() {
        String q = searchBox.getText().toLowerCase();
        itemListModel.clear();
        for (Borrowitem item : appRef.inventory) {
            if (item.getQuantity() <= 0) continue;
            if (!q.isEmpty()) {
                String hay = (item.getItemId() + " " + item.getItemName() + " " + item.getCategory()).toLowerCase();
                if (!hay.contains(q)) continue;
            }
            itemListModel.addElement(item);
        }
        if (itemListModel.isEmpty()) {
            selectedCountLabel.setText(q.isEmpty() ? "All items checked out" : "No matches for \"" + q + "\"");
        } else {
            selectedCountLabel.setText(availableItemsList.getSelectedIndices().length + " selected");
        }
    }
}
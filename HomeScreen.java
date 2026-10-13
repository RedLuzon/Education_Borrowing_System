package EducationBorrowingSystem;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class HomeScreen extends JPanel {
    private final BorrowingSystem app;
    private JLabel statUnits, statLoans, statCats, statOverdue;
    private DonutChart chart;
    private JPanel legendPanel;
    private JPanel activityPanel;
    private JLabel activityEmpty;

    public HomeScreen(BorrowingSystem app) {
        this.app = app;
        setBackground(UiKit.BG);
        setLayout(new BorderLayout());
        add(new Sidebar(app, "HOME"), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setOpaque(false); main.setBorder(new EmptyBorder(24, 28, 24, 28));
        add(main, BorderLayout.CENTER);

        main.add(new ScreenHeader("Dashboard", "Overview of the resource lending portal"), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false); content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        GradientPanel banner = new GradientPanel(UiKit.PRIMARY, UiKit.ACCENT, 18);
        banner.setLayout(new BorderLayout()); banner.setBorder(new EmptyBorder(22, 28, 22, 28));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        JLabel welcome = new JLabel("Welcome back, " + app.getCurrentUser() + " (" + (app.isAdminUser() ? "Super Admin" : "Staff") + ")");
        welcome.setFont(new Font(UiKit.FONT, Font.BOLD, 22)); welcome.setForeground(Color.WHITE);

        String roleNote = app.isAdminUser() ? "You have full system access including inventory control, adding & deleting items." : "You have staff access for check-outs and returns processing.";
        JLabel hint = new JLabel(roleNote); hint.setFont(UiKit.BODY); hint.setForeground(new Color(0xFFEDD5));

        JPanel bannerText = new JPanel(); bannerText.setOpaque(false); bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.add(welcome); bannerText.add(Box.createVerticalStrut(6)); bannerText.add(hint);
        banner.add(bannerText, BorderLayout.WEST);

        StyledButton enterBtn = new StyledButton("Check-Out  \u2192");
        enterBtn.setFont(UiKit.BODY_B);
        enterBtn.addActionListener(e -> app.switchScreen("CHECKOUT"));
        JPanel bannerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 24));
        bannerRight.setOpaque(false); bannerRight.add(enterBtn);
        banner.add(bannerRight, BorderLayout.EAST);
        content.add(banner);

        content.add(Box.createVerticalStrut(18));

        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false); stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 116));
        stats.add(statCard("\uD83D\uDCE6", "Total Stock Units", statUnits = new JLabel("0")));
        stats.add(statCard("\uD83D\uDCCB", "Active Loans", statLoans = new JLabel("0")));
        stats.add(statCard("\uD83D\uDDC2\uFE0F", "Catalog Categories", statCats = new JLabel("0")));
        stats.add(statCard("\u26A0\uFE0F", "Overdue Returns", statOverdue = new JLabel("0")));
        content.add(stats);

        content.add(Box.createVerticalStrut(18));

        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));

        RoundedPanel chartCard = new RoundedPanel(16, UiKit.SURFACE, true);
        chartCard.setLayout(new BorderLayout(0, 8));
        chartCard.setBorder(new CompoundBorder(chartCard.getBorder(), new EmptyBorder(16, 20, 14, 20)));
        chartCard.add(new SectionTitle("Stock by Category"), BorderLayout.NORTH);

        JPanel chartBody = new JPanel(new BorderLayout(10, 0)); chartBody.setOpaque(false);
        chart = new DonutChart(); chartBody.add(chart, BorderLayout.WEST);
        legendPanel = new JPanel(); legendPanel.setOpaque(false); legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        JPanel legendWrap = new JPanel(new BorderLayout()); legendWrap.setOpaque(false); legendWrap.add(legendPanel, BorderLayout.NORTH);
        chartBody.add(legendWrap, BorderLayout.CENTER); chartCard.add(chartBody, BorderLayout.CENTER);
        row.add(chartCard);

        RoundedPanel activityCard = new RoundedPanel(16, UiKit.SURFACE, true);
        activityCard.setLayout(new BorderLayout(0, 8));
        activityCard.setBorder(new CompoundBorder(activityCard.getBorder(), new EmptyBorder(16, 20, 14, 20)));
        activityCard.add(new SectionTitle("Recent Activity"), BorderLayout.NORTH);

        activityPanel = new JPanel(); activityPanel.setOpaque(false); activityPanel.setLayout(new BoxLayout(activityPanel, BoxLayout.Y_AXIS));
        JScrollPane actScroll = new JScrollPane(activityPanel); UiKit.styleScroll(actScroll);
        activityCard.add(actScroll, BorderLayout.CENTER);

        activityEmpty = new JLabel("No activity yet.", SwingConstants.CENTER);
        activityEmpty.setFont(UiKit.CAPTION); activityEmpty.setForeground(UiKit.TEXT_SUB);
        row.add(activityCard); content.add(row);

        content.add(Box.createVerticalStrut(18));

        RoundedPanel actions = new RoundedPanel(16, UiKit.SURFACE, true);
        actions.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 16));
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        actions.setBorder(new CompoundBorder(actions.getBorder(), new EmptyBorder(6, 20, 6, 20)));

        StyledButton checkoutBtn = new StyledButton("New Check-Out");
        checkoutBtn.addActionListener(e -> app.switchScreen("CHECKOUT"));
        GhostButton returnBtn = new GhostButton("Process Returns");
        returnBtn.addActionListener(e -> app.switchScreen("RETURN"));

        actions.add(Fields.caption("QUICK ACTIONS"));
        actions.add(Box.createHorizontalStrut(10));
        actions.add(checkoutBtn);
        actions.add(returnBtn);

        if (app.isAdminUser()) {
            StyledButton addItemBtn = new StyledButton("+ Add Item", UiKit.SUCCESS, Color.WHITE);
            addItemBtn.addActionListener(e -> showAddItemDialog());
            actions.add(addItemBtn);

            StyledButton deleteItemBtn = new StyledButton(" - Delete Item", UiKit.DANGER, Color.WHITE);
            deleteItemBtn.addActionListener(e -> showDeleteItemDialog());
            actions.add(deleteItemBtn);
        }

        content.add(actions);
        content.add(Box.createVerticalGlue());
        main.add(content, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent evt) { refreshDashboard(); }
        });
    }

    private void showAddItemDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Item to Inventory", true);
        dialog.setSize(420, 380); dialog.setLocationRelativeTo(this); dialog.setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 24, 20, 24)); panel.setBackground(UiKit.SURFACE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(4, 0, 4, 0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;

        panel.add(Fields.caption("ITEM NAME"), gbc);
        gbc.gridy++; JTextField nameField = Fields.text(); panel.add(nameField, gbc);

        gbc.gridy++; panel.add(Fields.caption("CATEGORY"), gbc);
        gbc.gridy++; String[] categories = {"Equipment", "Science", "Math", "Books", "Technology"};
        JComboBox<String> categoryBox = new JComboBox<>(categories); categoryBox.setFont(UiKit.BODY);
        panel.add(categoryBox, gbc);

        gbc.gridy++; panel.add(Fields.caption("INITIAL QUANTITY / STOCK"), gbc);
        gbc.gridy++; JTextField stockField = Fields.text(); stockField.setText("1"); panel.add(stockField, gbc);

        gbc.gridy++; gbc.insets = new Insets(16, 0, 0, 0);
        StyledButton saveBtn = new StyledButton("Save Item to Inventory"); panel.add(saveBtn, gbc);

        dialog.add(panel);

        saveBtn.addActionListener(e -> {
            String itemName = nameField.getText().trim();
            String category = (String) categoryBox.getSelectedItem();
            String stockStr = stockField.getText().trim();

            if (itemName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an item name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(stockStr);
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid positive stock number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String newId = String.format("%02d", app.inventory.size() + 1);
            app.inventory.add(new Borrowitem(newId, itemName, category, qty));
            app.logActivity(Activity.Kind.SYSTEM, "Admin added new item: " + itemName + " (" + category + ") with " + qty + " stock(s)");
            Toast.show(app, "Successfully added " + itemName + " to inventory!", Toast.Type.SUCCESS);
            dialog.dispose();
            refreshDashboard();
        });
        dialog.setVisible(true);
    }

    private void showDeleteItemDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage & Delete Inventory Items", true);
        dialog.setSize(520, 440); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(16, 20, 16, 20)); panel.setBackground(UiKit.SURFACE);

        JLabel headerLabel = new JLabel("Select an item to delete from inventory:");
        headerLabel.setFont(UiKit.SECTION); headerLabel.setForeground(UiKit.TEXT);
        panel.add(headerLabel, BorderLayout.NORTH);

        DefaultListModel<Borrowitem> listModel = new DefaultListModel<>();
        for (Borrowitem item : app.inventory) {
            listModel.addElement(item);
        }
        JList<Borrowitem> itemList = new JList<>(listModel);
        itemList.setFont(UiKit.BODY); itemList.setFixedCellHeight(40);
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(itemList); UiKit.styleScroll(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        StyledButton deleteBtn = new StyledButton("Delete Selected Item", UiKit.DANGER, Color.WHITE);
        deleteBtn.setEnabled(false);
        itemList.addListSelectionListener(e -> deleteBtn.setEnabled(!itemList.isSelectionEmpty()));

        deleteBtn.addActionListener(e -> {
            Borrowitem selected = itemList.getSelectedValue();
            if (selected == null) return;

            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to delete \"" + selected.getItemName() + "\" from inventory?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                app.inventory.remove(selected);
                app.logActivity(Activity.Kind.SYSTEM, "Admin deleted item: " + selected.getItemName() + " (ID: " + selected.getItemId() + ")");
                Toast.show(app, "Deleted " + selected.getItemName() + " successfully.", Toast.Type.SUCCESS);
                dialog.dispose();
                refreshDashboard();
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setOpaque(false); bottom.add(deleteBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JPanel statCard(String icon, String caption, JLabel numberLabel) {
        RoundedPanel p = new RoundedPanel(16, UiKit.SURFACE, true);
        p.setLayout(new BorderLayout(0, 4)); p.setBorder(new CompoundBorder(p.getBorder(), new EmptyBorder(14, 18, 12, 18)));
        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
        JLabel ic = new JLabel(icon); ic.setFont(UiKit.EMOJI);
        JLabel c = new JLabel(caption); c.setFont(UiKit.CAPTION); c.setForeground(UiKit.TEXT_SUB); c.setHorizontalAlignment(SwingConstants.RIGHT);
        top.add(ic, BorderLayout.WEST); top.add(c, BorderLayout.EAST);
        numberLabel.setFont(UiKit.BIG_NUM); numberLabel.setForeground(UiKit.PRIMARY);
        p.add(top, BorderLayout.NORTH); p.add(numberLabel, BorderLayout.CENTER);
        return p;
    }

    private void refreshDashboard() {
        int totalUnits = 0; for (Borrowitem it : app.inventory) totalUnits += it.getQuantity();
        int active = 0, overdue = 0;
        for (Transaction tx : app.transactions) {
            if (!tx.isReturned()) { active++; if (tx.daysLate() > 0) overdue++; }
        }
        Set<String> cats = new HashSet<>(); for (Borrowitem it : app.inventory) cats.add(it.getCategory());

        Anim.countUp(statUnits, totalUnits);
        Anim.countUp(statLoans, active);
        Anim.countUp(statCats, cats.size());
        Anim.countUp(statOverdue, overdue);
        statOverdue.setForeground(overdue > 0 ? UiKit.DANGER : UiKit.PRIMARY);

        Map<String, Integer> chartData = new LinkedHashMap<>();
        for (Borrowitem it : app.inventory) {
            if (it.getQuantity() > 0) chartData.merge(it.getCategory(), it.getQuantity(), Integer::sum);
        }
        chart.setData(chartData);

        legendPanel.removeAll();
        for (Map.Entry<String, Integer> e : chartData.entrySet()) {
            Color c = UiKit.categoryColor(e.getKey());
            JPanel rowItem = new JPanel(new BorderLayout(8, 0)); rowItem.setOpaque(false); rowItem.setBorder(new EmptyBorder(4, 2, 4, 2));
            JLabel dot = new JLabel("\u25CF"); dot.setForeground(c); dot.setFont(new Font(UiKit.FONT, Font.PLAIN, 10));
            JLabel name = new JLabel(e.getKey()); name.setFont(UiKit.BODY); name.setForeground(UiKit.TEXT);
            JLabel count = new JLabel(String.valueOf(e.getValue())); count.setFont(UiKit.BODY_B); count.setForeground(UiKit.TEXT_SUB); count.setHorizontalAlignment(SwingConstants.RIGHT);
            rowItem.add(dot, BorderLayout.WEST); rowItem.add(name, BorderLayout.CENTER); rowItem.add(count, BorderLayout.EAST);
            legendPanel.add(rowItem);
        }
        legendPanel.revalidate(); legendPanel.repaint();

        activityPanel.removeAll();
        if (app.activityLog.isEmpty()) {
            activityPanel.add(activityEmpty);
        } else {
            int shown = 0; for (Activity a : app.activityLog) { if (shown++ >= 12) break; activityPanel.add(activityRow(a)); }
        }
        activityPanel.revalidate(); activityPanel.repaint();
    }

    private JPanel activityRow(Activity a) {
        JPanel rowItem = new JPanel(new BorderLayout(10, 0)); rowItem.setOpaque(false); rowItem.setBorder(new EmptyBorder(5, 2, 5, 2));
        rowItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        JLabel dot = new JLabel("\u25CF"); dot.setForeground(a.getKind().color); dot.setFont(new Font(UiKit.FONT, Font.PLAIN, 9));
        JPanel mid = new JPanel(); mid.setOpaque(false); mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        JLabel text = new JLabel("<html><body style='width:300px'>" + a.getText() + "</body></html>"); text.setFont(UiKit.BODY); text.setForeground(UiKit.TEXT);
        JLabel time = new JLabel(a.timeStamp()); time.setFont(UiKit.CAPTION); time.setForeground(UiKit.TEXT_SUB);
        mid.add(text); mid.add(time);
        rowItem.add(dot, BorderLayout.WEST); rowItem.add(mid, BorderLayout.CENTER);
        return rowItem;
    }
}

class Sidebar extends JPanel {
    Sidebar(BorrowingSystem app, String current) {
        setPreferredSize(new Dimension(220, getHeight()));
        setBackground(UiKit.SIDEBAR_BG);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(24, 16, 24, 16));

        JLabel title = new JLabel("EBS Portal");
        title.setFont(UiKit.H2);
        title.setForeground(Color.WHITE);
        top.add(title);
        top.add(Box.createVerticalStrut(20));

        String[] menuKeys = {"HOME", "CHECKOUT", "RETURN"};
        String[] menuLabels = {"Dashboard", "Check-Out", "Returns"};

        for (int i = 0; i < menuKeys.length; i++) {
            String k = menuKeys[i];
            JButton btn = new JButton(menuLabels[i]);
            btn.setFont(UiKit.BODY);
            btn.setForeground(k.equals(current) ? UiKit.PRIMARY : UiKit.SIDEBAR_TX);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.addActionListener(e -> app.switchScreen(k));
            top.add(btn);
            top.add(Box.createVerticalStrut(8));
        }

        add(top, BorderLayout.NORTH);

        JButton logoutBtn = new JButton("Sign Out");
        logoutBtn.setFont(UiKit.BODY_B);
        logoutBtn.setForeground(UiKit.DANGER);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(new EmptyBorder(16, 16, 24, 16));
        logoutBtn.addActionListener(e -> app.switchScreen("LOGIN"));
        add(logoutBtn, BorderLayout.SOUTH);
    }
}

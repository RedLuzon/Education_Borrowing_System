package EducationBorrowingSystem;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

class TxCellRenderer extends JPanel implements ListCellRenderer<Transaction> {
    private final JLabel title = new JLabel();
    private final JLabel sub = new JLabel();
    private final Chip dueChip = new Chip("", UiKit.TEXT_SUB, UiKit.tint(UiKit.TEXT_SUB, 20));

    TxCellRenderer() {
        setLayout(new BorderLayout(10, 0)); setBorder(new EmptyBorder(8, 12, 8, 12));
        JPanel left = new JPanel(); left.setOpaque(false); left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        title.setFont(UiKit.BODY_B); sub.setFont(UiKit.CAPTION); sub.setForeground(UiKit.TEXT_SUB);
        left.add(title); left.add(sub);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8)); right.setOpaque(false);
        right.add(dueChip);
        add(left, BorderLayout.CENTER); add(right, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Transaction> list, Transaction tx,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        StringBuilder names = new StringBuilder();
        for (Borrowitem bi : tx.getBorrowedItems()) names.append(bi.getItemName()).append(", ");
        if (names.length() > 2) names.setLength(names.length() - 2);

        title.setText(tx.getTransactionId() + "  \u00B7  " + tx.getBorrower().getBorrowerName());
        sub.setText("<html><body style='width:340px'>" + names + "</body></html>");

        long late = tx.daysLate();
        if (late > 0) {
            dueChip.setText(late + "d overdue"); dueChip.setForeground(UiKit.DANGER); dueChip.setFill(UiKit.tint(UiKit.DANGER, 22));
        } else {
            dueChip.setText("Due " + tx.getDueDate().format(DateTimeFormatter.ofPattern("MMM d")));
            dueChip.setForeground(UiKit.TEXT_SUB); dueChip.setFill(UiKit.tint(UiKit.TEXT_SUB, 20));
        }

        if (isSelected) {
            setBackground(UiKit.SELECT_BLUE);
            title.setForeground(UiKit.PRIMARY_DKR); sub.setForeground(UiKit.PRIMARY_DK);
            dueChip.setForeground(UiKit.PRIMARY_DKR); dueChip.setFill(new Color(255, 255, 255, 180));
        } else {
            setBackground(index % 2 == 0 ? Color.WHITE : UiKit.ZEBRA);
            title.setForeground(UiKit.TEXT); sub.setForeground(UiKit.TEXT_SUB);
        }
        for (Component c : getComponents()) c.setBackground(getBackground());
        return this;
    }
}

class ReturnScreen extends JPanel {
    private DefaultListModel<Transaction> listModel;
    private JList<Transaction> activeList;
    private final BorrowingSystem appRef;
    private JPanel detailCard;
    private JLabel dTx, dItems, dBorrower, dDue, dFine;
    private Chip dStatus;
    private StyledButton processReturnBtn;

    public ReturnScreen(BorrowingSystem app) {
        this.appRef = app;
        setBackground(UiKit.BG); setLayout(new BorderLayout());
        add(new Sidebar(app, "RETURN"), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setOpaque(false); main.setBorder(new EmptyBorder(24, 28, 24, 28));
        add(main, BorderLayout.CENTER);

        main.add(new ScreenHeader("Returns & Fines", "Process returns, restore item stock, and compute late fees"), BorderLayout.NORTH);

        JPanel wrap = new JPanel(new GridBagLayout()); wrap.setOpaque(false);
        main.add(wrap, BorderLayout.CENTER);

        RoundedPanel centerCard = new RoundedPanel(16, UiKit.SURFACE, true);
        centerCard.setLayout(new BorderLayout(0, 14));
        centerCard.setBorder(new CompoundBorder(centerCard.getBorder(), new EmptyBorder(22, 26, 22, 26)));
        centerCard.setPreferredSize(new Dimension(640, 560));

        JLabel instruction = new JLabel("Select an active transaction to preview & process the return:");
        instruction.setFont(UiKit.SECTION); instruction.setForeground(UiKit.TEXT);
        centerCard.add(instruction, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        activeList = new JList<>(listModel);
        activeList.setFont(UiKit.BODY); activeList.setFixedCellHeight(56);
        activeList.setBorder(new EmptyBorder(4, 4, 4, 4));
        activeList.setSelectionBackground(UiKit.SELECT_BLUE); activeList.setSelectionForeground(UiKit.PRIMARY_DKR);
        activeList.setCellRenderer(new TxCellRenderer());

        JScrollPane scrollPane = new JScrollPane(activeList); UiKit.styleScroll(scrollPane);
        scrollPane.setPreferredSize(new Dimension(580, 210));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        detailCard = new RoundedPanel(12, new Color(0xFAFAF9), false);
        detailCard.setLayout(new GridBagLayout());
        detailCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        GridBagConstraints g = new GridBagConstraints(); g.anchor = GridBagConstraints.WEST; g.insets = new Insets(2, 4, 2, 12);
        dTx = previewLabel("Select a transaction above");
        dItems = previewLabel("\u2014");
        dBorrower = previewLabel("\u2014");
        dDue = previewLabel("\u2014");
        dFine = previewLabel("\u2014");
        dStatus = new Chip("No selection", UiKit.TEXT_SUB, UiKit.tint(UiKit.TEXT_SUB, 22));

        g.gridx = 0; g.gridy = 0; detailCard.add(Fields.caption("TRANSACTION"), g);
        g.gridx = 1; detailCard.add(dTx, g);
        g.gridx = 2; g.gridy = 0; detailCard.add(Fields.caption("STATUS"), g);
        g.gridx = 3; detailCard.add(dStatus, g);

        g.gridx = 0; g.gridy = 1; detailCard.add(Fields.caption("ITEMS"), g);
        g.gridx = 1; detailCard.add(dItems, g);
        g.gridx = 2; g.gridy = 1; detailCard.add(Fields.caption("DUE DATE"), g);
        g.gridx = 3; detailCard.add(dDue, g);

        g.gridx = 0; g.gridy = 2; detailCard.add(Fields.caption("BORROWER"), g);
        g.gridx = 1; detailCard.add(dBorrower, g);
        g.gridx = 2; g.gridy = 2; detailCard.add(Fields.caption("EST. FINE"), g);
        g.gridx = 3; detailCard.add(dFine, g);

        JPanel bottomPanel = new JPanel(); bottomPanel.setOpaque(false); bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(detailCard); bottomPanel.add(Box.createVerticalStrut(12));
        processReturnBtn = new StyledButton("Process Return & Calculate Fine");
        processReturnBtn.setFont(new Font(UiKit.FONT, Font.BOLD, 14)); processReturnBtn.setEnabled(false);
        processReturnBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(processReturnBtn);
        centerCard.add(bottomPanel, BorderLayout.SOUTH);

        wrap.add(centerCard);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentShown(java.awt.event.ComponentEvent evt) { refreshActiveList(); }
        });

        activeList.addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) updateDetail(); });

        processReturnBtn.addActionListener(e -> {
            Transaction tx = activeList.getSelectedValue();
            if (tx == null) return;
            double fine = tx.processReturnAndCalculateFine();
            int ok = JOptionPane.showConfirmDialog(this,
                    "Process return for " + tx.getTransactionId() + "?\n" + "Items: " + tx.getBorrowedItems().size() + "\n" + "Fine to collect: $" + String.format("%.2f", fine),
                    "Confirm Return", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) return;

            tx.setReturned(true);
            for (Borrowitem item : tx.getBorrowedItems()) item.setQuantity(item.getQuantity() + 1);
            app.logActivity(Activity.Kind.RETURN, tx.getTransactionId() + " returned by " + tx.getBorrower().getBorrowerName() + (fine > 0 ? " \u00B7 fine $" + String.format("%.2f", fine) : " \u00B7 no fine") + " [processed by " + app.getCurrentUser() + "]");
            Toast.show(app, tx.getTransactionId() + " returned \u2014 fine: $" + String.format("%.2f", fine), fine > 0 ? Toast.Type.INFO : Toast.Type.SUCCESS);
            refreshActiveList();
        });
    }

    private JLabel previewLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(UiKit.BODY_B); l.setForeground(UiKit.TEXT);
        return l;
    }

    private void updateDetail() {
        Transaction tx = activeList.getSelectedValue();
        if (tx == null) {
            dTx.setText("Select a transaction above"); dItems.setText("\u2014"); dBorrower.setText("\u2014");
            dDue.setText("\u2014"); dFine.setText("\u2014"); dStatus.setText("No selection");
            dStatus.setForeground(UiKit.TEXT_SUB); dStatus.setFill(UiKit.tint(UiKit.TEXT_SUB, 22));
            processReturnBtn.setEnabled(false); return;
        }
        long late = tx.daysLate(); double fine = tx.processReturnAndCalculateFine();
        dTx.setText(tx.getTransactionId() + "  \u00B7  " + tx.getPurpose());
        dItems.setText(tx.getBorrowedItems().size() + " item(s)");
        dBorrower.setText(tx.getBorrower().getBorrowerName() + " (" + tx.getBorrower().getSectionOrFacultyNo() + ")");
        dDue.setText(tx.getDueDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        dFine.setText("$" + String.format("%.2f", fine));

        if (late > 0) {
            dStatus.setText(late + "d overdue"); dStatus.setForeground(UiKit.DANGER); dStatus.setFill(UiKit.tint(UiKit.DANGER, 22));
        } else {
            dStatus.setText("On time"); dStatus.setForeground(UiKit.SUCCESS); dStatus.setFill(UiKit.tint(UiKit.SUCCESS, 22));
        }
        processReturnBtn.setEnabled(true);
    }

    private void refreshActiveList() {
        Transaction prev = activeList.getSelectedValue();
        listModel.clear();
        for (Transaction tx : appRef.transactions) {
            if (!tx.isReturned()) listModel.addElement(tx);
        }
        if (prev != null && listModel.contains(prev)) activeList.setSelectedValue(prev, true);
        else activeList.clearSelection();
        updateDetail();
    }
}
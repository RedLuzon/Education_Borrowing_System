package EducationBorrowingSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

class LoginScreen extends JPanel {
    private FieldBox userBox, passBox;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private final RoundedPanel card;

    public LoginScreen(BorrowingSystem app) {
        setDoubleBuffered(true);
        setOpaque(false); // <--- Inalis ang default opaque background para walang flash/flicker
        setLayout(new GridBagLayout());

        card = new RoundedPanel(22, UiKit.SURFACE, true);
        card.setDoubleBuffered(true);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(44, 52, 40, 52));
        card.setAlpha(0f);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(6, 0, 6, 0); gbc.anchor = GridBagConstraints.CENTER;

        JLabel logo = new JLabel("\uD83D\uDCDA", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        card.add(logo, gbc);

        gbc.gridy++;
        JLabel title = new JLabel("Education Borrowing System", SwingConstants.CENTER);
        title.setFont(UiKit.H1); title.setForeground(UiKit.TEXT);
        card.add(title, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 16, 0);
        JLabel sub = new JLabel("<html><center>Sign in as <b>admin / admin123</b> (Super Admin)<br>or <b>staff / staff123</b> (Staff / Librarian)</center></html>", SwingConstants.CENTER);
        sub.setFont(UiKit.CAPTION); sub.setForeground(UiKit.TEXT_SUB);
        card.add(sub, gbc);

        gbc.gridy++; gbc.insets = new Insets(6, 0, 2, 0); gbc.anchor = GridBagConstraints.WEST;
        card.add(Fields.caption("USERNAME"), gbc);

        gbc.gridy++; gbc.insets = new Insets(2, 0, 12, 0); gbc.anchor = GridBagConstraints.CENTER;
        userBox = new FieldBox("\uD83D\uDC64", false);
        userBox.setPreferredSize(new Dimension(300, 42));
        card.add(userBox, gbc);

        gbc.gridy++; gbc.insets = new Insets(6, 0, 2, 0); gbc.anchor = GridBagConstraints.WEST;
        card.add(Fields.caption("PASSWORD"), gbc);

        gbc.gridy++; gbc.insets = new Insets(2, 0, 16, 0); gbc.anchor = GridBagConstraints.CENTER;
        passBox = new FieldBox("\uD83D\uDD12", true);
        passBox.setPreferredSize(new Dimension(300, 42));
        card.add(passBox, gbc);

        gbc.gridy++; gbc.insets = new Insets(4, 0, 4, 0);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true); progressBar.setFont(UiKit.CAPTION);
        progressBar.setForeground(UiKit.PRIMARY); progressBar.setBackground(UiKit.BORDER);
        progressBar.setBorderPainted(false); progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(300, 6));
        card.add(progressBar, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 10, 0);
        StyledButton loginBtn = new StyledButton("Sign In");
        loginBtn.setPreferredSize(new Dimension(300, 42));
        card.add(loginBtn, gbc);

        gbc.gridy++; gbc.insets = new Insets(4, 0, 0, 0);
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(UiKit.CAPTION);
        card.add(statusLabel, gbc);

        add(card);

        SwingUtilities.invokeLater(() -> {
            Timer fadeIn = new Timer(16, null);
            final float[] a = {0f};
            fadeIn.addActionListener(e -> {
                a[0] += 0.05f;
                if (a[0] >= 1f) { a[0] = 1f; fadeIn.stop(); }
                card.setAlpha(a[0]);
            });
            fadeIn.start();
        });

        ActionListener doLogin = e -> loginBtn.doClick();
        userBox.getField().addActionListener(doLogin);
        passBox.getField().addActionListener(doLogin);

        loginBtn.addActionListener(e -> {
            String u = userBox.getText();
            String p = new String(((JPasswordField) passBox.getField()).getPassword()).trim();
            boolean isValidAdmin = u.equalsIgnoreCase("admin") && p.equals("admin123");
            boolean isValidStaff = u.equalsIgnoreCase("staff") && p.equals("staff123");

            if (isValidAdmin || isValidStaff) {
                app.setCurrentUser(u, isValidAdmin);
                loginBtn.setEnabled(false);
                progressBar.setVisible(true);
                progressBar.setValue(0);
                statusLabel.setText("Authenticating credentials (" + (isValidAdmin ? "Super Admin" : "Staff") + ")...");
                statusLabel.setForeground(UiKit.SUCCESS);

                Timer timer = new Timer(30, null);
                timer.addActionListener(evt -> {
                    int val = progressBar.getValue();
                    if (val < 100) {
                        progressBar.setValue(val + 10);
                    } else {
                        timer.stop();
                        userBox.setText(""); passBox.setText("");
                        progressBar.setVisible(false); loginBtn.setEnabled(true); statusLabel.setText(" ");
                        app.logActivity(Activity.Kind.AUTH, u + " signed in as " + (isValidAdmin ? "Super Admin" : "Staff"));
                        app.switchScreen("HOME");
                        Timer delay = new Timer(500, ev -> Toast.show(app, "Welcome back, " + u + " \uD83D\uDC4B", Toast.Type.SUCCESS));
                        delay.setRepeats(false); delay.start();
                    }
                });
                timer.start();
            } else {
                statusLabel.setText("Access Denied: Invalid username or password.");
                statusLabel.setForeground(UiKit.DANGER);
                Toast.show(app, "Invalid credentials. Use admin/admin123 or staff/staff123.", Toast.Type.ERROR);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, UiKit.PRIMARY_DKR, getWidth(), getHeight(), UiKit.PRIMARY));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
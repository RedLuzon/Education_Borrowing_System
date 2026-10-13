package EducationBorrowingSystem;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BorrowingSystem extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private FadeGlass glass;
    private StatusBar statusBar;
    private Timer fadeTimer;

    public List<Borrowitem> inventory = new ArrayList<>();
    public List<Transaction> transactions = new ArrayList<>();
    public List<Activity> activityLog = new ArrayList<>();

    private String currentUser = "admin";
    private boolean isAdminUser = true;

    public BorrowingSystem() {
        setTitle("Education Borrowing System — Orange Edition");
        setSize(1100, 840);
        setMinimumSize(new Dimension(1020, 740));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadMockData();
        logActivity(Activity.Kind.SYSTEM, "System initialized with " + inventory.size() + " catalog items");

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UiKit.BG);

    
        cardPanel.add(new StartScreen(this), "START");
        cardPanel.add(new LoginScreen(this), "LOGIN");
        cardPanel.add(new HomeScreen(this), "HOME");
        cardPanel.add(new CheckoutScreen(this), "CHECKOUT");
        cardPanel.add(new ReturnScreen(this), "RETURN");

        glass = new FadeGlass();
        statusBar = new StatusBar();
        statusBar.setVisible(false);

        JLayeredPane layers = new JLayeredPane();
        layers.add(cardPanel, JLayeredPane.DEFAULT_LAYER);
        layers.add(glass, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                cardPanel.setBounds(0, 0, layers.getWidth(), layers.getHeight());
                glass.setBounds(0, 0, layers.getWidth(), layers.getHeight());
            }
        });

        setLayout(new BorderLayout());
        add(layers, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        cardPanel.setBounds(0, 0, getWidth(), getHeight() - 40);
        glass.setBounds(0, 0, getWidth(), getHeight() - 40);

    
        cardLayout.show(cardPanel, "START");

        JRootPane rp = getRootPane();
        rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control 1"), "goHome");
        rp.getActionMap().put("goHome", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { switchScreen("HOME"); }
        });
        rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control 2"), "goCheckout");
        rp.getActionMap().put("goCheckout", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { switchScreen("CHECKOUT"); }
        });
        rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control 3"), "goReturn");
        rp.getActionMap().put("goReturn", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { switchScreen("RETURN"); }
        });
    }

    public void setCurrentUser(String user, boolean isAdmin) {
        this.currentUser = user;
        this.isAdminUser = isAdmin;
    }

    public String getCurrentUser() { return currentUser; }
    public boolean isAdminUser() { return isAdminUser; }

    private void loadMockData() {
        inventory.add(new Borrowitem("01", "Projector Pro X1", "Equipment", 2));
        inventory.add(new Borrowitem("02", "Office Laser Printer", "Equipment", 1));
        inventory.add(new Borrowitem("03", "Wireless Microphone Set", "Equipment", 3));
        inventory.add(new Borrowitem("04", "Portable Speaker System", "Equipment", 2));
        inventory.add(new Borrowitem("05", "Document Camera", "Equipment", 1));
        inventory.add(new Borrowitem("06", "Digital DSLR Camera", "Equipment", 2));
        inventory.add(new Borrowitem("07", "Lab Microscope", "Science", 4));
        inventory.add(new Borrowitem("08", "Digital Balance Scale", "Science", 3));
        inventory.add(new Borrowitem("09", "Anatomy Skeleton Model", "Science", 1));
        inventory.add(new Borrowitem("10", "Chemistry Glassware Set", "Science", 5));
        inventory.add(new Borrowitem("11", "Physics Optics Bench", "Science", 2));
        inventory.add(new Borrowitem("12", "Stethoscope Kit", "Science", 3));
        inventory.add(new Borrowitem("13", "Graphing Calculator", "Math", 6));
        inventory.add(new Borrowitem("14", "Geometric Shapes Set", "Math", 4));
        inventory.add(new Borrowitem("15", "Financial Calculator", "Math", 5));
        inventory.add(new Borrowitem("16", "Large Demonstration Compass", "Math", 2));
        inventory.add(new Borrowitem("17", "Probability Spinner Set", "Math", 3));
        inventory.add(new Borrowitem("18", "Abacus Traditional Tool", "Math", 4));
        inventory.add(new Borrowitem("19", "Java Programming Textbook", "Books", 5));
        inventory.add(new Borrowitem("20", "Data Structures Guide", "Books", 4));
        inventory.add(new Borrowitem("21", "Advanced Calculus Manual", "Books", 3));
        inventory.add(new Borrowitem("22", "World History Atlas", "Books", 2));
        inventory.add(new Borrowitem("23", "Organic Chemistry Reference", "Books", 3));
        inventory.add(new Borrowitem("24", "Modern Literature Anthology", "Books", 4));
        inventory.add(new Borrowitem("25", "Robotics Kit V2", "Technology", 2));
        inventory.add(new Borrowitem("26", "Arduino Starter Kit", "Technology", 4));
        inventory.add(new Borrowitem("27", "VR Educational Headset", "Technology", 2));
        inventory.add(new Borrowitem("28", "Tablet Learning Device", "Technology", 5));
        inventory.add(new Borrowitem("29", "Raspberry Pi 4 Board", "Technology", 3));
        inventory.add(new Borrowitem("30", "Networking Switch Hub", "Technology", 2));
    }

    public void logActivity(Activity.Kind kind, String text) {
        activityLog.add(0, new Activity(kind, text));
        if (activityLog.size() > 60) activityLog.remove(activityLog.size() - 1);
    }

    public void switchScreen(String screenName) {
        if (fadeTimer != null && fadeTimer.isRunning()) return;
        fadeTimer = new Timer(11, null);
        final boolean[] swapped = {false};
        fadeTimer.addActionListener(e -> {
            if (!swapped[0]) {
                glass.alpha += 0.22f;
                if (glass.alpha >= 1f) {
                    glass.alpha = 1f;
                    swapped[0] = true;

                    cardPanel.removeAll();
                    cardPanel.add(new StartScreen(this), "START");
                    cardPanel.add(new LoginScreen(this), "LOGIN");
                    cardPanel.add(new HomeScreen(this), "HOME");
                    cardPanel.add(new CheckoutScreen(this), "CHECKOUT");
                    cardPanel.add(new ReturnScreen(this), "RETURN");

                    cardLayout.show(cardPanel, screenName);
                    boolean hideStatus = "START".equals(screenName) || "LOGIN".equals(screenName);
                    statusBar.setVisible(!hideStatus);
                    if (!hideStatus) statusBar.setScreen(this, screenName);
                    revalidate();
                }
            } else {
                glass.alpha -= 0.22f;
                if (glass.alpha <= 0f) {
                    glass.alpha = 0f;
                    fadeTimer.stop();
                }
            }
            glass.repaint();
        });
        fadeTimer.start();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        UiKit.applyGlobal();
        SwingUtilities.invokeLater(() -> new BorrowingSystem().setVisible(true));
    }
}

class StartScreen extends JPanel {
    StartScreen(BorrowingSystem app) {
        setDoubleBuffered(true);
        setLayout(new GridBagLayout());
        setOpaque(false);

        RoundedPanel card = new RoundedPanel(24, UiKit.SURFACE, true);
        card.setDoubleBuffered(true);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(44, 56, 44, 56));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(6, 0, 6, 0); gbc.anchor = GridBagConstraints.CENTER;

        JLabel logo = new JLabel("\uD83D\uDCDA", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        card.add(logo, gbc);

        gbc.gridy++;
        JLabel title = new JLabel("Education Borrowing System", SwingConstants.CENTER);
        title.setFont(UiKit.H1); title.setForeground(UiKit.TEXT);
        card.add(title, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 18, 0);
        JLabel sub = new JLabel("Smart Resource Lending & Inventory Portal", SwingConstants.CENTER);
        sub.setFont(UiKit.CAPTION); sub.setForeground(UiKit.TEXT_SUB);
        card.add(sub, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 18, 0);
        Chip versionChip = new Chip("Powered by RedLuzon", UiKit.PRIMARY, UiKit.tint(UiKit.PRIMARY, 22));
        card.add(versionChip, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 0, 0);
        StyledButton startBtn = new StyledButton("Get Started  \u2192");
        startBtn.setPreferredSize(new Dimension(300, 42));
        card.add(startBtn, gbc);

        add(card);

        startBtn.addActionListener(e -> app.switchScreen("LOGIN"));
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

class StatusBar extends JPanel {
    private final JLabel leftLabel = new JLabel("Ready");
    private final JLabel clockLabel = new JLabel();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("EEE, MMM d \u00B7 hh:mm:ss a");

    StatusBar() {
        setLayout(new BorderLayout());
        setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, UiKit.BORDER), new EmptyBorder(7, 18, 7, 18)));
        setBackground(UiKit.SURFACE);
        leftLabel.setFont(UiKit.CAPTION); leftLabel.setForeground(UiKit.TEXT_SUB);
        clockLabel.setFont(UiKit.CAPTION); clockLabel.setForeground(UiKit.TEXT_SUB);
        add(leftLabel, BorderLayout.WEST); add(clockLabel, BorderLayout.EAST);
        Timer clock = new Timer(1000, e -> clockLabel.setText(LocalTime.now().atDate(LocalDate.now()).format(FMT)));
        clock.setInitialDelay(0); clock.start();
    }

    void setScreen(BorrowingSystem app, String key) {
        String name = key.equals("HOME") ? "Dashboard" : key.equals("CHECKOUT") ? "Check-Out" : "Returns";
        leftLabel.setText("Signed in as: " + app.getCurrentUser() + " (" + (app.isAdminUser() ? "Super Admin" : "Staff") + ")  \u00B7  View: " + name);
    }
}

class FadeGlass extends JPanel {
    float alpha = 0;
    FadeGlass() { setOpaque(false); }
    @Override
    protected void paintComponent(Graphics g) {
        if (alpha > 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(UiKit.BG.getRed(), UiKit.BG.getGreen(), UiKit.BG.getBlue(), Math.round(alpha * 255)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
        super.paintComponent(g);
    }
}

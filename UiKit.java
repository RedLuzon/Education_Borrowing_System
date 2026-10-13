package EducationBorrowingSystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

final class UiKit {
    static final Color BG          = new Color(0xFDF8F6);
    static final Color SURFACE     = Color.WHITE;
    static final Color PRIMARY     = new Color(0xF97316);
    static final Color PRIMARY_DK  = new Color(0xEA580C);
    static final Color PRIMARY_DKR = new Color(0x9A3412);
    static final Color ACCENT      = new Color(0xFB923C);
    static final Color SUCCESS     = new Color(0x059669);
    static final Color DANGER      = new Color(0xDC2626);
    static final Color WARNING     = new Color(0xD97706);
    static final Color SIDEBAR_BG  = new Color(0x1C1917);
    static final Color SIDEBAR_TX  = new Color(0xA8A29E);
    static final Color TEXT        = new Color(0x1C1917);
    static final Color TEXT_SUB    = new Color(0x78716C);
    static final Color BORDER      = new Color(0xE7E5E4);
    static final Color SELECT_BLUE = new Color(0xFFEDD5);
    static final Color ZEBRA       = new Color(0xFAFAF9);
    static final Color TOAST_BG    = new Color(0x1C1917);

    static final String FONT = "Segoe UI";
    static final Font H1      = new Font(FONT, Font.BOLD, 24);
    static final Font H2      = new Font(FONT, Font.BOLD, 18);
    static final Font SECTION = new Font(FONT, Font.BOLD, 14);
    static final Font BODY    = new Font(FONT, Font.PLAIN, 13);
    static final Font BODY_B  = new Font(FONT, Font.BOLD, 13);
    static final Font CAPTION = new Font(FONT, Font.PLAIN, 12);
    static final Font CAPTION_B = new Font(FONT, Font.BOLD, 11);
    static final Font BIG_NUM = new Font(FONT, Font.BOLD, 30);
    static final Font EMOJI   = new Font("Segoe UI Emoji", Font.PLAIN, 15);

    static void applyGlobal() {
        UIManager.put("ToolTip.font", UiKit.CAPTION);
        UIManager.put("ToolTip.background", new Color(0x1C1917));
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("ToolTip.border", new EmptyBorder(6, 10, 6, 10));
        UIManager.put("OptionPane.background", UiKit.SURFACE);
        UIManager.put("OptionPane.messageForeground", UiKit.TEXT);
        UIManager.put("OptionPane.messageFont", UiKit.BODY);
        UIManager.put("OptionPane.buttonFont", UiKit.BODY_B);
        UIManager.put("Panel.background", UiKit.SURFACE);
    }

    static Color categoryColor(String cat) {
        switch (cat == null ? "" : cat) {
            case "Equipment":  return new Color(0xF97316);
            case "Science":    return new Color(0x059669);
            case "Math":       return new Color(0xD97706);
            case "Books":      return new Color(0xDB2777);
            case "Technology": return new Color(0x0891B2);
            default:           return new Color(0x78716C);
        }
    }

    static Color tint(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    static void styleScroll(JScrollPane sp) {
        sp.setBorder(new RoundBorder(UiKit.BORDER, 10));
        sp.getViewport().setBackground(Color.WHITE);
        sp.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new StyledScrollBarUI());
        sp.getVerticalScrollBar().setUnitIncrement(16);
    }
}

class StyledScrollBarUI extends BasicScrollBarUI {
    @Override protected void configureScrollBarColors() {}
    @Override protected JButton createDecreaseButton(int orientation) { return zero(); }
    @Override protected JButton createIncreaseButton(int orientation) { return zero(); }
    private JButton zero() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        return b;
    }
    @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if (!c.isEnabled() || r.width <= 0 || r.height <= 0) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UiKit.tint(UiKit.PRIMARY, 120));
        g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
        g2.dispose();
    }
}

class RoundedPanel extends JPanel {
    private final int radius;
    private final Color fill;
    private final boolean shadow;
    private float alpha = 1f;

    RoundedPanel(int radius, Color fill, boolean shadow) {
        this.radius = radius; this.fill = fill; this.shadow = shadow; setOpaque(false);
    }
    void setAlpha(float a) { this.alpha = a; repaint(); }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (alpha < 1f) g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        int w = getWidth() - 4, h = getHeight() - 4;
        if (shadow) {
            g2.setColor(new Color(28, 25, 23, 14));
            g2.fillRoundRect(4, 8, w, h, radius, radius);
        }
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w, h, radius, radius);
        g2.setColor(new Color(231, 229, 228, 160));
        g2.drawRoundRect(0, 0, w, h, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class GradientPanel extends JPanel {
    private final Color c1, c2;
    private final int radius;
    GradientPanel(Color c1, Color c2, int radius) {
        this.c1 = c1; this.c2 = c2; this.radius = radius; setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth() - 4, h = getHeight() - 4;
        g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
        g2.fillRoundRect(0, 0, w, h, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class RoundBorder implements Border {
    private final Color color;
    private final int radius;
    RoundBorder(Color color, int radius) { this.color = color; this.radius = radius; }
    @Override public Insets getBorderInsets(Component c) { return new Insets(9, 14, 9, 14); }
    @Override public boolean isBorderOpaque() { return false; }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }
}

class StyledButton extends JButton {
    private final Color base, hover, press;
    private final int radius;
    private boolean hovering;

    StyledButton(String text) { this(text, UiKit.PRIMARY, Color.WHITE); }
    StyledButton(String text, Color base, Color fg) {
        super(text);
        this.base = base; this.hover = base.brighter(); this.press = base.darker(); this.radius = 12;
        setForeground(fg); setFont(UiKit.BODY_B); setCursor(new Cursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
        setBorder(new EmptyBorder(10, 18, 10, 18));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = !isEnabled() ? new Color(0xD6D3D1)
                : getModel().isPressed() ? press
                : hovering ? hover : base;
        g2.setColor(c);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.setColor(new Color(255, 255, 255, 45));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class GhostButton extends JButton {
    private final Color line;
    private boolean hovering;
    GhostButton(String text) {
        super(text);
        this.line = UiKit.PRIMARY;
        setForeground(UiKit.PRIMARY); setFont(UiKit.BODY_B); setCursor(new Cursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
        setBorder(new EmptyBorder(10, 18, 10, 18));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (hovering) {
            g2.setColor(new Color(249, 115, 22, 18));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        }
        g2.setColor(line);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}

class LinkButton extends JButton {
    private boolean hovering;
    LinkButton(String text) {
        super(text);
        setForeground(UiKit.PRIMARY); setFont(UiKit.CAPTION_B); setCursor(new Cursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
        setBorder(new EmptyBorder(4, 6, 4, 6));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (hovering) {
            g2.setColor(new Color(249, 115, 22, 14));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
        }
        g2.dispose();
        setForeground(hovering ? UiKit.PRIMARY_DKR : UiKit.PRIMARY);
        super.paintComponent(g);
    }
}

class Chip extends JLabel {
    private Color fill;
    Chip(String text, Color fg, Color bg) {
        super(text); this.fill = bg; setForeground(fg); setFont(UiKit.CAPTION_B); setOpaque(false);
        setBorder(new EmptyBorder(3, 9, 3, 9));
    }
    void setFill(Color c) { this.fill = c; repaint(); }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}

class RadioIcon implements Icon {
    @Override public int getIconWidth() { return 18; }
    @Override public int getIconHeight() { return 18; }
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        AbstractButton b = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(b.isSelected() ? UiKit.PRIMARY : UiKit.BORDER);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(x + 1, y + 1, 16, 16);
        if (b.isSelected()) {
            g2.setColor(UiKit.PRIMARY);
            g2.fillOval(x + 5, y + 5, 8, 8);
        }
        g2.dispose();
    }
}

class FieldBox extends JPanel {
    private final JTextField field;
    private final boolean isPassword;
    private Color borderColor = UiKit.BORDER;

    FieldBox(String icon, boolean password) {
        this.isPassword = password;
        setLayout(new BorderLayout()); setOpaque(false); setBorder(new EmptyBorder(1, 1, 1, 1));
        JLabel ic = new JLabel(icon);
        ic.setFont(UiKit.EMOJI); ic.setForeground(UiKit.TEXT_SUB); ic.setBorder(new EmptyBorder(0, 12, 0, 2));

        field = password ? new JPasswordField() : new JTextField();
        field.setFont(UiKit.BODY); field.setForeground(UiKit.TEXT); field.setOpaque(false);
        field.setBorder(new EmptyBorder(9, 6, 9, 10)); field.setCaretColor(UiKit.PRIMARY);

        add(ic, BorderLayout.WEST); add(field, BorderLayout.CENTER);

        if (password) {
            ((JPasswordField) field).setEchoChar('\u2022');
            JButton eye = new JButton("\uD83D\uDC41");
            eye.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            eye.setFocusable(false); eye.setContentAreaFilled(false); eye.setBorderPainted(false);
            eye.setCursor(new Cursor(Cursor.HAND_CURSOR)); eye.setBorder(new EmptyBorder(0, 2, 0, 10));
            eye.addActionListener(e -> {
                JPasswordField pf = (JPasswordField) field;
                if (pf.getEchoChar() == 0) { pf.setEchoChar('\u2022'); eye.setText("\uD83D\uDC41"); }
                else { pf.setEchoChar((char) 0); eye.setText("\uD83D\uDE48"); }
            });
            add(eye, BorderLayout.EAST);
        }

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { borderColor = UiKit.PRIMARY; repaint(); }
            @Override public void focusLost(FocusEvent e)  { borderColor = UiKit.BORDER; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UiKit.SURFACE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
    JTextField getField() { return field; }
    String getText() { return field.getText().trim(); }
    void setText(String t) { field.setText(t); }
}

class ScreenHeader extends GradientPanel {
    ScreenHeader(String title, String subtitle) {
        super(UiKit.PRIMARY_DKR, UiKit.PRIMARY, 0);
        setLayout(new BorderLayout()); setBorder(new EmptyBorder(20, 28, 20, 28));
        JPanel left = new JPanel(); left.setOpaque(false); left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title); t.setFont(UiKit.H2); t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle); s.setFont(UiKit.CAPTION); s.setForeground(new Color(0xFFEDD5));
        left.add(t); left.add(Box.createVerticalStrut(4)); left.add(s);
        add(left, BorderLayout.WEST);
        Chip today = new Chip(LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy")),
                new Color(0xFFEDD5), new Color(255, 255, 255, 28));
        add(today, BorderLayout.EAST);
    }
}

class SectionTitle extends JLabel {
    SectionTitle(String text) { super(text); setFont(UiKit.SECTION); setForeground(UiKit.TEXT); setBorder(new EmptyBorder(8, 0, 2, 0)); }
}

final class Fields {
    static JTextField text() {
        JTextField f = new JTextField();
        f.setFont(UiKit.BODY); f.setForeground(UiKit.TEXT); f.setBackground(UiKit.SURFACE);
        f.setBorder(new RoundBorder(UiKit.BORDER, 10)); f.setCaretColor(UiKit.PRIMARY);
        return f;
    }
    static JLabel caption(String text) {
        JLabel l = new JLabel(text); l.setFont(UiKit.CAPTION); l.setForeground(UiKit.TEXT_SUB);
        return l;
    }
}

class Toast {
    enum Type {
        SUCCESS(UiKit.SUCCESS, "\u2713"), ERROR(UiKit.DANGER, "\u2715"), INFO(UiKit.PRIMARY, "\u2139");
        final Color color; final String icon;
        Type(Color c, String i) { color = c; icon = i; }
    }
    static void show(Component anchor, String message, Type type) {
        Window w = SwingUtilities.getWindowAncestor(anchor);
        if (!(w instanceof JFrame)) return;
        JFrame frame = (JFrame) w;
        JWindow toast = new JWindow(frame);
        RoundedPanel content = new RoundedPanel(14, UiKit.TOAST_BG, true);
        content.setLayout(new BorderLayout(10, 0)); content.setBorder(new EmptyBorder(12, 16, 12, 18));
        JLabel icon = new JLabel(type.icon); icon.setFont(new Font(UiKit.FONT, Font.BOLD, 15)); icon.setForeground(type.color);
        JLabel text = new JLabel("<html><body style='width:260px'>" + message + "</body></html>");
        text.setFont(UiKit.BODY); text.setForeground(Color.WHITE);
        content.add(icon, BorderLayout.WEST); content.add(text, BorderLayout.CENTER);
        toast.setContentPane(content); toast.pack();
        final int finalX = frame.getX() + frame.getWidth() - toast.getWidth() - 28;
        final int finalY = frame.getY() + frame.getHeight() - toast.getHeight() - 88;
        final int startX = finalX + 40;
        try { toast.setOpacity(0f); } catch (Exception ignored) {}
        toast.setLocation(startX, finalY); toast.setVisible(true);
        final int[] frameCount = {0};
        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            frameCount[0]++;
            int f = frameCount[0];
            float alpha; int x = startX;
            if (f <= 10) { float p = f / 10f; alpha = p; x = startX + (int) ((finalX - startX) * p); }
            else if (f <= 90) { alpha = 1f; x = finalX; }
            else if (f <= 105) { alpha = 1f - (f - 90) / 15f; x = finalX; }
            else { timer.stop(); toast.dispose(); return; }
            try { toast.setOpacity(Math.max(0f, Math.min(1f, alpha))); } catch (Exception ignored) {}
            toast.setLocation(x, finalY);
        });
        timer.start();
    }
}

final class Anim {
    static void countUp(JLabel label, int target) {
        Timer t = new Timer(24, null);
        final int[] v = {0};
        t.addActionListener(e -> {
            v[0] += Math.max(1, (target - v[0]) / 5);
            if (v[0] >= target) { v[0] = target; t.stop(); }
            label.setText(String.valueOf(v[0]));
        });
        t.start();
    }
}

class DonutChart extends JPanel {
    private final Map<String, Integer> data = new java.util.LinkedHashMap<>();
    DonutChart() { setOpaque(false); setPreferredSize(new Dimension(210, 210)); setMinimumSize(new Dimension(210, 210)); }
    void setData(Map<String, Integer> d) { data.clear(); data.putAll(d); repaint(); }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int total = 0; for (int v : data.values()) total += v;
        int size = Math.min(getWidth(), getHeight()) - 8;
        int x = (getWidth() - size) / 2, y = (getHeight() - size) / 2;
        if (total == 0) {
            g2.setColor(UiKit.BORDER); g2.setStroke(new BasicStroke(24f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(x + 12, y + 12, size - 24, size - 24);
        } else {
            double angle = 90;
            g2.setStroke(new BasicStroke(24f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            for (Map.Entry<String, Integer> e : data.entrySet()) {
                double extent = e.getValue() / (double) total * 360.0;
                double drawn = Math.max(0, extent - 2.5);
                g2.setColor(UiKit.categoryColor(e.getKey()));
                g2.draw(new Arc2D.Double(x + 12, y + 12, size - 24, size - 24, angle, -drawn, Arc2D.OPEN));
                angle -= extent;
            }
        }
        String big = String.valueOf(total); g2.setColor(UiKit.TEXT); g2.setFont(new Font(UiKit.FONT, Font.BOLD, 26));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(big, getWidth() / 2 - fm.stringWidth(big) / 2, getHeight() / 2 + 2);
        g2.setColor(UiKit.TEXT_SUB); g2.setFont(UiKit.CAPTION);
        String cap = "units in stock";
        g2.drawString(cap, getWidth() / 2 - g2.getFontMetrics().stringWidth(cap) / 2, getHeight() / 2 + 20);
        g2.dispose();
        super.paintComponent(g);
    }
}
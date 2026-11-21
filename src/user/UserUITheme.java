package user;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Tiện ích giao diện UserUITheme
 * Cung cấp bảng màu, font chữ chuẩn hỗ trợ tiếng Việt mượt mà
 * và các tiện ích tạo component Swing đồng bộ với phân hệ Admin.
 */
public final class UserUITheme {

    private UserUITheme() {
        // Utility class - không khởi tạo instance
    }

    // =========================================================================
    // 1. BẢNG MÀU CHUẨN (ĐỒNG BỘ VỚI PHÂN HỆ ADMIN)
    // =========================================================================

    /** Màu nền chính của ứng dụng: nhạt, dịu mắt, đồng bộ Admin BACKGROUND_COLOR */
    public static final Color COLOR_BACKGROUND = new Color(245, 247, 250);

    /** Màu thanh Sidebar / Điều hướng: tông xanh than đậm sang trọng đồng bộ Admin SIDEBAR_COLOR */
    public static final Color COLOR_SIDEBAR = new Color(23, 37, 61);

    /** Màu hover trên Sidebar */
    public static final Color COLOR_SIDEBAR_HOVER = new Color(36, 56, 92);

    /** Màu mục đang được chọn trên Sidebar (đồng bộ Admin SELECTED_COLOR) */
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(45, 70, 105);

    /** Màu chữ trên Sidebar */
    public static final Color COLOR_SIDEBAR_TEXT = Color.WHITE;

    /** Màu chữ phân mục / tiêu đề nhóm trên Sidebar */
    public static final Color COLOR_SIDEBAR_MUTED = new Color(150, 165, 185);

    /** Màu nút nhấn chính (Primary Blue): đồng bộ Admin BLUE_COLOR */
    public static final Color COLOR_PRIMARY = new Color(45, 105, 190);

    /** Màu hover cho nút nhấn chính */
    public static final Color COLOR_PRIMARY_HOVER = new Color(36, 92, 170);

    /** Màu khi click (pressed) nút nhấn chính */
    public static final Color COLOR_PRIMARY_PRESSED = new Color(28, 75, 142);

    /** Màu chữ trên nút nhấn chính */
    public static final Color COLOR_PRIMARY_TEXT = Color.WHITE;

    /** Màu bong bóng chat cho tin nhắn của mình gửi (Xanh dương nhạt) */
    public static final Color COLOR_BUBBLE_SENT = new Color(220, 237, 255);

    /** Màu viền tinh tế cho bong bóng chat gửi */
    public static final Color COLOR_BUBBLE_SENT_BORDER = new Color(195, 222, 250);

    /** Màu chữ nội dung trong bong bóng chat gửi (tương phản rõ trên nền xanh nhạt) */
    public static final Color COLOR_BUBBLE_SENT_TEXT = new Color(15, 35, 65);

    /** Màu bong bóng chat cho tin nhắn người khác gửi (Xám sáng) */
    public static final Color COLOR_BUBBLE_RECEIVED = new Color(240, 242, 245);

    /** Màu viền tinh tế cho bong bóng chat nhận */
    public static final Color COLOR_BUBBLE_RECEIVED_BORDER = new Color(225, 230, 238);

    /** Màu chữ nội dung trong bong bóng chat nhận (đồng bộ Admin TEXT_COLOR) */
    public static final Color COLOR_BUBBLE_RECEIVED_TEXT = new Color(30, 45, 65);

    /** Nền thẻ / Container / Card / Dialog / Toolbar */
    public static final Color COLOR_CARD_BACKGROUND = Color.WHITE;

    /** Màu đường viền khung / Đường phân cách bảng và panel */
    public static final Color COLOR_BORDER = new Color(225, 230, 238);

    /** Màu chữ tiêu đề và văn bản chính */
    public static final Color COLOR_TEXT_PRIMARY = new Color(30, 45, 65);

    /** Màu chữ phụ / mô tả / ngày giờ */
    public static final Color COLOR_TEXT_SECONDARY = new Color(100, 115, 130);

    /** Màu chữ mờ / placeholder */
    public static final Color COLOR_TEXT_MUTED = new Color(160, 170, 185);

    /** Trạng thái trực tuyến (Online) - Xanh lá tươi */
    public static final Color COLOR_STATUS_ONLINE = new Color(40, 167, 69);

    /** Trạng thái ngoại tuyến (Offline) - Xám nhạt */
    public static final Color COLOR_STATUS_OFFLINE = new Color(160, 170, 185);

    /** Màu thông báo lỗi / Nguy hiểm / Nút xóa / Khóa tài khoản / Chặn */
    public static final Color COLOR_DANGER = new Color(220, 53, 69);
    public static final Color COLOR_DANGER_HOVER = new Color(200, 40, 55);

    /** Màu cảnh báo / Báo cáo spam / Chú ý */
    public static final Color COLOR_WARNING = new Color(243, 156, 18);

    /** Màu nền khi hover danh sách cuộc trò chuyện, bạn bè */
    public static final Color COLOR_ITEM_HOVER = new Color(243, 246, 251);

    /** Màu nền mục được chọn trong danh sách */
    public static final Color COLOR_ITEM_SELECTED = new Color(230, 240, 255);

    // =========================================================================
    // 2. FONT CHỮ CHUẨN (HỖ TRỢ TIẾNG VIỆT MƯỢT MÀ: SEGOE UI / ARIAL)
    // =========================================================================

    /** Tên font được phát hiện phù hợp nhất trên hệ điều hành */
    public static final String FONT_FAMILY = detectBestVietnameseFont();

    private static String detectBestVietnameseFont() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            List<String> available = Arrays.asList(ge.getAvailableFontFamilyNames());
            // Segoe UI hiển thị tiếng Việt sắc nét và hiện đại trên Windows
            if (available.contains("Segoe UI")) {
                return "Segoe UI";
            }
            // Arial phổ biến và hỗ trợ đầy đủ ký tự Unicode tiếng Việt
            if (available.contains("Arial")) {
                return "Arial";
            }
        } catch (Exception ignored) {
            // Trường hợp không truy cập được GraphicsEnvironment
        }
        return Font.SANS_SERIF;
    }

    /** Tạo font chữ với kiểu dáng và kích thước tùy chỉnh */
    public static Font getFont(int style, float size) {
        return new Font(FONT_FAMILY, style, Math.round(size));
    }

    // Các cỡ font chuẩn dùng xuyên suốt giao diện
    public static final Font FONT_TITLE_LARGE     = getFont(Font.BOLD, 24f);
    public static final Font FONT_TITLE_MEDIUM    = getFont(Font.BOLD, 18f);
    public static final Font FONT_TITLE_SMALL     = getFont(Font.BOLD, 15f);
    public static final Font FONT_SECTION_HEADER  = getFont(Font.BOLD, 11f);

    public static final Font FONT_BODY            = getFont(Font.PLAIN, 14f);
    public static final Font FONT_BODY_BOLD       = getFont(Font.BOLD, 14f);
    public static final Font FONT_SMALL           = getFont(Font.PLAIN, 12f);
    public static final Font FONT_SMALL_BOLD      = getFont(Font.BOLD, 12f);
    public static final Font FONT_TIMESTAMP       = getFont(Font.PLAIN, 11f);

    public static final Font FONT_CHAT_MESSAGE    = getFont(Font.PLAIN, 14f);
    public static final Font FONT_CHAT_SENDER     = getFont(Font.BOLD, 13f);

    // =========================================================================
    // 3. THIẾT LẬP THEME TOÀN CỤC & KHỬ RĂNG CƯA (ANTIALIASING)
    // =========================================================================

    /**
     * Kích hoạt chế độ vẽ mượt mà (Antialiasing) cho Graphics2D
     */
    public static void enableAntialiasing(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
    }

    /**
     * Áp dụng font tiếng Việt chuẩn và giao diện hệ thống cho toàn bộ UIManager.
     * Gọi hàm này trước khi khởi tạo các JFrame/JDialog.
     */
    public static void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Giữ giao diện mặc định nếu không set được
        }

        // Bật khử răng cưa chữ cho Swing
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");

        // Đồng bộ font Segoe UI / Arial tiếng Việt cho tất cả component trong Swing
        FontUIResource fontResource = new FontUIResource(getFont(Font.PLAIN, 13f));
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontResource);
            }
        }

        // Cấu hình màu nền tooltip và scrollbar
        UIManager.put("ToolTip.background", Color.WHITE);
        UIManager.put("ToolTip.foreground", COLOR_TEXT_PRIMARY);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(COLOR_BORDER));
    }

    // =========================================================================
    // 4. TIỆN ÍCH TẠO NÚT NHẤN (BUTTON FACTORY)
    // =========================================================================

    /**
     * Tạo nút nhấn chính (Primary Blue Button) - đồng bộ với Admin BLUE_COLOR
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new RoundedButton(text, 8, COLOR_PRIMARY, COLOR_PRIMARY_HOVER, COLOR_PRIMARY_PRESSED);
        button.setFont(FONT_BODY_BOLD);
        button.setForeground(COLOR_PRIMARY_TEXT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        return button;
    }

    /**
     * Tạo nút phụ (Secondary / Outline Button) - nền trắng, viền nhẹ, chữ sẫm
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new RoundedButton(text, 8, COLOR_CARD_BACKGROUND, new Color(245, 248, 252), new Color(235, 240, 248));
        button.setFont(FONT_BODY_BOLD);
        button.setForeground(COLOR_TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new RoundedLineBorder(COLOR_BORDER, 8, 1),
                new EmptyBorder(9, 19, 9, 19)
        ));
        return button;
    }

    /**
     * Tạo nút nguy hiểm (Danger Button) - dùng cho Hủy kết bạn, Xóa, Chặn, Thoát nhóm
     */
    public static JButton createDangerButton(String text) {
        JButton button = new RoundedButton(text, 8, COLOR_DANGER, COLOR_DANGER_HOVER, new Color(175, 30, 45));
        button.setFont(FONT_BODY_BOLD);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        return button;
    }

    /**
     * Tạo nút Sidebar / Menu điều hướng - đồng bộ Sidebar phân hệ Admin
     */
    public static JButton createSidebarButton(String text, boolean isActive) {
        JButton button = new JButton(text);
        button.setFont(FONT_BODY);
        button.setForeground(COLOR_SIDEBAR_TEXT);
        button.setBackground(isActive ? COLOR_SIDEBAR_ACTIVE : COLOR_SIDEBAR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(12, 22, 12, 15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    button.setBackground(COLOR_SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    button.setBackground(COLOR_SIDEBAR);
                }
            }
        });

        return button;
    }

    // =========================================================================
    // 5. TIỆN ÍCH TẠO Ô NHẬP LIỆU (TEXT INPUT FACTORY)
    // =========================================================================

    /**
     * Tạo trường nhập liệu văn bản với placeholder và viền bo góc hiện đại
     */
    public static JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(FONT_BODY);
        textField.setForeground(COLOR_TEXT_PRIMARY);
        textField.setBackground(COLOR_CARD_BACKGROUND);
        textField.setCaretColor(COLOR_PRIMARY);

        Border defaultBorder = new CompoundBorder(
                new RoundedLineBorder(COLOR_BORDER, 8, 1),
                new EmptyBorder(8, 12, 8, 12)
        );
        Border focusedBorder = new CompoundBorder(
                new RoundedLineBorder(COLOR_PRIMARY, 8, 1),
                new EmptyBorder(8, 12, 8, 12)
        );

        textField.setBorder(defaultBorder);

        if (placeholder != null && !placeholder.trim().isEmpty()) {
            textField.setText(placeholder);
            textField.setForeground(COLOR_TEXT_MUTED);

            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    textField.setBorder(focusedBorder);
                    if (textField.getText().equals(placeholder)) {
                        textField.setText("");
                        textField.setForeground(COLOR_TEXT_PRIMARY);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    textField.setBorder(defaultBorder);
                    if (textField.getText().trim().isEmpty()) {
                        textField.setText(placeholder);
                        textField.setForeground(COLOR_TEXT_MUTED);
                    }
                }
            });
        } else {
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    textField.setBorder(focusedBorder);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    textField.setBorder(defaultBorder);
                }
            });
        }

        return textField;
    }

    /**
     * Tạo ô nhập mật khẩu với viền bo góc hiện đại
     */
    public static JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(FONT_BODY);
        passwordField.setForeground(COLOR_TEXT_PRIMARY);
        passwordField.setBackground(COLOR_CARD_BACKGROUND);
        passwordField.setCaretColor(COLOR_PRIMARY);

        Border defaultBorder = new CompoundBorder(
                new RoundedLineBorder(COLOR_BORDER, 8, 1),
                new EmptyBorder(8, 12, 8, 12)
        );
        Border focusedBorder = new CompoundBorder(
                new RoundedLineBorder(COLOR_PRIMARY, 8, 1),
                new EmptyBorder(8, 12, 8, 12)
        );

        passwordField.setBorder(defaultBorder);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(focusedBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(defaultBorder);
            }
        });

        return passwordField;
    }

    // =========================================================================
    // 6. TIỆN ÍCH BONG BÓNG CHAT (CHAT BUBBLE FACTORY)
    // =========================================================================

    /**
     * Tạo panel bong bóng chat hoàn chỉnh với:
     * - Tin mình gửi (isSelf = true): căn lề phải, bong bóng xanh dương nhạt (COLOR_BUBBLE_SENT)
     * - Tin người khác gửi (isSelf = false): căn lề trái, hiển thị tên người gửi, bong bóng xám sáng (COLOR_BUBBLE_RECEIVED)
     *
     * @param senderName Tên người gửi (hiển thị khi isSelf = false)
     * @param message    Nội dung tin nhắn (hỗ trợ xuống dòng, tiếng Việt mượt mà)
     * @param timestamp  Thời gian gửi (ví dụ: "14:35" hoặc "21/11/2026 14:35")
     * @param isSelf     true nếu là tin của mình, false nếu là tin người khác gửi
     */
    public static JPanel createChatBubble(String senderName, String message, String timestamp, boolean isSelf) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(4, 12, 4, 12));

        // Container bên trong
        JPanel bubbleBox = new JPanel();
        bubbleBox.setLayout(new BoxLayout(bubbleBox, BoxLayout.Y_AXIS));
        bubbleBox.setOpaque(false);

        // Nếu là tin người khác gửi và có tên, hiển thị tên người gửi phía trên
        if (!isSelf && senderName != null && !senderName.trim().isEmpty()) {
            JLabel nameLabel = new JLabel(senderName);
            nameLabel.setFont(FONT_CHAT_SENDER);
            nameLabel.setForeground(COLOR_PRIMARY);
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            nameLabel.setBorder(new EmptyBorder(0, 4, 3, 0));
            bubbleBox.add(nameLabel);
        }

        // Tạo bong bóng bo góc vẽ mượt mà
        Color bubbleBg = isSelf ? COLOR_BUBBLE_SENT : COLOR_BUBBLE_RECEIVED;
        Color bubbleBorder = isSelf ? COLOR_BUBBLE_SENT_BORDER : COLOR_BUBBLE_RECEIVED_BORDER;
        Color textColor = isSelf ? COLOR_BUBBLE_SENT_TEXT : COLOR_BUBBLE_RECEIVED_TEXT;

        RoundedPanel bubble = new RoundedPanel(16, bubbleBg, bubbleBorder);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(new EmptyBorder(10, 14, 8, 14));
        bubble.setAlignmentX(isSelf ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        // Nội dung tin nhắn
        JTextArea msgArea = new JTextArea(message);
        msgArea.setFont(FONT_CHAT_MESSAGE);
        msgArea.setForeground(textColor);
        msgArea.setOpaque(false);
        msgArea.setEditable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setFocusable(false);
        int cols = Math.min(34, Math.max(14, message.length()));
        msgArea.setColumns(cols);
        msgArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        bubble.add(msgArea);

        // Thời gian gửi
        if (timestamp != null && !timestamp.trim().isEmpty()) {
            bubble.add(Box.createVerticalStrut(4));
            JLabel timeLabel = new JLabel(timestamp);
            timeLabel.setFont(FONT_TIMESTAMP);
            timeLabel.setForeground(COLOR_TEXT_SECONDARY);
            timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            bubble.add(timeLabel);
        }

        bubbleBox.add(bubble);

        if (isSelf) {
            bubbleBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
            wrapper.add(bubbleBox, BorderLayout.EAST);
        } else {
            bubbleBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrapper.add(bubbleBox, BorderLayout.WEST);
        }

        return wrapper;
    }

    // =========================================================================
    // 7. TIỆN ÍCH PANEL THẺ & HUY HIỆU TRẠNG THÁI (CARD & BADGES)
    // =========================================================================

    /**
     * Tạo panel Card nền trắng, viền mảnh chuẩn đồng bộ phân hệ Admin
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        return panel;
    }

    /**
     * Tạo huy hiệu chấm tròn trạng thái Trực tuyến / Ngoại tuyến
     */
    public static JPanel createStatusBadge(boolean isOnline) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panel.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Arial", Font.PLAIN, 12));
        dot.setForeground(isOnline ? COLOR_STATUS_ONLINE : COLOR_STATUS_OFFLINE);

        JLabel text = new JLabel(isOnline ? "Đang hoạt động" : "Ngoại tuyến");
        text.setFont(FONT_SMALL);
        text.setForeground(isOnline ? COLOR_STATUS_ONLINE : COLOR_TEXT_MUTED);

        panel.add(dot);
        panel.add(text);
        return panel;
    }

    // =========================================================================
    // 8. CÁC LỚP BỔ TRỢ ĐỒ HỌA BO GÓC (CUSTOM RENDERING HELPERS)
    // =========================================================================

    /**
     * Nút bo góc với hiệu ứng đổi màu hover và click mượt mà
     */
    public static class RoundedButton extends JButton {
        private final int radius;
        private final Color normalColor;
        private final Color hoverColor;
        private final Color pressedColor;
        private boolean isHovered = false;
        private boolean isPressed = false;

        public RoundedButton(String text, int radius, Color normal, Color hover, Color pressed) {
            super(text);
            this.radius = radius;
            this.normalColor = normal;
            this.hoverColor = hover;
            this.pressedColor = pressed;
            setContentAreaFilled(false);

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

                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableAntialiasing(g2);

            Color bgColor = normalColor;
            if (isPressed) {
                bgColor = pressedColor;
            } else if (isHovered) {
                bgColor = hoverColor;
            }

            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();

            super.paintComponent(g);
        }
    }

    /**
     * Panel bo góc mượt mà cho bong bóng chat và thẻ hiển thị
     */
    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color backgroundColor;
        private final Color borderColor;

        public RoundedPanel(int radius, Color backgroundColor, Color borderColor) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableAntialiasing(g2);

            // Vẽ nền bo góc
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));

            // Vẽ viền bo góc nếu có borderColor
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Đường viền bo tròn tùy chỉnh (Rounded Line Border)
     */
    public static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedLineBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableAntialiasing(g2);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 4, thickness + 2, thickness + 4);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = thickness + 4;
            insets.top = insets.bottom = thickness + 2;
            return insets;
        }
    }
}

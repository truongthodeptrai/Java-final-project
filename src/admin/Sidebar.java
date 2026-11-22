package admin;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;



public class Sidebar extends JPanel {

    private final Color SIDEBAR_COLOR = new Color(23, 37, 61);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color SELECTED_COLOR = new Color(45, 70, 105);

    private JButton selectedButton;

    private static class UserIcon implements Icon {

        public int getIconWidth() {
            return 18;
        }

        public int getIconHeight() {
            return 18;
        }

        public void paintIcon(
            Component c, Graphics g, int x, int y
        ) {
            g.setColor(Color.LIGHT_GRAY);

            g.fillOval(x + 6, y, 7, 7);
            g.fillRoundRect(x + 2, y + 9, 15, 9, 8, 8);
        }
    }

    public Sidebar(Consumer<String> navigation) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SIDEBAR_COLOR);
        setPreferredSize(new Dimension(240, 0));

        // Khu vực menu
        addMenuItem("Bảng điều khiển", "dashboard", navigation);

        addSectionTitle("QUẢN LÝ NGƯỜI DÙNG");

        addMenuItem("Người dùng", "users", navigation);
        addMenuItem("Lịch sử đăng nhập", "login", navigation);
        addMenuItem("Thống kê bạn bè", "friends", navigation);

        addSectionTitle("QUẢN LÝ NHÓM");

        addMenuItem("Nhóm trò chuyện", "groups", navigation);
        addMenuItem("Báo cáo spam", "spam", navigation);

        addSectionTitle("THỐNG KÊ");

        addMenuItem("Đăng ký mới", "registrations", navigation);
        addMenuItem("Biểu đồ đăng ký mới", "registration_chart", navigation);
        addMenuItem("Hoạt động người dùng", "activity", navigation);
        addMenuItem("Biểu đồ hoạt động", "activity_chart", navigation);

        add(Box.createVerticalGlue());

        JLabel adminLabel = new JLabel(
            "  \uD83D\uDC64  Tài khoản quản trị"
        );

        adminLabel.setForeground(Color.LIGHT_GRAY);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 13));

        // Thêm icon hình người
        adminLabel.setIcon(new UserIcon());
        adminLabel.setIconTextGap(10);

        adminLabel.setBorder(
            BorderFactory.createEmptyBorder(15, 10, 20, 10)
        );

        add(adminLabel);
    }

    private void addSectionTitle(String title) {

        JLabel label = new JLabel(title);

        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(new Color(150, 165, 185));

        label.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 8, 10)
        );

        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(label);
    }

    private void addMenuItem(
        String title,
        String page,
        Consumer<String> navigation
    ) {

        JButton button = new JButton(title);

        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(SIDEBAR_COLOR);

        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.setBorder(
            BorderFactory.createEmptyBorder(12, 25, 12, 10)
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 45)
        );

        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addActionListener(e -> {

            if (selectedButton != null) {
                selectedButton.setBackground(SIDEBAR_COLOR);
            }

            selectedButton = button;
            selectedButton.setBackground(SELECTED_COLOR);

            navigation.accept(page);
        });

        add(button);
    }
}

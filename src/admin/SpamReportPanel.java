package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SpamReportPanel extends JPanel {

    private final Color BACKGROUND_COLOR =
        new Color(245, 247, 250);

    private final Color TEXT_COLOR =
        new Color(30, 45, 65);

    private final Color BLUE_COLOR =
        new Color(45, 105, 190);

    public SpamReportPanel() {

        setLayout(new BorderLayout(0, 25));

        setBackground(BACKGROUND_COLOR);

        setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );

        // Tiêu đề trang
        add(createHeading(), BorderLayout.NORTH);

        // Nội dung chính
        JPanel mainContent = new JPanel(
            new BorderLayout(0, 20)
        );

        mainContent.setBackground(BACKGROUND_COLOR);

        mainContent.add(
            createToolbar(),
            BorderLayout.NORTH
        );

        mainContent.add(
            createReportTable(),
            BorderLayout.CENTER
        );

        add(mainContent, BorderLayout.CENTER);
    }

    // Tạo phần tiêu đề trang
    private JPanel createHeading() {

        JPanel panel = new JPanel();

        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("Báo cáo spam");

        title.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        title.setForeground(TEXT_COLOR);

        JLabel description = new JLabel(
            "Xem xét báo cáo spam và quản lý tài khoản bị báo cáo"
        );

        description.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        description.setForeground(Color.GRAY);

        panel.add(title);

        panel.add(Box.createVerticalStrut(8));

        panel.add(description);

        return panel;
    }

    // Tạo thanh công cụ tìm kiếm, lọc và sắp xếp
    private JPanel createToolbar() {

        JPanel panel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 12, 10)
        );

        panel.setBackground(Color.WHITE);

        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(225, 230, 238)
                ),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            )
        );

        // Ô tìm kiếm
        JTextField searchField = new JTextField(18);

        searchField.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        searchField.setText("Tìm kiếm...");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(
            new java.awt.event.FocusAdapter() {

                @Override
                public void focusGained(
                    java.awt.event.FocusEvent e
                ) {

                    if (searchField.getText().equals("Tìm kiếm...")) {
                        searchField.setText("");
                        searchField.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(
                    java.awt.event.FocusEvent e
                ) {

                    if (searchField.getText().isEmpty()) {
                        searchField.setText("Tìm kiếm...");
                        searchField.setForeground(Color.GRAY);
                    }
                }
            }
        );

        // Bộ lọc ngày báo cáo
        String[] dateOptions = {
            "Tất cả thời gian",
            "Hôm nay",
            "7 ngày qua",
            "30 ngày qua"
        };

        JComboBox<String> dateFilter =
            new JComboBox<>(dateOptions);

        dateFilter.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        // Các tùy chọn sắp xếp
        String[] sortOptions = {
            "Thời gian báo cáo",
            "Tên đăng nhập"
        };

        JComboBox<String> sortFilter =
            new JComboBox<>(sortOptions);

        sortFilter.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        // Nút khóa tài khoản
        JButton lockButton = new JButton(
            "Khóa tài khoản được chọn"
        );

        lockButton.setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        lockButton.setForeground(Color.WHITE);

        lockButton.setBackground(BLUE_COLOR);

        lockButton.setFocusPainted(false);

        lockButton.setBorderPainted(false);

        panel.add(searchField);

        panel.add(new JLabel("Ngày báo cáo:"));

        panel.add(dateFilter);

        panel.add(new JLabel("Sắp xếp theo:"));

        panel.add(sortFilter);

        panel.add(lockButton);

        return panel;
    }

    // Tạo bảng báo cáo spam
    private JPanel createReportTable() {

        JPanel panel = new JPanel(
            new BorderLayout(0, 15)
        );

        panel.setBackground(Color.WHITE);

        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(225, 230, 238)
                ),
                BorderFactory.createEmptyBorder(
                    20, 20, 20, 20
                )
            )
        );

        JLabel title = new JLabel("Danh sách báo cáo");

        title.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        title.setForeground(TEXT_COLOR);

        panel.add(title, BorderLayout.NORTH);

        // Các cột của bảng
        String[] columns = {
            "Người dùng bị báo cáo",
            "Người báo cáo",
            "Lý do",
            "Thời gian báo cáo"
        };

        // Dữ liệu báo cáo mẫu
        Object[][] data = {
            {"spam123", "john123", "Tin nhắn rác",
                "21/11/2026 08:30"},

            {"user456", "mary456", "Quấy rối",
                "20/11/2026 15:20"},

            {"bot789", "alex789", "Liên kết đáng ngờ",
                "19/11/2026 12:45"},

            {"fake101", "david101", "Tài khoản giả mạo",
                "18/11/2026 19:10"},

            {"spam202", "sarah202", "Tin nhắn rác",
                "17/11/2026 10:30"},

            {"user303", "peter303", "Nội dung không phù hợp",
                "16/11/2026 14:15"}
        };

        DefaultTableModel model = new DefaultTableModel(
            data, columns
        ) {

            @Override
            public boolean isCellEditable(
                int row, int column
            ) {
                return false;
            }
        };

        JTable table = new JTable(model);

        table.setRowHeight(40);

        table.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        table.getTableHeader().setBackground(
            new Color(240, 243, 248)
        );

        table.setSelectionBackground(
            new Color(225, 235, 250)
        );

        table.setShowVerticalLines(false);

        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(
            BorderFactory.createEmptyBorder()
        );

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}

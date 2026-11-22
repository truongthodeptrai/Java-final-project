package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FriendStatisticsPanel extends JPanel {

    private final Color BACKGROUND_COLOR =
        new Color(245, 247, 250);

    private final Color TEXT_COLOR =
        new Color(30, 45, 65);

    public FriendStatisticsPanel() {

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
            createFriendTable(),
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

        JLabel title = new JLabel("Thống kê bạn bè");

        title.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        title.setForeground(TEXT_COLOR);

        JLabel description = new JLabel(
            "Xem người dùng và thống kê quan hệ bạn bè"
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

        // Văn bản gợi ý trong ô nhập liệu
        searchField.setText("Tìm kiếm...");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {

        @Override
        public void focusGained(java.awt.event.FocusEvent e) {

            if (searchField.getText().equals("Tìm kiếm...")) {
               searchField.setText("");
               searchField.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {

            if (searchField.getText().isEmpty()) {
                searchField.setText("Tìm kiếm...");
                searchField.setForeground(Color.GRAY);
            }
        }
    });
        searchField.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        searchField.setToolTipText(
            "Tìm theo tên đăng nhập hoặc họ tên"
        );

        // Điều kiện so sánh
        String[] conditions = {
            "=", "<", ">"
        };

        JComboBox<String> conditionFilter =
            new JComboBox<>(conditions);

        // Ô nhập số lượng bạn bè
        JTextField friendCountField = new JTextField(6);

        friendCountField.setToolTipText(
            "Enter number of direct friends"
        );

        // Tùy chọn sắp xếp
        String[] sortOptions = {
            "Tên",
            "Ngày tạo"
        };

        JComboBox<String> sortFilter =
            new JComboBox<>(sortOptions);

        // Thêm các thành phần vào thanh công cụ
        panel.add(searchField);

        panel.add(new JLabel("Bạn bè trực tiếp:"));

        panel.add(conditionFilter);

        panel.add(friendCountField);

        panel.add(new JLabel("Sắp xếp theo:"));

        panel.add(sortFilter);

        return panel;
    }

    // Tạo bảng thống kê bạn bè
    private JPanel createFriendTable() {

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

        JLabel title = new JLabel(
            "Thống kê quan hệ bạn bè"
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        title.setForeground(TEXT_COLOR);

        panel.add(title, BorderLayout.NORTH);

        // Các cột của bảng
        String[] columns = {
            "Tên đăng nhập",
            "Họ và tên",
            "Bạn bè trực tiếp",
            "Bạn của bạn bè"
        };

        // Dữ liệu mẫu
        Object[][] data = {
            {"john123", "John Smith", 25, 120},
            {"mary456", "Mary Johnson", 18, 95},
            {"alex789", "Alex Brown", 32, 156},
            {"david101", "David Wilson", 12, 68},
            {"sarah202", "Sarah Davis", 40, 210},
            {"peter303", "Peter Wilson", 15, 82},
            {"lisa404", "Lisa Taylor", 28, 135}
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

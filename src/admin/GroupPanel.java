package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GroupPanel extends JPanel {

    private final Color BACKGROUND_COLOR =
        new Color(245, 247, 250);

    private final Color TEXT_COLOR =
        new Color(30, 45, 65);

    public GroupPanel() {

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
            createGroupTable(),
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

        JLabel title = new JLabel("Nhóm trò chuyện");

        title.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        title.setForeground(TEXT_COLOR);

        JLabel description = new JLabel(
            "Quản lý nhóm trò chuyện và thành viên"
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

    // Tạo thanh công cụ tìm kiếm và sắp xếp
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
        JTextField searchField = new JTextField(22);

        searchField.setFont(
            new Font("Arial", Font.PLAIN, 14)
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

        // Các tùy chọn sắp xếp
        String[] sortOptions = {
            "Tên",
            "Ngày tạo"
        };

        JComboBox<String> sortFilter =
            new JComboBox<>(sortOptions);

        sortFilter.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        panel.add(searchField);

        panel.add(new JLabel("Sắp xếp theo:"));

        panel.add(sortFilter);

        return panel;
    }

    // Tạo bảng nhóm chat
    private JPanel createGroupTable() {

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

        JLabel title = new JLabel("Danh sách nhóm");

        title.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        title.setForeground(TEXT_COLOR);

        panel.add(title, BorderLayout.NORTH);

        // Các cột của bảng
        String[] columns = {
            "Tên nhóm",
            "Ngày tạo",
            "Thành viên",
            "Quản trị viên"
        };

        // Dữ liệu nhóm chat mẫu
        Object[][] data = {
            {"Cộng đồng trò chơi", "15/10/2026", 25, 3},
            {"Nhóm Tin học đại học", "12/10/2026", 18, 2},
            {"Thảo luận Anime", "08/10/2026", 32, 4},
            {"Nhóm dự án", "01/10/2026", 12, 2},
            {"Bạn bè mãi mãi", "25/09/2026", 15, 2},
            {"Nhóm học tập", "20/09/2026", 10, 1},
            {"Người yêu phim", "15/09/2026", 28, 3}
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

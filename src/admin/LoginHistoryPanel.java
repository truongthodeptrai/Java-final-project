package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LoginHistoryPanel extends JPanel {

    private final Color BACKGROUND_COLOR =
        new Color(245, 247, 250);

    private final Color TEXT_COLOR =
        new Color(30, 45, 65);

    public LoginHistoryPanel() {

        setLayout(new BorderLayout(0, 25));

        setBackground(BACKGROUND_COLOR);

        setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );

        // Tiêu đề trang
        add(createHeading(), BorderLayout.NORTH);

        // Bảng lịch sử đăng nhập
        add(createLoginTable(), BorderLayout.CENTER);
    }

    // Tạo phần tiêu đề trang
    private JPanel createHeading() {

        JPanel panel = new JPanel();

        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("Login History");

        title.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        title.setForeground(TEXT_COLOR);

        JLabel description = new JLabel(
            "View the login history of registered users"
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

    // Tạo bảng lịch sử đăng nhập
    private JPanel createLoginTable() {

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

        JLabel title = new JLabel("Login Records");

        title.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        title.setForeground(TEXT_COLOR);

        panel.add(title, BorderLayout.NORTH);

        // Các cột của bảng
        String[] columns = {
            "Login Time",
            "Username",
            "Full Name"
        };

        // Dữ liệu lịch sử đăng nhập mẫu
        Object[][] data = {
            {"21/11/2026 08:30", "john123", "John Smith"},
            {"21/11/2026 08:15", "mary456", "Mary Johnson"},
            {"21/11/2026 07:45", "alex789", "Alex Brown"},
            {"20/11/2026 22:30", "david101", "David Wilson"},
            {"20/11/2026 21:55", "sarah202", "Sarah Davis"},
            {"20/11/2026 20:40", "john123", "John Smith"},
            {"20/11/2026 19:15", "mary456", "Mary Johnson"}
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

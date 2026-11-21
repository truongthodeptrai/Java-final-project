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

        // Page heading
        add(createHeading(), BorderLayout.NORTH);

        // Main content
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

    // Create page heading
    private JPanel createHeading() {

        JPanel panel = new JPanel();

        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("Friend Statistics");

        title.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        title.setForeground(TEXT_COLOR);

        JLabel description = new JLabel(
            "View users and their friendship statistics"
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

    // Create search, filter and sorting toolbar
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

        // Search field
        JTextField searchField = new JTextField(18);

        searchField.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        // Placeholder text
        searchField.setText("Search...");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {

        @Override
        public void focusGained(java.awt.event.FocusEvent e) {

            if (searchField.getText().equals("Search...")) {
               searchField.setText("");
               searchField.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {

            if (searchField.getText().isEmpty()) {
                searchField.setText("Search...");
                searchField.setForeground(Color.GRAY);
            }
        }
    });
        searchField.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        searchField.setToolTipText(
            "Search by username or full name"
        );

        // Comparison condition
        String[] conditions = {
            "=", "<", ">"
        };

        JComboBox<String> conditionFilter =
            new JComboBox<>(conditions);

        // Friend count input
        JTextField friendCountField = new JTextField(6);

        friendCountField.setToolTipText(
            "Enter number of direct friends"
        );

        // Sort options
        String[] sortOptions = {
            "Name",
            "Created Date"
        };

        JComboBox<String> sortFilter =
            new JComboBox<>(sortOptions);

        // Add components
        panel.add(searchField);

        panel.add(new JLabel("Direct Friends:"));

        panel.add(conditionFilter);

        panel.add(friendCountField);

        panel.add(new JLabel("Sort by:"));

        panel.add(sortFilter);

        return panel;
    }

    // Create friendship statistics table
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
            "User Friendship Statistics"
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        title.setForeground(TEXT_COLOR);

        panel.add(title, BorderLayout.NORTH);

        // Table columns
        String[] columns = {
            "Username",
            "Full Name",
            "Direct Friends",
            "Friends of Friends"
        };

        // Sample data
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

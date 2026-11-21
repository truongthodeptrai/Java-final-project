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
            createGroupTable(),
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

        JLabel title = new JLabel("Chat Groups");

        title.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        title.setForeground(TEXT_COLOR);

        JLabel description = new JLabel(
            "Manage chat groups and their members"
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

    // Create search and sorting toolbar
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
        JTextField searchField = new JTextField(22);

        searchField.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        searchField.setText("Search...");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(
            new java.awt.event.FocusAdapter() {

                @Override
                public void focusGained(
                    java.awt.event.FocusEvent e
                ) {

                    if (searchField.getText().equals("Search...")) {
                        searchField.setText("");
                        searchField.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(
                    java.awt.event.FocusEvent e
                ) {

                    if (searchField.getText().isEmpty()) {
                        searchField.setText("Search...");
                        searchField.setForeground(Color.GRAY);
                    }
                }
            }
        );

        // Sorting options
        String[] sortOptions = {
            "Name",
            "Created Date"
        };

        JComboBox<String> sortFilter =
            new JComboBox<>(sortOptions);

        sortFilter.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        panel.add(searchField);

        panel.add(new JLabel("Sort by:"));

        panel.add(sortFilter);

        return panel;
    }

    // Create chat group table
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

        JLabel title = new JLabel("Group List");

        title.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        title.setForeground(TEXT_COLOR);

        panel.add(title, BorderLayout.NORTH);

        // Table columns
        String[] columns = {
            "Group Name",
            "Created Date",
            "Members",
            "Administrators"
        };

        // Sample group data
        Object[][] data = {
            {"Gaming Community", "15/10/2026", 25, 3},
            {"University CS", "12/10/2026", 18, 2},
            {"Anime Discussion", "08/10/2026", 32, 4},
            {"Project Team", "01/10/2026", 12, 2},
            {"Friends Forever", "25/09/2026", 15, 2},
            {"Study Group", "20/09/2026", 10, 1},
            {"Movie Lovers", "15/09/2026", 28, 3}
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

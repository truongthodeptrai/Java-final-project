
package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class ActivityPanel extends JPanel {

    private final Color BACKGROUND_COLOR =
        new Color(245, 247, 250);

    private final Color TEXT_COLOR =
        new Color(30, 45, 65);

    public ActivityPanel() {

        setLayout(new BorderLayout(0, 25));
        setBackground(BACKGROUND_COLOR);

        setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );

        // Heading
        add(createHeading(), BorderLayout.NORTH);

        // Main content
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(BACKGROUND_COLOR);

        content.add(createToolbar(), BorderLayout.NORTH);
        content.add(createActivityTable(), BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    private JPanel createHeading() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("User Activity");

        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);

        JLabel description = new JLabel(
            "Monitor user activity within a selected date range"
        );

        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setForeground(Color.GRAY);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(description);

        return panel;
    }

    private JPanel createToolbar() {

        JPanel toolbar = new JPanel(new FlowLayout(
            FlowLayout.LEFT, 10, 10
        ));

        toolbar.setBackground(Color.WHITE);

        toolbar.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        // Start date
        JLabel fromLabel = new JLabel("From:");

        JSpinner fromDate = new JSpinner(
            new SpinnerDateModel(new Date(), null, null,
                java.util.Calendar.DAY_OF_MONTH)
        );

        fromDate.setEditor(
            new JSpinner.DateEditor(fromDate, "dd/MM/yyyy")
        );

        // End date
        JLabel toLabel = new JLabel("To:");

        JSpinner toDate = new JSpinner(
            new SpinnerDateModel(new Date(), null, null,
                java.util.Calendar.DAY_OF_MONTH)
        );

        toDate.setEditor(
            new JSpinner.DateEditor(toDate, "dd/MM/yyyy")
        );

        // Search field
        JTextField searchField = new JTextField(15);

        searchField.setText("Search...");

        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(
            new java.awt.event.FocusAdapter() {

                public void focusGained(
                    java.awt.event.FocusEvent e
                ) {
                    if (searchField.getText().equals("Search...")) {
                        searchField.setText("");
                        searchField.setForeground(Color.BLACK);
                    }
                }

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

        // Activity comparison
        JComboBox<String> comparison = new JComboBox<>(
            new String[]{"=", "<", ">"}
        );

        JTextField activityCount = new JTextField(5);

        // Sort option
        JComboBox<String> sort = new JComboBox<>(
            new String[]{"Name", "Created Date"}
        );

        toolbar.add(fromLabel);
        toolbar.add(fromDate);

        toolbar.add(toLabel);
        toolbar.add(toDate);

        toolbar.add(searchField);
        toolbar.add(comparison);
        toolbar.add(activityCount);

        toolbar.add(new JLabel("Sort by:"));
        toolbar.add(sort);

        return toolbar;
    }

    private JScrollPane createActivityTable() {

        String[] columns = {
            "Username",
            "Full Name",
            "App Opens",
            "People Chatted With",
            "Groups Chatted In"
        };

        Object[][] data = {
            {"john01", "John Smith", 25, 12, 4},
            {"mary02", "Mary Johnson", 18, 9, 3},
            {"alex03", "Alex Brown", 32, 20, 8},
            {"sarah04", "Sarah Wilson", 15, 7, 2},
            {"david05", "David Miller", 40, 25, 10},
            {"emma06", "Emma Davis", 22, 11, 5},
            {"michael07", "Michael Taylor", 28, 16, 6}
        };

        DefaultTableModel model = new DefaultTableModel(
            data, columns
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

        table.setRowHeight(42);

        table.setFont(new Font("Arial", Font.PLAIN, 13));

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        table.getTableHeader().setBackground(
            new Color(235, 240, 247)
        );

        table.getTableHeader().setForeground(TEXT_COLOR);

        table.setSelectionBackground(
            new Color(220, 235, 250)
        );

        table.setShowVerticalLines(false);

        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(
            BorderFactory.createLineBorder(
                new Color(225, 230, 238)
            )
        );

        return scrollPane;
    }
}

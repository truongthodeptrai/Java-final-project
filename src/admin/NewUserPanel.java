package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class NewUserPanel extends JPanel {

    private final Color BACKGROUND_COLOR =
        new Color(245, 247, 250);

    private final Color TEXT_COLOR =
        new Color(30, 45, 65);

    public NewUserPanel() {

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
            createUserTable(),
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

        JLabel title = new JLabel("New Registrations");

        title.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        title.setForeground(TEXT_COLOR);

        JLabel description = new JLabel(
            "View newly registered users within a selected period"
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

    // Create date filters, search and sorting toolbar
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

        // Starting date
        JSpinner fromDate = new JSpinner(
            new SpinnerDateModel(
                new Date(),
                null,
                null,
                java.util.Calendar.DAY_OF_MONTH
            )
        );

        fromDate.setEditor(
            new JSpinner.DateEditor(fromDate, "dd/MM/yyyy")
        );

        // Ending date
        JSpinner toDate = new JSpinner(
            new SpinnerDateModel(
                new Date(),
                null,
                null,
                java.util.Calendar.DAY_OF_MONTH
            )
        );

        toDate.setEditor(
            new JSpinner.DateEditor(toDate, "dd/MM/yyyy")
        );

        // Search field
        JTextField searchField = new JTextField(15);

        searchField.setFont(
            new Font("Arial", Font.PLAIN, 13)
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
            "Registration Date"
        };

        JComboBox<String> sortFilter =
            new JComboBox<>(sortOptions);

        sortFilter.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        // Add components
        panel.add(new JLabel("From:"));
        panel.add(fromDate);

        panel.add(new JLabel("To:"));
        panel.add(toDate);

        panel.add(searchField);

        panel.add(new JLabel("Sort by:"));
        panel.add(sortFilter);

        return panel;
    }

    // Create newly registered users table
    private JPanel createUserTable() {

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

        JLabel title = new JLabel("Registered Users");

        title.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        title.setForeground(TEXT_COLOR);

        panel.add(title, BorderLayout.NORTH);

        // Table columns
        String[] columns = {
            "Username",
            "Full Name",
            "Email",
            "Registration Date"
        };

        // Sample registration data
        Object[][] data = {
            {"john123", "John Smith", "john@email.com",
                "21/11/2026"},

            {"mary456", "Mary Johnson", "mary@email.com",
                "20/11/2026"},

            {"alex789", "Alex Brown", "alex@email.com",
                "18/11/2026"},

            {"david101", "David Wilson", "david@email.com",
                "15/11/2026"},

            {"sarah202", "Sarah Davis", "sarah@email.com",
                "12/11/2026"},

            {"peter303", "Peter Wilson", "peter@email.com",
                "10/11/2026"},

            {"lisa404", "Lisa Taylor", "lisa@email.com",
                "05/11/2026"}
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

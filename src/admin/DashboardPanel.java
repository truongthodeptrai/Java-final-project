package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private final Color BACKGROUND_COLOR =
        new Color(245, 247, 250);

    private final Color CARD_COLOR = Color.WHITE;

    private final Color TEXT_COLOR =
        new Color(30, 45, 65);

    private final Color BLUE_COLOR =
        new Color(45, 105, 190);

    public DashboardPanel() {

        setLayout(new BorderLayout(0, 25));
        setBackground(BACKGROUND_COLOR);

        setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );

        // Dashboard heading
        JPanel heading = createHeading();

        add(heading, BorderLayout.NORTH);

        // Dashboard main content
        JPanel mainContent = new JPanel();

        mainContent.setLayout(
            new BoxLayout(mainContent, BoxLayout.Y_AXIS)
        );

        mainContent.setBackground(BACKGROUND_COLOR);

        // Statistic cards
        JPanel statistics = createStatistics();

        mainContent.add(statistics);

        mainContent.add(Box.createVerticalStrut(30));

        // Recent login table
        JPanel loginSection = createRecentLogin();

        mainContent.add(loginSection);

        add(mainContent, BorderLayout.CENTER);
    }

    // Create dashboard heading
    private JPanel createHeading() {

        JPanel panel = new JPanel();

        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("Dashboard");

        title.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        title.setForeground(TEXT_COLOR);

        JLabel description = new JLabel(
            "Overview of your ChatAdmin system"
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

    // Create statistic cards
    private JPanel createStatistics() {

        JPanel panel = new JPanel(
            new GridLayout(1, 4, 20, 0)
        );

        panel.setBackground(BACKGROUND_COLOR);

        panel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 145)
        );

        panel.add(createStatisticCard(
            "Total Users", "2,450", "Registered accounts"
        ));

        panel.add(createStatisticCard(
            "New Registrations", "128", "This month"
        ));

        panel.add(createStatisticCard(
            "Chat Groups", "86", "Total groups"
        ));

        panel.add(createStatisticCard(
            "Active Users", "1,820", "This month"
        ));

        return panel;
    }

    // Create one statistic card
    private JPanel createStatisticCard(
        String title,
        String value,
        String description
    ) {

        JPanel card = new JPanel();

        card.setLayout(
            new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBackground(CARD_COLOR);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(225, 230, 238)
                ),
                BorderFactory.createEmptyBorder(
                    20, 20, 20, 20
                )
            )
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);

        valueLabel.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        valueLabel.setForeground(BLUE_COLOR);

        JLabel descriptionLabel = new JLabel(description);

        descriptionLabel.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );

        descriptionLabel.setForeground(Color.GRAY);

        card.add(titleLabel);

        card.add(Box.createVerticalStrut(12));

        card.add(valueLabel);

        card.add(Box.createVerticalStrut(8));

        card.add(descriptionLabel);

        return card;
    }

    // Create recent login section
    private JPanel createRecentLogin() {

        JPanel panel = new JPanel(new BorderLayout(0, 15));

        panel.setBackground(CARD_COLOR);

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

        JLabel title = new JLabel("Recent Login Activity");

        title.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        title.setForeground(TEXT_COLOR);

        panel.add(title, BorderLayout.NORTH);

        // Table columns
        String[] columns = {
            "Username",
            "Full Name",
            "Login Time"
        };

        // Sample table data
        Object[][] data = {
            {"john123", "John Smith", "20:30"},
            {"mary456", "Mary Johnson", "20:15"},
            {"alex789", "Alex Brown", "19:45"},
            {"david101", "David Wilson", "19:20"},
            {"sarah202", "Sarah Davis", "18:55"}
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
        table.setFillsViewportHeight(true);

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

        table.setGridColor(
            new Color(235, 238, 243)
        );

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(
            BorderFactory.createEmptyBorder()
        );

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}

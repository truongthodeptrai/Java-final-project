
package admin;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Sidebar extends JPanel {

    private final Color SIDEBAR_COLOR = new Color(23, 37, 61);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color SELECTED_COLOR = new Color(45, 70, 105);

    private JButton selectedButton;

    public Sidebar(Consumer<String> navigation) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SIDEBAR_COLOR);
        setPreferredSize(new Dimension(240, 0));

        // Application name
        JLabel logo = new JLabel("  CHATADMIN");
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setForeground(TEXT_COLOR);
        logo.setBorder(
            BorderFactory.createEmptyBorder(25, 10, 30, 10)
        );

        add(logo);

        // Menu section
        addMenuItem("Dashboard", "dashboard", navigation);

        addSectionTitle("USER MANAGEMENT");

        addMenuItem("Users", "users", navigation);
        addMenuItem("Login History", "login", navigation);
        addMenuItem("Friend Statistics", "friends", navigation);

        addSectionTitle("GROUP MANAGEMENT");

        addMenuItem("Chat Groups", "groups", navigation);
        addMenuItem("Spam Reports", "spam", navigation);

        addSectionTitle("STATISTICS");

        addMenuItem("New Registrations", "registrations", navigation);
        addMenuItem("Registration Chart", "registration_chart", navigation);
        addMenuItem("User Activity", "activity", navigation);
        addMenuItem("Activity Chart", "activity_chart", navigation);

        add(Box.createVerticalGlue());

        JLabel adminLabel = new JLabel("  Administrator");
        adminLabel.setForeground(Color.LIGHT_GRAY);
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

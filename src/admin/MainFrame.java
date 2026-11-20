
package admin;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private final Color BACKGROUND_COLOR =
        new Color(245, 247, 250);

    public MainFrame() {

        setTitle("ChatAdmin - Administration System");

        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 700));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Create sidebar
        Sidebar sidebar = new Sidebar(this::showPage);

        add(sidebar, BorderLayout.WEST);

        // Create main content area
        JPanel mainArea = new JPanel(new BorderLayout());

        mainArea.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel header = createHeader();

        mainArea.add(header, BorderLayout.NORTH);

        // Content pages
        cardLayout = new CardLayout();

        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND_COLOR);

        addPages();

        mainArea.add(contentPanel, BorderLayout.CENTER);

        add(mainArea, BorderLayout.CENTER);

        // Default page
        cardLayout.show(contentPanel, "dashboard");
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(Color.WHITE);

        header.setBorder(
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JLabel title = new JLabel("Administration Panel");

        title.setFont(
            new Font("Arial", Font.BOLD, 20)
        );

        title.setForeground(new Color(30, 45, 65));

        JLabel admin = new JLabel("Admin Account");

        admin.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        admin.setForeground(new Color(90, 100, 115));

        header.add(title, BorderLayout.WEST);
        header.add(admin, BorderLayout.EAST);

        return header;
    }

    private void addPages() {

        addPage("dashboard", "Dashboard",
            "Welcome to ChatAdmin Administration System");

        addPage("users", "User Management",
            "Manage user accounts and personal information");

        addPage("login", "Login History",
            "View user login history");

        addPage("friends", "Friend Statistics",
            "View users and their number of friends");

        addPage("groups", "Chat Groups",
            "Manage chat groups and their members");

        addPage("spam", "Spam Reports",
            "Review spam reports and manage reported accounts");

        addPage("registrations", "New Registrations",
            "View newly registered users");

        addPage("registration_chart", "Registration Statistics",
            "View monthly user registrations");

        addPage("activity", "User Activity",
            "View user activity statistics");

        addPage("activity_chart", "Activity Statistics",
            "View monthly active user statistics");
    }

    private void addPage(
        String key,
        String title,
        String description
    ) {

        JPanel page = new JPanel(new BorderLayout());

        page.setBackground(BACKGROUND_COLOR);

        page.setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(BACKGROUND_COLOR);

        JLabel pageTitle = new JLabel(title);

        pageTitle.setFont(
            new Font("Arial", Font.BOLD, 26)
        );

        pageTitle.setForeground(new Color(30, 45, 65));

        JLabel pageDescription = new JLabel(description);

        pageDescription.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        pageDescription.setForeground(Color.GRAY);

        heading.add(pageTitle);
        heading.add(Box.createVerticalStrut(8));
        heading.add(pageDescription);

        page.add(heading, BorderLayout.NORTH);

        // Placeholder content
        JPanel placeholder = new JPanel(new GridBagLayout());

        placeholder.setBackground(Color.WHITE);

        JLabel message = new JLabel(
            "This page is under development."
        );

        message.setFont(
            new Font("Arial", Font.PLAIN, 16)
        );

        message.setForeground(Color.GRAY);

        placeholder.add(message);

        page.add(placeholder, BorderLayout.CENTER);

        contentPanel.add(page, key);
    }

    private void showPage(String page) {

        cardLayout.show(contentPanel, page);
    }
}

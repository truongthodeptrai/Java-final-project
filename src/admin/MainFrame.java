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

        // Dashboard screens
        contentPanel.add(
        new DashboardPanel(),
        "dashboard"
        );

        
        contentPanel.add(
        new UserPanel(),
        "users"
        );

        contentPanel.add(
        new LoginHistoryPanel(),
        "login"
        );

        contentPanel.add(
        new FriendStatisticsPanel(),
        "friends"
        );

        contentPanel.add(
        new GroupPanel(),
        "groups"
        );

        contentPanel.add(
        new SpamReportPanel(),
        "spam"
        );

        contentPanel.add(
        new NewUserPanel(),
        "registrations"
        );

        contentPanel.add(
        new RegistrationChartPanel(),
        "registration_chart"
        );

        contentPanel.add(
        new ActivityPanel(),
        "activity"
        );

        contentPanel.add(
        new ActivityChartPanel(),
        "activity_chart"
        );
        
    }

    private void showPage(String page) {

        cardLayout.show(contentPanel, page);
    }
}

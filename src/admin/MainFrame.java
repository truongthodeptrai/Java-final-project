package admin;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private final Color BACKGROUND_COLOR =
        new Color(245, 247, 250);

    public MainFrame() {

        setTitle("ChatAdmin - Hệ thống quản trị");

        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 700));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Tạo thanh điều hướng bên
        Sidebar sidebar = new Sidebar(this::showPage);

        add(sidebar, BorderLayout.WEST);

        // Tạo khu vực nội dung chính
        JPanel mainArea = new JPanel(new BorderLayout());

        mainArea.setBackground(BACKGROUND_COLOR);

        // Phần đầu trang
        JPanel header = createHeader();

        mainArea.add(header, BorderLayout.NORTH);

        // Các trang nội dung
        cardLayout = new CardLayout();

        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND_COLOR);

        addPages();

        mainArea.add(contentPanel, BorderLayout.CENTER);

        add(mainArea, BorderLayout.CENTER);

        // Trang hiển thị mặc định
        cardLayout.show(contentPanel, "dashboard");
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(Color.WHITE);

        header.setBorder(
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JLabel title = new JLabel("Trang quản trị");

        title.setFont(
            new Font("Arial", Font.BOLD, 20)
        );

        title.setForeground(new Color(30, 45, 65));

        header.add(title, BorderLayout.WEST);

        return header;

    }

    
    private void addPages() {

        // Đăng ký các màn hình trong trang tổng quan
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

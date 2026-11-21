package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Màn hình Đăng nhập (LoginFrame)
 * Màn hình mở đầu có form đăng nhập trực quan, căn giữa màn hình,
 * kết nối đồng bộ với RegisterDialog, ForgotPasswordDialog, ProfileDialog và ChangePasswordDialog.
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkRemember;
    private JCheckBox chkShowPassword;
    private JLabel lblError;
    private JButton btnLogin;
    private JButton btnRegister;
    private JButton btnForgotPassword;

    // Các nút truy cập nhanh sau khi đăng nhập
    private JButton btnDemoProfile;
    private JButton btnDemoChangePass;

    public LoginFrame() {
        super("ChatConnect - Đăng nhập hệ thống");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 620);
        setMinimumSize(new Dimension(880, 580));
        setLayout(new BorderLayout());

        // Áp dụng theme và font Segoe UI / Arial mượt mà
        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // Chia bố cục 2 cột: Cột trái (Hero Brand) & Cột phải (Form Đăng nhập)
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel cardWrapper = new UserUITheme.RoundedPanel(16, UserUITheme.COLOR_CARD_BACKGROUND, UserUITheme.COLOR_BORDER);
        cardWrapper.setLayout(new BorderLayout());
        cardWrapper.setPreferredSize(new Dimension(910, 560));

        // 1. Cột trái: Hero Branding Panel (Tông xanh than đậm UserUITheme.COLOR_SIDEBAR)
        JPanel leftBrandPanel = createBrandPanel();
        cardWrapper.add(leftBrandPanel, BorderLayout.WEST);

        // 2. Cột phải: Form Đăng nhập & Điều hướng các Dialog
        JPanel rightFormPanel = createFormPanel();
        cardWrapper.add(rightFormPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainContainer.add(cardWrapper, gbc);

        add(mainContainer, BorderLayout.CENTER);

        // Căn giữa màn hình theo đặc tả
        pack();
        setSize(960, 620);
        setLocationRelativeTo(null);
    }

    /**
     * Tạo Panel thương hiệu bên trái (Hero Showcase)
     */
    private JPanel createBrandPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UserUITheme.enableAntialiasing(g2);

                // Nền gradient xanh than hiện đại
                GradientPaint gp = new GradientPaint(
                        0, 0, UserUITheme.COLOR_SIDEBAR,
                        0, getHeight(), new Color(15, 25, 45)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Vẽ hoa văn trang trí nhẹ
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fillOval(-50, -50, 240, 240);
                g2.fillOval(getWidth() - 120, getHeight() - 140, 220, 220);

                g2.dispose();
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(360, 560));
        panel.setBorder(new EmptyBorder(38, 36, 30, 36));

        // Logo Icon
        JPanel logoBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UserUITheme.enableAntialiasing(g2);
                g2.setColor(UserUITheme.COLOR_PRIMARY);
                g2.fillRoundRect(0, 0, 56, 56, 16, 16);

                // Vẽ biểu tượng bong bóng chat
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(13, 14, 30, 22, 10, 10);
                int[] xPoints = {17, 24, 25};
                int[] yPoints = {36, 36, 43};
                g2.fillPolygon(xPoints, yPoints, 3);

                // Điểm sáng trong bong bóng
                g2.fillOval(20, 22, 4, 4);
                g2.fillOval(26, 22, 4, 4);
                g2.fillOval(32, 22, 4, 4);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(56, 56);
            }
        };
        logoBadge.setOpaque(false);
        logoBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(logoBadge);
        panel.add(Box.createVerticalStrut(18));

        // Tên ứng dụng
        JLabel appTitle = new JLabel("ChatConnect");
        appTitle.setFont(UserUITheme.getFont(Font.BOLD, 26f));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(appTitle);
        panel.add(Box.createVerticalStrut(6));

        // Slogan
        JLabel appSlogan = new JLabel("Hệ thống kết nối & trò chuyện trực tuyến");
        appSlogan.setFont(UserUITheme.FONT_SMALL);
        appSlogan.setForeground(new Color(185, 205, 230));
        appSlogan.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(appSlogan);
        panel.add(Box.createVerticalStrut(28));

        // Danh sách tính năng nổi bật vẽ vector bullet đảm bảo hiển thị hoàn hảo
        panel.add(createFeatureItem("Nhắn tin thời gian thực mượt mà"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(createFeatureItem("Quản lý nhóm trò chuyện & bạn bè"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(createFeatureItem("Bảo mật tài khoản & mã hóa tin nhắn"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(createFeatureItem("Tích hợp trợ lý thông minh gợi ý chat"));

        panel.add(Box.createVerticalGlue());

        // Footer ghi chú phiên bản
        JLabel versionLabel = new JLabel("Đồ án Lập trình ứng dụng Java • BM. CNPM");
        versionLabel.setFont(UserUITheme.FONT_TIMESTAMP);
        versionLabel.setForeground(new Color(140, 160, 185));
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(versionLabel);

        return panel;
    }

    private JPanel createFeatureItem(String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        item.setOpaque(false);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Vector blue dot
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UserUITheme.enableAntialiasing(g2);
                g2.setColor(new Color(90, 165, 255));
                g2.fillOval(1, 5, 8, 8);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(12, 18);
            }
        };
        dot.setOpaque(false);

        JLabel lblText = new JLabel(text);
        lblText.setFont(UserUITheme.FONT_BODY);
        lblText.setForeground(new Color(225, 235, 248));

        item.add(dot);
        item.add(lblText);
        return item;
    }

    /**
     * Tạo Panel Form Đăng nhập bên phải
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 42, 20, 42));

        // Form content
        JPanel formCenter = new JPanel();
        formCenter.setLayout(new BoxLayout(formCenter, BoxLayout.Y_AXIS));
        formCenter.setOpaque(false);

        // Tiêu đề form
        JLabel title = new JLabel("Đăng nhập");
        title.setFont(UserUITheme.FONT_TITLE_LARGE);
        title.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCenter.add(title);
        formCenter.add(Box.createVerticalStrut(4));

        JLabel subtitle = new JLabel("Chào mừng bạn quay trở lại! Vui lòng nhập thông tin.");
        subtitle.setFont(UserUITheme.FONT_BODY);
        subtitle.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCenter.add(subtitle);
        formCenter.add(Box.createVerticalStrut(20));

        // 1. Trường Tên đăng nhập
        JLabel lblUser = new JLabel("Tên đăng nhập hoặc Email *");
        lblUser.setFont(UserUITheme.FONT_BODY_BOLD);
        lblUser.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCenter.add(lblUser);
        formCenter.add(Box.createVerticalStrut(6));

        txtUsername = UserUITheme.createTextField("Nhập tên đăng nhập hoặc email...");
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCenter.add(txtUsername);
        formCenter.add(Box.createVerticalStrut(14));

        // 2. Trường Mật khẩu
        JLabel lblPass = new JLabel("Mật khẩu *");
        lblPass.setFont(UserUITheme.FONT_BODY_BOLD);
        lblPass.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCenter.add(lblPass);
        formCenter.add(Box.createVerticalStrut(6));

        txtPassword = UserUITheme.createPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCenter.add(txtPassword);
        formCenter.add(Box.createVerticalStrut(8));

        // Dòng tùy chọn: Hiển thị mật khẩu, Ghi nhớ đăng nhập & Quên mật khẩu
        JPanel optionsRow = new JPanel(new BorderLayout());
        optionsRow.setOpaque(false);
        optionsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JPanel leftOpts = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftOpts.setOpaque(false);

        chkShowPassword = new JCheckBox("Hiện mật khẩu");
        chkShowPassword.setFont(UserUITheme.FONT_SMALL);
        chkShowPassword.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '•');
        });

        chkRemember = new JCheckBox("Ghi nhớ");
        chkRemember.setFont(UserUITheme.FONT_SMALL);
        chkRemember.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        chkRemember.setOpaque(false);
        chkRemember.setFocusPainted(false);

        leftOpts.add(chkShowPassword);
        leftOpts.add(chkRemember);
        optionsRow.add(leftOpts, BorderLayout.WEST);

        // Nút Quên mật khẩu -> Mở ForgotPasswordDialog
        btnForgotPassword = new JButton("Quên mật khẩu?");
        btnForgotPassword.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnForgotPassword.setForeground(UserUITheme.COLOR_PRIMARY);
        btnForgotPassword.setBorderPainted(false);
        btnForgotPassword.setContentAreaFilled(false);
        btnForgotPassword.setFocusPainted(false);
        btnForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnForgotPassword.addActionListener(e -> openForgotPasswordDialog());
        optionsRow.add(btnForgotPassword, BorderLayout.EAST);

        formCenter.add(optionsRow);
        formCenter.add(Box.createVerticalStrut(6));

        // Nhãn thông báo lỗi
        lblError = new JLabel(" ");
        lblError.setFont(UserUITheme.FONT_SMALL_BOLD);
        lblError.setForeground(UserUITheme.COLOR_DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCenter.add(lblError);
        formCenter.add(Box.createVerticalStrut(6));

        // 3. Nút Đăng nhập
        btnLogin = UserUITheme.createPrimaryButton("Đăng nhập");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> processLogin());
        formCenter.add(btnLogin);
        formCenter.add(Box.createVerticalStrut(14));

        // Phím Enter để đăng nhập
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);

        // Dòng chuyển sang Đăng ký
        JPanel regRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regRow.setOpaque(false);
        regRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        regRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblNoAcc = new JLabel("Chưa có tài khoản?");
        lblNoAcc.setFont(UserUITheme.FONT_BODY);
        lblNoAcc.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);

        btnRegister = new JButton("Đăng ký ngay");
        btnRegister.setFont(UserUITheme.FONT_BODY_BOLD);
        btnRegister.setForeground(UserUITheme.COLOR_PRIMARY);
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> openRegisterDialog());

        regRow.add(lblNoAcc);
        regRow.add(btnRegister);
        formCenter.add(regRow);

        panel.add(formCenter, BorderLayout.CENTER);

        // Thanh công cụ hỗ trợ trải nghiệm sau khi đăng nhập (ProfileDialog & ChangePasswordDialog)
        JPanel bottomDemoBar = createSupportDialogsBar();
        panel.add(bottomDemoBar, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Thanh truy cập nhanh các Dialog hỗ trợ sau đăng nhập
     */
    private JPanel createSupportDialogsBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER),
                new EmptyBorder(12, 0, 0, 0)
        ));

        JLabel lblHelp = new JLabel("Dialog hỗ trợ sau đăng nhập:");
        lblHelp.setFont(UserUITheme.FONT_SMALL);
        lblHelp.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);

        btnDemoProfile = UserUITheme.createSecondaryButton("Hồ sơ cá nhân");
        btnDemoProfile.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnDemoProfile.setBorder(new CompoundBorder(
                new UserUITheme.RoundedLineBorder(UserUITheme.COLOR_BORDER, 6, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnDemoProfile.addActionListener(e -> openProfileDialog());

        btnDemoChangePass = UserUITheme.createSecondaryButton("Đổi mật khẩu");
        btnDemoChangePass.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnDemoChangePass.setBorder(new CompoundBorder(
                new UserUITheme.RoundedLineBorder(UserUITheme.COLOR_BORDER, 6, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnDemoChangePass.addActionListener(e -> openChangePasswordDialog());

        btnGroup.add(btnDemoProfile);
        btnGroup.add(btnDemoChangePass);

        bar.add(lblHelp, BorderLayout.WEST);
        bar.add(btnGroup, BorderLayout.EAST);
        return bar;
    }

    /**
     * Mở RegisterDialog
     */
    private void openRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.setVisible(true);

        // Nếu vừa đăng ký thành công, tự động điền username vào ô đăng nhập
        String regUser = dialog.getRegisteredUsername();
        if (regUser != null && !regUser.isEmpty()) {
            txtUsername.setText(regUser);
            txtUsername.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            txtPassword.requestFocus();
            lblError.setForeground(UserUITheme.COLOR_STATUS_ONLINE);
            lblError.setText("✓ Tài khoản " + regUser + " đã sẵn sàng. Vui lòng nhập mật khẩu để đăng nhập!");
        }
    }

    /**
     * Mở ForgotPasswordDialog
     */
    private void openForgotPasswordDialog() {
        String currentInput = txtUsername.getText().trim();
        String placeholder = "Nhập tên đăng nhập hoặc email...";
        if (currentInput.equals(placeholder)) {
            currentInput = "";
        }
        ForgotPasswordDialog dialog = new ForgotPasswordDialog(this, currentInput);
        dialog.setVisible(true);
    }

    /**
     * Mở ProfileDialog (Cập nhật thông tin tài khoản)
     */
    public void openProfileDialog() {
        String username = getUsernameInput();
        if (username.isEmpty()) {
            username = "nguyen_van_a";
        }
        ProfileDialog dialog = new ProfileDialog(this, username, "Nguyễn Văn An", username + "@gmail.com",
                "227 Nguyễn Văn Cừ, Quận 5, TP.HCM", 15, 8, 2000, "Nam");
        dialog.setVisible(true);
    }

    /**
     * Mở ChangePasswordDialog (Đổi mật khẩu)
     */
    public void openChangePasswordDialog() {
        String username = getUsernameInput();
        if (username.isEmpty()) {
            username = "nguyen_van_a";
        }
        ChangePasswordDialog dialog = new ChangePasswordDialog(this, username);
        dialog.setVisible(true);
    }

    private String getUsernameInput() {
        String user = txtUsername.getText().trim();
        String placeholder = "Nhập tên đăng nhập hoặc email...";
        return user.equals(placeholder) ? "" : user;
    }

    /**
     * Xử lý sự kiện nhấn nút Đăng nhập
     */
    private void processLogin() {
        String username = getUsernameInput();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty()) {
            lblError.setForeground(UserUITheme.COLOR_DANGER);
            lblError.setText("⚠️ Vui lòng nhập Tên đăng nhập hoặc Email!");
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            lblError.setForeground(UserUITheme.COLOR_DANGER);
            lblError.setText("⚠️ Vui lòng nhập Mật khẩu!");
            txtPassword.requestFocus();
            return;
        }

        lblError.setText(" ");

        // Hiển thị hộp thoại đăng nhập thành công & cung cấp lựa chọn mở Profile / Đổi mật khẩu
        Object[] options = {"Vào phiên làm việc", "Xem & Sửa Hồ sơ", "Đổi mật khẩu"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "🎉 Đăng nhập thành công!\n\n"
                        + "Chào mừng người dùng: @" + username + "\n"
                        + "Bạn có thể vào phiên làm việc hoặc quản lý thông tin tài khoản ngay bây giờ.",
                "Đăng nhập thành công",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 1) {
            openProfileDialog();
        } else if (choice == 2) {
            openChangePasswordDialog();
        } else if (choice == 0) {
            UserMainFrame mainFrame = new UserMainFrame(username);
            mainFrame.setVisible(true);
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}

package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Hộp thoại Hồ sơ & Cập nhật thông tin cá nhân (ProfileDialog)
 * Hỗ trợ người dùng sau khi đăng nhập cập nhật thông tin tài khoản theo đặc tả hệ thống.
 */
public class ProfileDialog extends JDialog {

    private JTextField txtUsername;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtAddress;

    private JComboBox<Integer> cboDay;
    private JComboBox<Integer> cboMonth;
    private JComboBox<Integer> cboYear;

    private JRadioButton rdoMale;
    private JRadioButton rdoFemale;
    private JRadioButton rdoOther;
    private ButtonGroup btgGender;

    private JLabel lblError;
    private JButton btnSave;
    private JButton btnChangePassword;
    private JButton btnClose;

    private final Frame parentFrame;
    private String currentUsername;
    private String currentFullName;
    private String currentEmail;
    private String currentAddress;
    private int currentDobDay;
    private int currentDobMonth;
    private int currentDobYear;
    private String currentGender;
    private boolean isSaved = false;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public ProfileDialog() {
        this(null, "nguyen_van_a", "Nguyễn Văn An", "nguyenvana@gmail.com", "227 Nguyễn Văn Cừ, Quận 5, TP.HCM", 15, 8, 2000, "Nam");
    }

    public ProfileDialog(Frame parent) {
        this(parent, "nguyen_van_a", "Nguyễn Văn An", "nguyenvana@gmail.com", "227 Nguyễn Văn Cừ, Quận 5, TP.HCM", 15, 8, 2000, "Nam");
    }

    public ProfileDialog(Frame parent, String username, String fullName, String email, String address,
                         int dobDay, int dobMonth, int dobYear, String gender) {
        super(parent, "Hồ sơ cá nhân - ChatConnect", true);
        this.parentFrame = parent;
        this.currentUsername = (username != null && !username.isEmpty()) ? username : "user";
        this.currentFullName = (fullName != null && !fullName.isEmpty()) ? fullName : "Nguyễn Văn An";
        this.currentEmail = (email != null && !email.isEmpty()) ? email : "user@gmail.com";
        this.currentAddress = (address != null) ? address : "";
        this.currentDobDay = (dobDay >= 1 && dobDay <= 31) ? dobDay : 1;
        this.currentDobMonth = (dobMonth >= 1 && dobMonth <= 12) ? dobMonth : 1;
        this.currentDobYear = (dobYear >= 1930 && dobYear <= LocalDate.now().getYear()) ? dobYear : 2000;
        this.currentGender = (gender != null && !gender.isEmpty()) ? gender : "Nam";

        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout());

        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // Header Panel with Profile Avatar & Info
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Content inside ScrollPane
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel formCard = new UserUITheme.RoundedPanel(12, UserUITheme.COLOR_CARD_BACKGROUND, UserUITheme.COLOR_BORDER);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(22, 28, 22, 28));

        // 1. Tên đăng nhập (Read-only ID)
        formCard.add(createFieldLabel("Tên đăng nhập (Username)", "Cố định, không thể thay đổi"));
        formCard.add(Box.createVerticalStrut(6));
        txtUsername = new JTextField(currentUsername);
        txtUsername.setFont(UserUITheme.FONT_BODY);
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(245, 247, 250));
        txtUsername.setForeground(new Color(100, 115, 130));
        txtUsername.setBorder(new CompoundBorder(
                new UserUITheme.RoundedLineBorder(UserUITheme.COLOR_BORDER, 8, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtUsername);
        formCard.add(Box.createVerticalStrut(12));

        // 2. Họ và tên
        formCard.add(createFieldLabel("Họ và tên *", "Tên hiển thị trong danh bạ và tin nhắn"));
        formCard.add(Box.createVerticalStrut(6));
        txtFullName = UserUITheme.createTextField(null);
        txtFullName.setText(currentFullName);
        txtFullName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtFullName.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtFullName);
        formCard.add(Box.createVerticalStrut(12));

        // 3. Email
        formCard.add(createFieldLabel("Địa chỉ Email *", "Email nhận mã khôi phục mật khẩu"));
        formCard.add(Box.createVerticalStrut(6));
        txtEmail = UserUITheme.createTextField(null);
        txtEmail.setText(currentEmail);
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtEmail);
        formCard.add(Box.createVerticalStrut(12));

        // 4. Ngày sinh (DOB)
        formCard.add(createFieldLabel("Ngày sinh (DOB) *", "Ngày / Tháng / Năm"));
        formCard.add(Box.createVerticalStrut(6));
        JPanel dobPickers = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dobPickers.setOpaque(false);
        dobPickers.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboDay = new JComboBox<>();
        for (int i = 1; i <= 31; i++) cboDay.addItem(i);
        cboDay.setSelectedItem(currentDobDay);
        cboDay.setPreferredSize(new Dimension(75, 36));

        cboMonth = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cboMonth.addItem(i);
        cboMonth.setSelectedItem(currentDobMonth);
        cboMonth.setPreferredSize(new Dimension(75, 36));

        cboYear = new JComboBox<>();
        int nowYear = LocalDate.now().getYear();
        for (int y = nowYear; y >= 1930; y--) cboYear.addItem(y);
        cboYear.setSelectedItem(currentDobYear);
        cboYear.setPreferredSize(new Dimension(95, 36));

        cboDay.setFont(UserUITheme.FONT_BODY);
        cboMonth.setFont(UserUITheme.FONT_BODY);
        cboYear.setFont(UserUITheme.FONT_BODY);

        dobPickers.add(new JLabel("Ngày:"));
        dobPickers.add(cboDay);
        dobPickers.add(Box.createHorizontalStrut(6));
        dobPickers.add(new JLabel("Tháng:"));
        dobPickers.add(cboMonth);
        dobPickers.add(Box.createHorizontalStrut(6));
        dobPickers.add(new JLabel("Năm:"));
        dobPickers.add(cboYear);

        formCard.add(dobPickers);
        formCard.add(Box.createVerticalStrut(12));

        // 5. Giới tính
        formCard.add(createFieldLabel("Giới tính *", null));
        formCard.add(Box.createVerticalStrut(6));
        JPanel genderRadios = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        genderRadios.setOpaque(false);
        genderRadios.setAlignmentX(Component.LEFT_ALIGNMENT);

        rdoMale = new JRadioButton("Nam", "Nam".equalsIgnoreCase(currentGender));
        rdoFemale = new JRadioButton("Nữ", "Nữ".equalsIgnoreCase(currentGender));
        rdoOther = new JRadioButton("Khác", !"Nam".equalsIgnoreCase(currentGender) && !"Nữ".equalsIgnoreCase(currentGender));

        rdoMale.setFont(UserUITheme.FONT_BODY);
        rdoFemale.setFont(UserUITheme.FONT_BODY);
        rdoOther.setFont(UserUITheme.FONT_BODY);

        rdoMale.setOpaque(false);
        rdoFemale.setOpaque(false);
        rdoOther.setOpaque(false);

        btgGender = new ButtonGroup();
        btgGender.add(rdoMale);
        btgGender.add(rdoFemale);
        btgGender.add(rdoOther);

        genderRadios.add(rdoMale);
        genderRadios.add(rdoFemale);
        genderRadios.add(rdoOther);
        formCard.add(genderRadios);
        formCard.add(Box.createVerticalStrut(12));

        // 6. Địa chỉ
        formCard.add(createFieldLabel("Địa chỉ thường trú (Address)", "Số nhà, tên đường, khu vực"));
        formCard.add(Box.createVerticalStrut(6));
        txtAddress = UserUITheme.createTextField(null);
        txtAddress.setText(currentAddress);
        txtAddress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtAddress.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtAddress);
        formCard.add(Box.createVerticalStrut(10));

        // 7. Nhãn lỗi
        lblError = new JLabel(" ");
        lblError.setFont(UserUITheme.FONT_SMALL_BOLD);
        lblError.setForeground(UserUITheme.COLOR_DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(lblError);

        contentContainer.add(formCard, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(contentContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Bar
        add(createBottomBar(), BorderLayout.SOUTH);

        setSize(660, 740);
        setMinimumSize(new Dimension(600, 620));
        setLocationRelativeTo(parentFrame);
    }

    private JPanel createFieldLabel(String labelText, String hint) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UserUITheme.FONT_BODY_BOLD);
        lbl.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        p.add(lbl);

        if (hint != null && !hint.isEmpty()) {
            JLabel lblHint = new JLabel("(" + hint + ")");
            lblHint.setFont(UserUITheme.FONT_SMALL);
            lblHint.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            p.add(lblHint);
        }
        return p;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UserUITheme.COLOR_SIDEBAR);
        panel.setBorder(new EmptyBorder(18, 28, 18, 28));

        // Avatar + User Info Left
        JPanel userBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        userBox.setOpaque(false);

        // Avatar circle
        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UserUITheme.enableAntialiasing(g2);
                g2.setColor(UserUITheme.COLOR_PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                String initials = getInitials(currentFullName);
                int tx = (getWidth() - fm.stringWidth(initials)) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initials, tx, ty);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(54, 54);
            }
        };
        avatarCircle.setOpaque(false);
        userBox.add(avatarCircle);

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);

        JLabel nameLabel = new JLabel(currentFullName);
        nameLabel.setFont(UserUITheme.FONT_TITLE_MEDIUM);
        nameLabel.setForeground(Color.WHITE);

        JPanel subRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        subRow.setOpaque(false);

        JLabel userTag = new JLabel("@" + currentUsername);
        userTag.setFont(UserUITheme.FONT_SMALL);
        userTag.setForeground(new Color(195, 210, 230));
        subRow.add(userTag);

        JPanel onlineBadge = UserUITheme.createStatusBadge(true);
        subRow.add(onlineBadge);

        details.add(nameLabel);
        details.add(subRow);
        userBox.add(details);

        panel.add(userBox, BorderLayout.WEST);
        return panel;
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "U";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER));

        // Nút bên trái: Đổi mật khẩu
        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        leftActions.setOpaque(false);
        btnChangePassword = UserUITheme.createSecondaryButton("Đổi mật khẩu...");
        btnChangePassword.addActionListener(e -> {
            ChangePasswordDialog pwdDialog = new ChangePasswordDialog(parentFrame, currentUsername);
            pwdDialog.setVisible(true);
        });
        leftActions.add(btnChangePassword);
        bar.add(leftActions, BorderLayout.WEST);

        // Nút bên phải: Đóng + Lưu thay đổi
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        rightActions.setOpaque(false);

        btnClose = UserUITheme.createSecondaryButton("Đóng");
        btnClose.setPreferredSize(new Dimension(95, 38));
        btnClose.addActionListener(e -> dispose());

        btnSave = UserUITheme.createPrimaryButton("Lưu thay đổi");
        btnSave.setPreferredSize(new Dimension(150, 38));
        btnSave.addActionListener(e -> processSaveProfile());

        rightActions.add(btnClose);
        rightActions.add(btnSave);
        bar.add(rightActions, BorderLayout.EAST);

        return bar;
    }

    private void processSaveProfile() {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        if (fullName.isEmpty()) {
            showError("Vui lòng nhập Họ và tên!");
            txtFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showError("Vui lòng nhập địa chỉ Email!");
            txtEmail.requestFocus();
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Địa chỉ Email không đúng định dạng!");
            txtEmail.requestFocus();
            return;
        }

        int day = (int) cboDay.getSelectedItem();
        int month = (int) cboMonth.getSelectedItem();
        int year = (int) cboYear.getSelectedItem();
        LocalDate dob;
        try {
            dob = LocalDate.of(year, month, day);
        } catch (Exception ex) {
            showError("Ngày sinh đã chọn không hợp lệ!");
            return;
        }

        if (dob.isAfter(LocalDate.now())) {
            showError("Ngày sinh không thể lớn hơn ngày hiện tại!");
            return;
        }

        lblError.setText(" ");

        // Cập nhật giá trị
        this.currentFullName = fullName;
        this.currentEmail = email;
        this.currentAddress = address;
        this.currentDobDay = day;
        this.currentDobMonth = month;
        this.currentDobYear = year;
        this.currentGender = rdoMale.isSelected() ? "Nam" : (rdoFemale.isSelected() ? "Nữ" : "Khác");
        this.isSaved = true;

        JOptionPane.showMessageDialog(
                this,
                "Cập nhật thông tin tài khoản thành công!\n\n"
                        + "• Họ và tên: " + currentFullName + "\n"
                        + "• Email: " + currentEmail + "\n"
                        + "• Ngày sinh: " + String.format("%02d/%02d/%d", day, month, year) + "\n"
                        + "• Giới tính: " + currentGender + "\n"
                        + "• Địa chỉ: " + currentAddress,
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }

    private void showError(String msg) {
        lblError.setText("⚠️ " + msg);
    }

    public boolean isSaved() {
        return isSaved;
    }

    public String getUpdatedFullName() {
        return currentFullName;
    }

    public String getUpdatedEmail() {
        return currentEmail;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProfileDialog dialog = new ProfileDialog(null);
            dialog.setVisible(true);
        });
    }
}

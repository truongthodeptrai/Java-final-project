package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Hộp thoại Đăng ký tài khoản (RegisterDialog)
 * Đầy đủ các trường theo đặc tả: Username, Password, Full Name, Address, DOB, Gender, Email.
 */
public class RegisterDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtAddress;

    // Bộ chọn Ngày sinh (DOB)
    private JComboBox<Integer> cboDay;
    private JComboBox<Integer> cboMonth;
    private JComboBox<Integer> cboYear;

    // Giới tính (Gender)
    private JRadioButton rdoMale;
    private JRadioButton rdoFemale;
    private JRadioButton rdoOther;
    private ButtonGroup btgGender;

    private JCheckBox chkShowPassword;
    private JCheckBox chkTerms;
    private JLabel lblError;
    private JButton btnRegister;
    private JButton btnCancel;

    private final Frame parentFrame;
    private String registeredUsername = null;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public RegisterDialog() {
        this(null);
    }

    public RegisterDialog(Frame parent) {
        super(parent, "Đăng ký tài khoản mới - ChatConnect", true);
        this.parentFrame = parent;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout());

        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Form Content inside a modern scroll pane
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel formCard = new UserUITheme.RoundedPanel(12, UserUITheme.COLOR_CARD_BACKGROUND, UserUITheme.COLOR_BORDER);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(22, 28, 22, 28));

        // 1. Tên đăng nhập (Username)
        formCard.add(createFieldLabel("Tên đăng nhập (Username) *", "Từ 3-30 ký tự, không dấu, ví dụ: user123"));
        formCard.add(Box.createVerticalStrut(6));
        txtUsername = UserUITheme.createTextField("Nhập tên đăng nhập...");
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtUsername);
        formCard.add(Box.createVerticalStrut(12));

        // 2. Mật khẩu & Xác nhận mật khẩu (2 cột ngang tiện lợi)
        JPanel passRow = new JPanel(new GridLayout(1, 2, 16, 0));
        passRow.setOpaque(false);
        passRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        passRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel passCol = new JPanel();
        passCol.setLayout(new BoxLayout(passCol, BoxLayout.Y_AXIS));
        passCol.setOpaque(false);
        passCol.add(createFieldLabel("Mật khẩu *", "Tối thiểu 6 ký tự"));
        passCol.add(Box.createVerticalStrut(6));
        txtPassword = UserUITheme.createPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passCol.add(txtPassword);

        JPanel confirmPassCol = new JPanel();
        confirmPassCol.setLayout(new BoxLayout(confirmPassCol, BoxLayout.Y_AXIS));
        confirmPassCol.setOpaque(false);
        confirmPassCol.add(createFieldLabel("Xác nhận mật khẩu *", "Nhập lại mật khẩu"));
        confirmPassCol.add(Box.createVerticalStrut(6));
        txtConfirmPassword = UserUITheme.createPasswordField();
        txtConfirmPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        confirmPassCol.add(txtConfirmPassword);

        passRow.add(passCol);
        passRow.add(confirmPassCol);
        formCard.add(passRow);
        formCard.add(Box.createVerticalStrut(8));

        // Checkbox hiển thị mật khẩu
        chkShowPassword = new JCheckBox("Hiển thị mật khẩu");
        chkShowPassword.setFont(UserUITheme.FONT_SMALL);
        chkShowPassword.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkShowPassword.addActionListener(e -> {
            char echo = chkShowPassword.isSelected() ? (char) 0 : '•';
            txtPassword.setEchoChar(echo);
            txtConfirmPassword.setEchoChar(echo);
        });
        formCard.add(chkShowPassword);
        formCard.add(Box.createVerticalStrut(12));

        // 3. Họ và tên (Full Name)
        formCard.add(createFieldLabel("Họ và tên (Full Name) *", "Ví dụ: Nguyễn Văn An"));
        formCard.add(Box.createVerticalStrut(6));
        txtFullName = UserUITheme.createTextField("Nhập đầy đủ họ và tên...");
        txtFullName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtFullName.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtFullName);
        formCard.add(Box.createVerticalStrut(12));

        // 4. Email
        formCard.add(createFieldLabel("Địa chỉ Email *", "Dùng để nhận mật khẩu khi quên"));
        formCard.add(Box.createVerticalStrut(6));
        txtEmail = UserUITheme.createTextField("vd: example@gmail.com");
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtEmail);
        formCard.add(Box.createVerticalStrut(12));

        // 5. Ngày sinh (DOB)
        formCard.add(createFieldLabel("Ngày sinh (DOB) *", "Ngày / Tháng / Năm"));
        formCard.add(Box.createVerticalStrut(6));
        JPanel dobPickers = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dobPickers.setOpaque(false);
        dobPickers.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboDay = new JComboBox<>();
        for (int i = 1; i <= 31; i++) cboDay.addItem(i);
        cboDay.setSelectedItem(1);
        cboDay.setPreferredSize(new Dimension(75, 36));

        cboMonth = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cboMonth.addItem(i);
        cboMonth.setSelectedItem(1);
        cboMonth.setPreferredSize(new Dimension(75, 36));

        cboYear = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear; y >= 1930; y--) cboYear.addItem(y);
        cboYear.setSelectedItem(2000);
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

        // 6. Giới tính (Gender)
        formCard.add(createFieldLabel("Giới tính (Gender) *", null));
        formCard.add(Box.createVerticalStrut(6));
        JPanel genderRadios = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        genderRadios.setOpaque(false);
        genderRadios.setAlignmentX(Component.LEFT_ALIGNMENT);

        rdoMale = new JRadioButton("Nam", true);
        rdoFemale = new JRadioButton("Nữ");
        rdoOther = new JRadioButton("Khác");

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

        // 7. Địa chỉ (Address)
        formCard.add(createFieldLabel("Địa chỉ thường trú (Address)", "Số nhà, tên đường, khu vực"));
        formCard.add(Box.createVerticalStrut(6));
        txtAddress = UserUITheme.createTextField("vd: 227 Nguyễn Văn Cừ, Quận 5, TP.HCM");
        txtAddress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtAddress.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtAddress);
        formCard.add(Box.createVerticalStrut(12));

        // Checkbox điều khoản
        chkTerms = new JCheckBox("Tôi xác nhận các thông tin trên là chính xác và đồng ý với Điều khoản sử dụng.");
        chkTerms.setFont(UserUITheme.FONT_SMALL);
        chkTerms.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        chkTerms.setOpaque(false);
        chkTerms.setSelected(true);
        chkTerms.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(chkTerms);
        formCard.add(Box.createVerticalStrut(8));

        // Nhãn hiển thị lỗi validation
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

        // Bottom Action Bar
        add(createBottomBar(), BorderLayout.SOUTH);

        // Thiết lập kích thước
        setSize(660, 750);
        setMinimumSize(new Dimension(600, 640));
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

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Đăng ký tài khoản");
        title.setFont(UserUITheme.FONT_TITLE_MEDIUM);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Tạo tài khoản người dùng để tham gia nhắn tin và kết nối bạn bè");
        subtitle.setFont(UserUITheme.FONT_SMALL);
        subtitle.setForeground(new Color(195, 210, 230));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        // Badge biểu tượng thẻ đăng ký vẽ mượt mà
        JPanel iconBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UserUITheme.enableAntialiasing(g2);
                g2.setColor(new Color(45, 105, 190, 80));
                g2.fillRoundRect(0, 0, 42, 42, 12, 12);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(10, 8, 22, 26, 4, 4);
                g2.drawLine(15, 15, 27, 15);
                g2.drawLine(15, 21, 27, 21);
                g2.drawLine(15, 27, 22, 27);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(42, 42);
            }
        };
        iconBadge.setOpaque(false);

        panel.add(textPanel, BorderLayout.WEST);
        panel.add(iconBadge, BorderLayout.EAST);
        return panel;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 14));
        bar.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER));

        btnCancel = UserUITheme.createSecondaryButton("Hủy bỏ");
        btnCancel.setPreferredSize(new Dimension(110, 40));
        btnCancel.addActionListener(e -> dispose());

        btnRegister = UserUITheme.createPrimaryButton("Đăng ký tài khoản");
        btnRegister.setPreferredSize(new Dimension(185, 40));
        btnRegister.addActionListener(e -> processRegister());

        bar.add(btnCancel);
        bar.add(btnRegister);
        return bar;
    }

    private String getFieldValue(JTextField field, String placeholder) {
        String val = field.getText().trim();
        return val.equals(placeholder) ? "" : val;
    }

    private void processRegister() {
        String username = getFieldValue(txtUsername, "Nhập tên đăng nhập...");
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
        String fullName = getFieldValue(txtFullName, "Nhập đầy đủ họ và tên...");
        String email = getFieldValue(txtEmail, "vd: example@gmail.com");
        String address = getFieldValue(txtAddress, "vd: 227 Nguyễn Văn Cừ, Quận 5, TP.HCM");

        // 1. Kiểm tra Username
        if (username.isEmpty()) {
            showError("Vui lòng nhập Tên đăng nhập!", txtUsername);
            return;
        }
        if (username.length() < 3 || username.length() > 30) {
            showError("Tên đăng nhập phải có từ 3 đến 30 ký tự!", txtUsername);
            return;
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới!", txtUsername);
            return;
        }

        // 2. Kiểm tra Mật khẩu
        if (password.isEmpty()) {
            showError("Vui lòng nhập Mật khẩu!", txtPassword);
            return;
        }
        if (password.length() < 6) {
            showError("Mật khẩu phải có tối thiểu 6 ký tự!", txtPassword);
            return;
        }
        if (confirmPassword.isEmpty()) {
            showError("Vui lòng xác nhận lại mật khẩu!", txtConfirmPassword);
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không trùng khớp!", txtConfirmPassword);
            return;
        }

        // 3. Kiểm tra Họ và tên
        if (fullName.isEmpty()) {
            showError("Vui lòng nhập đầy đủ Họ và tên!", txtFullName);
            return;
        }

        // 4. Kiểm tra Email
        if (email.isEmpty()) {
            showError("Vui lòng nhập địa chỉ Email!", txtEmail);
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Địa chỉ Email không đúng định dạng (vd: user@gmail.com)!", txtEmail);
            return;
        }

        // 5. Kiểm tra Ngày sinh (DOB)
        int day = (int) cboDay.getSelectedItem();
        int month = (int) cboMonth.getSelectedItem();
        int year = (int) cboYear.getSelectedItem();
        LocalDate dob;
        try {
            dob = LocalDate.of(year, month, day);
        } catch (Exception ex) {
            showError("Ngày sinh đã chọn không hợp lệ (ví dụ: ngày 31 tháng 2)!", cboDay);
            return;
        }
        if (dob.isAfter(LocalDate.now())) {
            showError("Ngày sinh không thể lớn hơn ngày hiện tại!", cboYear);
            return;
        }

        // 6. Giới tính
        String gender = rdoMale.isSelected() ? "Nam" : (rdoFemale.isSelected() ? "Nữ" : "Khác");

        // 7. Đồng ý điều khoản
        if (!chkTerms.isSelected()) {
            showError("Vui lòng đồng ý với Điều khoản sử dụng để tiếp tục!", chkTerms);
            return;
        }

        lblError.setText(" ");

        // Hiển thị thông báo đăng ký thành công
        String successMsg = "🎉 Đăng ký tài khoản thành công!\n\n"
                + "Thông tin tài khoản đã tạo:\n"
                + "• Tên đăng nhập: " + username + "\n"
                + "• Họ và tên: " + fullName + "\n"
                + "• Email: " + email + "\n"
                + "• Ngày sinh: " + String.format("%02d/%02d/%d", day, month, year) + "\n"
                + "• Giới tính: " + gender + "\n"
                + (address.isEmpty() ? "" : "• Địa chỉ: " + address + "\n")
                + "\nBạn có thể đăng nhập ngay bằng tài khoản này.";

        JOptionPane.showMessageDialog(
                this,
                successMsg,
                "Đăng ký thành công",
                JOptionPane.INFORMATION_MESSAGE
        );

        this.registeredUsername = username;
        dispose();
    }

    private void showError(String message, Component target) {
        lblError.setText("⚠️ " + message);
        if (target != null) {
            target.requestFocus();
        }
    }

    public String getRegisteredUsername() {
        return registeredUsername;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegisterDialog dialog = new RegisterDialog(null);
            dialog.setVisible(true);
        });
    }
}

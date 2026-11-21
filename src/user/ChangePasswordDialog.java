package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Hộp thoại Đổi mật khẩu (ChangePasswordDialog)
 * Hỗ trợ người dùng sau khi đăng nhập cập nhật mật khẩu mới một cách an toàn và trực quan.
 */
public class ChangePasswordDialog extends JDialog {

    private JPasswordField txtCurrentPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;
    private JCheckBox chkShowPass;
    private JLabel lblError;
    private JButton btnUpdate;
    private JButton btnCancel;

    private final Frame parentFrame;
    private final String username;

    public ChangePasswordDialog() {
        this(null, "currentUser");
    }

    public ChangePasswordDialog(Frame parent) {
        this(parent, "currentUser");
    }

    public ChangePasswordDialog(Frame parent, String username) {
        super(parent, "Đổi mật khẩu - ChatConnect", true);
        this.parentFrame = parent;
        this.username = (username != null && !username.trim().isEmpty()) ? username : "currentUser";
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(16, 28, 14, 28));

        JPanel formCard = new UserUITheme.RoundedPanel(12, UserUITheme.COLOR_CARD_BACKGROUND, UserUITheme.COLOR_BORDER);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(18, 24, 18, 24));

        // Trường: Mật khẩu hiện tại
        formCard.add(createFieldLabel("Mật khẩu hiện tại *"));
        formCard.add(Box.createVerticalStrut(6));
        txtCurrentPass = UserUITheme.createPasswordField();
        txtCurrentPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtCurrentPass.setPreferredSize(new Dimension(380, 38));
        txtCurrentPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtCurrentPass);
        formCard.add(Box.createVerticalStrut(12));

        // Trường: Mật khẩu mới
        formCard.add(createFieldLabel("Mật khẩu mới *"));
        formCard.add(Box.createVerticalStrut(6));
        txtNewPass = UserUITheme.createPasswordField();
        txtNewPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtNewPass.setPreferredSize(new Dimension(380, 38));
        txtNewPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtNewPass);
        formCard.add(Box.createVerticalStrut(12));

        // Trường: Xác nhận mật khẩu mới
        formCard.add(createFieldLabel("Xác nhận mật khẩu mới *"));
        formCard.add(Box.createVerticalStrut(6));
        txtConfirmPass = UserUITheme.createPasswordField();
        txtConfirmPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtConfirmPass.setPreferredSize(new Dimension(380, 38));
        txtConfirmPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(txtConfirmPass);
        formCard.add(Box.createVerticalStrut(8));

        // Checkbox hiển thị mật khẩu
        chkShowPass = new JCheckBox("Hiển thị mật khẩu");
        chkShowPass.setFont(UserUITheme.FONT_SMALL);
        chkShowPass.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        chkShowPass.setOpaque(false);
        chkShowPass.setFocusPainted(false);
        chkShowPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkShowPass.addActionListener(e -> togglePasswordVisibility(chkShowPass.isSelected()));
        formCard.add(chkShowPass);
        formCard.add(Box.createVerticalStrut(6));

        // Nhãn thông báo lỗi
        lblError = new JLabel(" ");
        lblError.setFont(UserUITheme.FONT_SMALL);
        lblError.setForeground(UserUITheme.COLOR_DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(lblError);
        formCard.add(Box.createVerticalStrut(6));

        // Box yêu cầu mật khẩu
        JPanel guideBox = new UserUITheme.RoundedPanel(8, new Color(246, 248, 252), UserUITheme.COLOR_BORDER);
        guideBox.setLayout(new BoxLayout(guideBox, BoxLayout.Y_AXIS));
        guideBox.setBorder(new EmptyBorder(10, 14, 10, 14));
        guideBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        guideBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        JLabel lblReq1 = new JLabel("• Mật khẩu mới tối thiểu 6 ký tự");
        lblReq1.setFont(UserUITheme.FONT_SMALL);
        lblReq1.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);

        JLabel lblReq2 = new JLabel("• Mật khẩu mới không được trùng mật khẩu hiện tại");
        lblReq2.setFont(UserUITheme.FONT_SMALL);
        lblReq2.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);

        JLabel lblReq3 = new JLabel("• Mật khẩu xác nhận phải trùng khớp tuyệt đối");
        lblReq3.setFont(UserUITheme.FONT_SMALL);
        lblReq3.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);

        guideBox.add(lblReq1);
        guideBox.add(Box.createVerticalStrut(4));
        guideBox.add(lblReq2);
        guideBox.add(Box.createVerticalStrut(4));
        guideBox.add(lblReq3);

        formCard.add(guideBox);

        centerPanel.add(formCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Phím Enter để submit
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processChangePassword();
                }
            }
        };
        txtCurrentPass.addKeyListener(enterKeyAdapter);
        txtNewPass.addKeyListener(enterKeyAdapter);
        txtConfirmPass.addKeyListener(enterKeyAdapter);

        // Bottom Bar
        add(createBottomBar(), BorderLayout.SOUTH);

        pack();
        setSize(490, 600);
        setLocationRelativeTo(parentFrame);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UserUITheme.FONT_BODY_BOLD);
        label.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UserUITheme.COLOR_SIDEBAR);
        panel.setBorder(new EmptyBorder(18, 28, 18, 28));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Đổi mật khẩu");
        title.setFont(UserUITheme.FONT_TITLE_MEDIUM);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Tài khoản: " + username + " • Tăng cường an toàn cho tài khoản");
        subtitle.setFont(UserUITheme.FONT_SMALL);
        subtitle.setForeground(new Color(195, 210, 230));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        // Shield badge
        JPanel shieldBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UserUITheme.enableAntialiasing(g2);
                g2.setColor(new Color(45, 105, 190, 80));
                g2.fillRoundRect(0, 0, 42, 42, 12, 12);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));

                // Vẽ hình khiên bảo vệ
                int[] xPoints = {21, 33, 33, 21, 9, 9};
                int[] yPoints = {8, 13, 25, 34, 25, 13};
                g2.drawPolygon(xPoints, yPoints, 6);
                g2.drawLine(21, 14, 21, 28);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(42, 42);
            }
        };
        shieldBadge.setOpaque(false);

        panel.add(textPanel, BorderLayout.WEST);
        panel.add(shieldBadge, BorderLayout.EAST);
        return panel;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 14));
        bar.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER));

        btnCancel = UserUITheme.createSecondaryButton("Hủy bỏ");
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> dispose());

        btnUpdate = UserUITheme.createPrimaryButton("Cập nhật mật khẩu");
        btnUpdate.setPreferredSize(new Dimension(175, 38));
        btnUpdate.addActionListener(e -> processChangePassword());

        bar.add(btnCancel);
        bar.add(btnUpdate);
        return bar;
    }

    private void togglePasswordVisibility(boolean show) {
        char echo = show ? (char) 0 : '•';
        txtCurrentPass.setEchoChar(echo);
        txtNewPass.setEchoChar(echo);
        txtConfirmPass.setEchoChar(echo);
    }

    private void processChangePassword() {
        String currentPass = new String(txtCurrentPass.getPassword()).trim();
        String newPass = new String(txtNewPass.getPassword()).trim();
        String confirmPass = new String(txtConfirmPass.getPassword()).trim();

        // 1. Kiểm tra rỗng
        if (currentPass.isEmpty()) {
            lblError.setText("Vui lòng nhập mật khẩu hiện tại!");
            txtCurrentPass.requestFocus();
            return;
        }

        if (newPass.isEmpty()) {
            lblError.setText("Vui lòng nhập mật khẩu mới!");
            txtNewPass.requestFocus();
            return;
        }

        if (confirmPass.isEmpty()) {
            lblError.setText("Vui lòng xác nhận mật khẩu mới!");
            txtConfirmPass.requestFocus();
            return;
        }

        // 2. Độ dài tối thiểu
        if (newPass.length() < 6) {
            lblError.setText("Mật khẩu mới phải có ít nhất 6 ký tự!");
            txtNewPass.requestFocus();
            return;
        }

        // 3. Trùng mật khẩu cũ
        if (newPass.equals(currentPass)) {
            lblError.setText("Mật khẩu mới không được trùng với mật khẩu hiện tại!");
            txtNewPass.requestFocus();
            return;
        }

        // 4. Khớp xác nhận
        if (!newPass.equals(confirmPass)) {
            lblError.setText("Mật khẩu xác nhận không trùng khớp với mật khẩu mới!");
            txtConfirmPass.requestFocus();
            return;
        }

        lblError.setText(" ");

        JOptionPane.showMessageDialog(
                this,
                "Đổi mật khẩu thành công!\n\nMật khẩu tài khoản '" + username + "' đã được cập nhật thành công vào hệ thống.",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(null, "demo_user");
            dialog.setVisible(true);
        });
    }
}

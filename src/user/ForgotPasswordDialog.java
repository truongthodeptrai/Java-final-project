package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.SecureRandom;

/**
 * Hộp thoại Khôi phục mật khẩu (ForgotPasswordDialog)
 * Cho phép người dùng nhập username hoặc email để nhận mật khẩu ngẫu nhiên mới qua hòm thư.
 */
public class ForgotPasswordDialog extends JDialog {

    private JTextField txtIdentifier;
    private JLabel lblError;
    private JButton btnSubmit;
    private JButton btnCancel;
    private final Frame parentFrame;

    private static final String CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#%*";
    private static final SecureRandom RANDOM = new SecureRandom();

    public ForgotPasswordDialog() {
        this(null, "");
    }

    public ForgotPasswordDialog(Frame parent) {
        this(parent, "");
    }

    public ForgotPasswordDialog(Frame parent, String initialIdentifier) {
        super(parent, "Khôi phục mật khẩu - ChatConnect", true);
        this.parentFrame = parent;
        initComponents(initialIdentifier);
    }

    private void initComponents(String initialIdentifier) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(520, 420);
        setLayout(new BorderLayout());

        // Áp dụng theme và căn giữa
        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center Content Card
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(16, 28, 20, 28));

        JPanel formCard = new UserUITheme.RoundedPanel(12, UserUITheme.COLOR_CARD_BACKGROUND, UserUITheme.COLOR_BORDER);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(22, 24, 22, 24));

        // Label trường nhập
        JLabel lblInput = new JLabel("Tên đăng nhập hoặc Email đã đăng ký:");
        lblInput.setFont(UserUITheme.FONT_BODY_BOLD);
        lblInput.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        lblInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(lblInput);
        formCard.add(Box.createVerticalStrut(8));

        // Text field nhập username/email
        txtIdentifier = UserUITheme.createTextField("Nhập tên đăng nhập hoặc địa chỉ email...");
        txtIdentifier.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtIdentifier.setPreferredSize(new Dimension(420, 40));
        txtIdentifier.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (initialIdentifier != null && !initialIdentifier.trim().isEmpty()) {
            txtIdentifier.setText(initialIdentifier.trim());
            txtIdentifier.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        }

        txtIdentifier.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processResetPassword();
                }
            }
        });
        formCard.add(txtIdentifier);
        formCard.add(Box.createVerticalStrut(10));

        // Label lỗi thông báo khi nhập thiếu
        lblError = new JLabel(" ");
        lblError.setFont(UserUITheme.FONT_SMALL);
        lblError.setForeground(UserUITheme.COLOR_DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(lblError);
        formCard.add(Box.createVerticalStrut(6));

        // Hộp thông tin hướng dẫn
        JPanel noteBox = new UserUITheme.RoundedPanel(8, new Color(238, 244, 255), new Color(205, 222, 252));
        noteBox.setLayout(new BorderLayout());
        noteBox.setBorder(new EmptyBorder(10, 12, 10, 12));
        noteBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        noteBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JLabel lblNote = new JLabel("<html><font color='#2D69BE'><b>💡 Hướng dẫn:</b></font> Sau khi nhận được mật khẩu ngẫu nhiên gửi tới email, vui lòng sử dụng mật khẩu đó để đăng nhập và tiến hành đổi lại mật khẩu cá nhân mới.</html>");
        lblNote.setFont(UserUITheme.FONT_SMALL);
        lblNote.setForeground(new Color(40, 60, 90));
        noteBox.add(lblNote, BorderLayout.CENTER);
        formCard.add(noteBox);

        centerPanel.add(formCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Action Buttons
        JPanel bottomBar = createBottomBar();
        add(bottomBar, BorderLayout.SOUTH);

        pack();
        setSize(520, 430);
        setLocationRelativeTo(parentFrame);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UserUITheme.COLOR_SIDEBAR);
        panel.setBorder(new EmptyBorder(18, 28, 18, 28));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Khôi phục mật khẩu");
        title.setFont(UserUITheme.FONT_TITLE_MEDIUM);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Hệ thống sẽ tạo mật khẩu ngẫu nhiên và gửi tới email của bạn");
        subtitle.setFont(UserUITheme.FONT_SMALL);
        subtitle.setForeground(new Color(195, 210, 230));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        JLabel iconLabel = new JLabel("🔑");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        panel.add(textPanel, BorderLayout.WEST);
        panel.add(iconLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 14));
        bar.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER));

        btnCancel = UserUITheme.createSecondaryButton("Quay lại");
        btnCancel.setPreferredSize(new Dimension(110, 38));
        btnCancel.addActionListener(e -> dispose());

        btnSubmit = UserUITheme.createPrimaryButton("Gửi mật khẩu mới");
        btnSubmit.setPreferredSize(new Dimension(175, 38));
        btnSubmit.addActionListener(e -> processResetPassword());

        bar.add(btnCancel);
        bar.add(btnSubmit);
        return bar;
    }

    private void processResetPassword() {
        String input = txtIdentifier.getText().trim();
        String placeholder = "Nhập tên đăng nhập hoặc địa chỉ email...";
        if (input.isEmpty() || input.equals(placeholder)) {
            lblError.setText("Vui lòng nhập tên đăng nhập hoặc địa chỉ email!");
            txtIdentifier.requestFocus();
            return;
        }

        lblError.setText(" ");

        // Tạo mật khẩu ngẫu nhiên an toàn (8 ký tự)
        String randomPassword = generateRandomPassword(8);

        // Giả lập xác định email đích
        String targetEmail;
        if (input.contains("@")) {
            targetEmail = input;
        } else {
            targetEmail = input.toLowerCase() + "@example.com";
        }

        // Hiển thị hộp thoại thông báo theo đúng đặc tả yêu cầu
        String dialogMessage = "Mật khẩu ngẫu nhiên đã được gửi tới email của bạn\n\n"
                + "Thông tin khôi phục:\n"
                + "• Tài khoản: " + input + "\n"
                + "• Địa chỉ email nhận: " + targetEmail + "\n"
                + "• Mật khẩu tạm thời: " + randomPassword + "\n\n"
                + "Vui lòng kiểm tra hộp thư (bao gồm cả mục Spam) để đăng nhập!";

        JOptionPane.showMessageDialog(
                this,
                dialogMessage,
                "Khôi phục mật khẩu thành công",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }

    private String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ForgotPasswordDialog dialog = new ForgotPasswordDialog(null);
            dialog.setVisible(true);
        });
    }
}

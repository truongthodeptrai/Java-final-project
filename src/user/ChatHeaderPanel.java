package user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Header của Khung trò chuyện trung tâm (ChatHeaderPanel)
 * - Tên người chat / tên nhóm
 * - Badge trạng thái: Biểu tượng ổ khóa xanh vector "Đã mã hóa đầu cuối" (E2EE)
 * - Bộ nút công cụ: Tìm kiếm (Ctrl+F), Báo cáo spam, Xóa lịch sử, Mở panel thông tin nhóm
 */
public class ChatHeaderPanel extends JPanel {

    public interface HeaderListener {
        void onToggleSearch();
        void onClearHistory();
        void onReportSpam();
        void onToggleDetails();
    }

    private final HeaderListener listener;

    private final JPanel avatarContainer;
    private AvatarComponent avatarComponent;
    private final JLabel lblTitle;
    private final JLabel lblSubtitle;
    private final JPanel encryptedBadge;

    private final JButton btnSearch;
    private final JButton btnClearHistory;
    private final JButton btnReportSpam;
    private final JButton btnToggleDetails;

    public ChatHeaderPanel(HeaderListener listener) {
        this.listener = listener;

        setLayout(new BorderLayout(10, 0));
        setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UserUITheme.COLOR_BORDER),
                new EmptyBorder(10, 16, 10, 16)
        ));

        // 1. Bên trái: Avatar + Tên + Badge mã hóa + Trạng thái
        JPanel leftBox = new JPanel(new BorderLayout(10, 0));
        leftBox.setOpaque(false);

        avatarContainer = new JPanel(new BorderLayout());
        avatarContainer.setOpaque(false);
        avatarComponent = new AvatarComponent("Chat", 42, true, null);
        avatarContainer.add(avatarComponent, BorderLayout.CENTER);
        leftBox.add(avatarContainer, BorderLayout.WEST);

        JPanel infoBox = new JPanel();
        infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));
        infoBox.setOpaque(false);

        // Dòng 1: Tiêu đề cuộc trò chuyện
        lblTitle = new JLabel("Chọn một cuộc trò chuyện");
        lblTitle.setFont(UserUITheme.FONT_TITLE_SMALL);
        lblTitle.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoBox.add(lblTitle);

        infoBox.add(Box.createVerticalStrut(2));

        // Dòng 2: Badge ổ khóa xanh "Đã mã hóa đầu cuối" + Phụ đề trạng thái
        JPanel subRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        subRow.setOpaque(false);
        subRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        encryptedBadge = createEncryptedBadge();
        encryptedBadge.setVisible(false);
        subRow.add(encryptedBadge);

        lblSubtitle = new JLabel("Đang tải dữ liệu...");
        lblSubtitle.setFont(UserUITheme.FONT_SMALL);
        lblSubtitle.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        subRow.add(lblSubtitle);

        infoBox.add(subRow);
        leftBox.add(infoBox, BorderLayout.CENTER);

        add(leftBox, BorderLayout.WEST);

        // 2. Bên phải: Bộ nút công cụ
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        rightBox.setOpaque(false);

        btnSearch = createHeaderButton("Tìm tin", ChatIcons.createSearchIcon(13, UserUITheme.COLOR_TEXT_PRIMARY), "Tìm kiếm tin nhắn trong chat (Ctrl+F)");
        btnSearch.addActionListener(e -> {
            if (listener != null) listener.onToggleSearch();
        });

        btnReportSpam = createHeaderButton("Báo spam", ChatIcons.createWarningIcon(13, UserUITheme.COLOR_WARNING), "Báo cáo cuộc trò chuyện là Spam / Vi phạm (Mục 10)");
        btnReportSpam.addActionListener(e -> {
            if (listener != null) listener.onReportSpam();
        });

        btnClearHistory = createHeaderButton("Xóa chat", ChatIcons.createTrashIcon(13, UserUITheme.COLOR_DANGER), "Xóa toàn bộ lịch sử cuộc trò chuyện (Mục 11d)");
        btnClearHistory.addActionListener(e -> {
            if (listener != null) listener.onClearHistory();
        });

        btnToggleDetails = createHeaderButton("Thông tin", ChatIcons.createInfoIcon(13, UserUITheme.COLOR_PRIMARY), "Xem thông tin nhóm / Chi tiết người dùng");
        btnToggleDetails.addActionListener(e -> {
            if (listener != null) listener.onToggleDetails();
        });

        rightBox.add(btnSearch);
        rightBox.add(btnReportSpam);
        rightBox.add(btnClearHistory);
        rightBox.add(btnToggleDetails);

        add(rightBox, BorderLayout.EAST);
    }

    /**
     * Tạo Badge "Đã mã hóa đầu cuối" với biểu tượng ổ khóa xanh lá vector sắc nét
     */
    private JPanel createEncryptedBadge() {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        badge.setBackground(new Color(235, 248, 240));
        badge.setBorder(new UserUITheme.RoundedLineBorder(new Color(40, 167, 69), 10, 1));
        badge.setToolTipText("Cuộc trò chuyện này được bảo vệ bởi Mã hóa đầu cuối (E2EE)");

        JLabel lblLockIcon = new JLabel(ChatIcons.createLockIcon(12, new Color(30, 126, 68)));
        badge.add(lblLockIcon);

        JLabel lblText = new JLabel("Đã mã hóa đầu cuối");
        lblText.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 10));
        lblText.setForeground(new Color(30, 126, 68));
        badge.add(lblText);

        return badge;
    }

    private JButton createHeaderButton(String text, Icon icon, String tooltip) {
        JButton btn = new JButton(text, icon);
        btn.setFont(UserUITheme.FONT_SMALL_BOLD);
        btn.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        btn.setIconTextGap(5);
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(new Color(245, 247, 250));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 8, 5, 8));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(230, 236, 245));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(245, 247, 250));
            }
        });

        return btn;
    }

    /**
     * Cập nhật thông tin tiêu đề, avatar và huy hiệu mã hóa
     */
    public void updateConversation(ChatDataModels.Conversation conv) {
        if (conv == null) {
            lblTitle.setText("Chưa chọn cuộc trò chuyện");
            lblSubtitle.setText("");
            encryptedBadge.setVisible(false);
            return;
        }

        lblTitle.setText(conv.getTitle());

        // Cập nhật huy hiệu ổ khóa xanh "Đã mã hóa đầu cuối"
        boolean isEncrypted = conv.isEncrypted();
        encryptedBadge.setVisible(isEncrypted);

        // Phụ đề trạng thái
        if (conv.isGroup()) {
            lblSubtitle.setText(conv.getSubtitle() + (isEncrypted ? " • Nhóm bảo mật" : ""));
            btnToggleDetails.setText("Nhóm (" + conv.getMembers().size() + ")");
        } else {
            lblSubtitle.setText(conv.isOnline() ? "● Đang hoạt động" : "○ Ngoại tuyến");
            lblSubtitle.setForeground(conv.isOnline() ? UserUITheme.COLOR_STATUS_ONLINE : UserUITheme.COLOR_TEXT_SECONDARY);
            btnToggleDetails.setText("Thông tin");
        }

        // Cập nhật Avatar
        avatarContainer.removeAll();
        avatarComponent = new AvatarComponent(
                conv.getTitle(), 42, conv.getAvatarColor(),
                conv.isGroup(), conv.isGroup() ? null : conv.isOnline()
        );
        avatarContainer.add(avatarComponent, BorderLayout.CENTER);
        avatarContainer.revalidate();
        avatarContainer.repaint();

        revalidate();
        repaint();
    }
}

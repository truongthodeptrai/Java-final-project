package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Cột 3 - Khung Thông tin chi tiết & Quản lý hội thoại (ChatDetailsPanel)
 * Thiết kế giao diện hiện đại, phân cấp rõ ràng theo các nhóm:
 * 1. Thông tin nhóm / Cá nhân (Avatar 50px, Tên, E2EE pill)
 * 2. Thành viên nhóm (Gọn gàng 34px/thành viên, có trạng thái Online/Offline, badge pill)
 * 3. Tệp phương tiện (Ảnh/Video, Tài liệu, Link đã chia sẻ)
 * 4. Cài đặt nhóm / Hội thoại (Thông báo, Ghim)
 * 5. Hành động nguy hiểm (Báo cáo vi phạm, Xóa tin nhắn, Rời nhóm - kèm xác nhận)
 */
public class ChatDetailsPanel extends JPanel {

    private ChatDataModels.Conversation currentConversation;

    private JPanel headerPanel;
    private JPanel contentContainer;

    public interface DetailsListener {
        void onCloseDetails();
        void onBlockUser(String username);
        void onReportSpam(ChatDataModels.Conversation conversation);
        void onClearHistory(ChatDataModels.Conversation conversation);
        void onManageGroup(ChatDataModels.Conversation conversation);
    }

    private DetailsListener listener;

    public ChatDetailsPanel(DetailsListener listener) {
        this.listener = listener;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UserUITheme.COLOR_BORDER));
        setPreferredSize(new Dimension(320, 700));
        setMinimumSize(new Dimension(280, 500));

        // 1. Header panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        headerPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UserUITheme.COLOR_BORDER),
                new EmptyBorder(10, 16, 10, 14)
        ));

        JLabel lblTitle = new JLabel("Thông tin hội thoại");
        lblTitle.setFont(UserUITheme.FONT_TITLE_SMALL);
        lblTitle.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnClose = new JButton(ChatIcons.createCloseIcon(12, UserUITheme.COLOR_TEXT_SECONDARY));
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setToolTipText("Đóng thông tin chi tiết");
        btnClose.setPreferredSize(new Dimension(28, 28));
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnClose.setContentAreaFilled(true);
                btnClose.setBackground(new Color(241, 245, 249));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnClose.setContentAreaFilled(false);
            }
        });
        btnClose.addActionListener(e -> {
            if (listener != null) listener.onCloseDetails();
        });
        headerPanel.add(btnClose, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Scrollable content
        contentContainer = new ScrollablePanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        contentContainer.setBorder(new EmptyBorder(12, 14, 16, 14));

        JScrollPane scrollPane = new JScrollPane(contentContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setConversation(ChatDataModels.Conversation conv) {
        this.currentConversation = conv;
        renderDetails();
    }

    private void renderDetails() {
        contentContainer.removeAll();

        if (currentConversation == null) {
            JPanel emptyBox = new JPanel(new GridBagLayout());
            emptyBox.setOpaque(false);
            JLabel lblNoData = new JLabel("Chưa chọn cuộc trò chuyện");
            lblNoData.setFont(UserUITheme.FONT_BODY);
            lblNoData.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            emptyBox.add(lblNoData);
            contentContainer.add(emptyBox);
            contentContainer.revalidate();
            contentContainer.repaint();
            return;
        }

        // 1. Thẻ Thông tin nhóm / Cá nhân (Avatar 50px)
        contentContainer.add(createProfileHeaderCard());
        contentContainer.add(Box.createVerticalStrut(10));
        contentContainer.add(createDivider());
        contentContainer.add(Box.createVerticalStrut(10));

        // 2. Thành viên nhóm hoặc Thông tin cá nhân
        if (currentConversation.isGroup()) {
            contentContainer.add(createGroupSection());
        } else {
            contentContainer.add(createContactSection());
        }

        contentContainer.add(Box.createVerticalStrut(10));
        contentContainer.add(createDivider());
        contentContainer.add(Box.createVerticalStrut(10));

        // 3. Nhóm Tệp phương tiện đã chia sẻ
        contentContainer.add(createMediaSection());
        contentContainer.add(Box.createVerticalStrut(10));
        contentContainer.add(createDivider());
        contentContainer.add(Box.createVerticalStrut(10));

        // 4. Cài đặt hội thoại
        contentContainer.add(createSettingsSection());
        contentContainer.add(Box.createVerticalStrut(10));
        contentContainer.add(createDivider());
        contentContainer.add(Box.createVerticalStrut(10));

        // 5. Hành động nguy hiểm (Đặt ở cuối khu vực)
        contentContainer.add(createHazardSection());

        contentContainer.revalidate();
        contentContainer.repaint();
    }

    /**
     * Thẻ thông tin đầu trang: Avatar 50px nhỏ gọn, Tên, Số thành viên, Badge E2EE
     */
    private JPanel createProfileHeaderCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));

        // Avatar 50px gọn gàng
        AvatarComponent bigAvatar = new AvatarComponent(
                currentConversation.getTitle(), 50, currentConversation.getAvatarColor(),
                currentConversation.isGroup(), currentConversation.isGroup() ? null : currentConversation.isOnline()
        );
        bigAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(bigAvatar);
        card.add(Box.createVerticalStrut(6));

        // Tên
        JLabel lblName = new JLabel(currentConversation.getTitle());
        lblName.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 15));
        lblName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblName);
        card.add(Box.createVerticalStrut(2));

        // Phụ đề (Số thành viên / Trạng thái)
        String sub = currentConversation.isGroup()
                ? (currentConversation.getGroupMembers().size() + " thành viên • Nhóm chat")
                : (currentConversation.isOnline() ? "Đang trực tuyến" : "Ngoại tuyến");
        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(UserUITheme.FONT_TIMESTAMP);
        lblSub.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSub);

        // Huy hiệu mã hóa đầu cuối E2EE
        card.add(Box.createVerticalStrut(6));
        JPanel encBadge = createEncryptionPillBadge(currentConversation.isEncrypted());
        encBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(encBadge);

        return card;
    }

    /**
     * Huy hiệu mã hóa dạng viên thuốc (Pill Badge) bo góc mềm mại
     */
    private JPanel createEncryptionPillBadge(boolean isEncrypted) {
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
        pill.setOpaque(true);
        pill.setMaximumSize(new Dimension(185, 22));

        if (isEncrypted) {
            pill.setBackground(new Color(236, 253, 245));
            pill.setBorder(new UserUITheme.RoundedLineBorder(new Color(167, 243, 208), 10, 1));

            JLabel icon = new JLabel(ChatIcons.createLockIcon(10, new Color(5, 150, 105)));
            pill.add(icon);

            JLabel text = new JLabel("Mã hóa đầu cuối (E2EE)");
            text.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 10));
            text.setForeground(new Color(5, 150, 105));
            pill.add(text);
        } else {
            pill.setBackground(new Color(248, 250, 252));
            pill.setBorder(new UserUITheme.RoundedLineBorder(new Color(226, 232, 240), 10, 1));

            JLabel text = new JLabel("Chưa bật mã hóa E2EE");
            text.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 10));
            text.setForeground(new Color(100, 116, 139));
            pill.add(text);
        }

        return pill;
    }

    /**
     * Danh sách thành viên nhóm với Nút Quản lý nhóm và Badge Admin/Member gọn gàng (34px/dòng)
     */
    private JPanel createGroupSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        List<ChatDataModels.GroupMember> members = currentConversation.getGroupMembers();

        // Header dòng: Tiêu đề bên trái + Nút Quản lý nhóm bên phải trên cùng 1 hàng
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel lblSecTitle = new JLabel("THÀNH VIÊN (" + members.size() + ")");
        lblSecTitle.setFont(UserUITheme.FONT_SECTION_HEADER);
        lblSecTitle.setForeground(UserUITheme.COLOR_TEXT_MUTED);
        headerRow.add(lblSecTitle, BorderLayout.WEST);

        // Nút Quản lý nhóm nhỏ gọn, tinh tế
        JButton btnManage = new JButton("Quản lý", ChatIcons.createGearIcon(12, UserUITheme.COLOR_PRIMARY));
        btnManage.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnManage.setForeground(UserUITheme.COLOR_PRIMARY);
        btnManage.setBackground(new Color(239, 246, 255));
        btnManage.setBorder(new UserUITheme.RoundedLineBorder(new Color(191, 219, 254), 6, 1));
        btnManage.setFocusPainted(false);
        btnManage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnManage.setToolTipText("Mở cửa sổ Quản lý nhóm & Phân quyền");
        btnManage.addActionListener(e -> {
            if (listener != null) listener.onManageGroup(currentConversation);
        });
        headerRow.add(btnManage, BorderLayout.EAST);

        section.add(headerRow);
        section.add(Box.createVerticalStrut(6));

        // Danh sách thành viên gọn gàng
        JPanel membersList = new JPanel();
        membersList.setLayout(new BoxLayout(membersList, BoxLayout.Y_AXIS));
        membersList.setOpaque(false);
        membersList.setAlignmentX(Component.CENTER_ALIGNMENT);
        membersList.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        for (ChatDataModels.GroupMember gm : members) {
            membersList.add(createMemberRow(gm));
            membersList.add(Box.createVerticalStrut(3));
        }

        section.add(membersList);
        return section;
    }

    /**
     * Từng dòng thành viên gọn 34px: Avatar 26px có chấm online/offline + Tên + Badge Admin/Member
     */
    private JPanel createMemberRow(ChatDataModels.GroupMember gm) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(true);
        row.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        row.setBorder(new CompoundBorder(
                new UserUITheme.RoundedLineBorder(new Color(243, 244, 246), 6, 1),
                new EmptyBorder(3, 8, 3, 8)
        ));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.setPreferredSize(new Dimension(280, 34));

        // Trái: Avatar 26px có chấm trạng thái Online / Offline
        AvatarComponent avatar = new AvatarComponent(gm.getDisplayName(), 26, gm.getAvatarColor(), false, gm.isOnline());
        row.add(avatar, BorderLayout.WEST);

        // Giữa: Tên thành viên rõ ràng
        JLabel lblName = new JLabel(gm.getDisplayName());
        lblName.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 12));
        lblName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        row.add(lblName, BorderLayout.CENTER);

        // Phải: Badge Admin / Member pill cân đối
        JPanel badgeWrapper = new JPanel(new GridBagLayout());
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(createRoleBadge(gm.isAdmin()));
        row.add(badgeWrapper, BorderLayout.EAST);

        row.setToolTipText(gm.getDisplayName() + " (@" + gm.getUsername() + ") - "
                + (gm.isOnline() ? "Đang trực tuyến" : "Ngoại tuyến") + " • "
                + (gm.isAdmin() ? "Quản trị viên" : "Thành viên"));

        // Hiệu ứng hover
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(248, 250, 252));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
            }
        });

        return row;
    }

    private JPanel createRoleBadge(boolean isAdmin) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 1));
        badge.setOpaque(true);

        if (isAdmin) {
            badge.setBackground(new Color(254, 243, 199)); // Vàng hổ phách nhạt
            badge.setBorder(new UserUITheme.RoundedLineBorder(new Color(245, 158, 11), 8, 1));

            JLabel lbl = new JLabel("Admin");
            lbl.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 9));
            lbl.setForeground(new Color(180, 83, 9));
            badge.add(lbl);
        } else {
            badge.setBackground(new Color(241, 245, 249)); // Xám slate nhạt
            badge.setBorder(new UserUITheme.RoundedLineBorder(new Color(203, 213, 225), 8, 1));

            JLabel lbl = new JLabel("Member");
            lbl.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 9));
            lbl.setForeground(new Color(71, 85, 105));
            badge.add(lbl);
        }

        return badge;
    }

    /**
     * Nhóm 3: Tệp phương tiện đã chia sẻ (Media & Files)
     */
    private JPanel createMediaSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel lblSecTitle = new JLabel("TỆP PHƯƠNG TIỆN");
        lblSecTitle.setFont(UserUITheme.FONT_SECTION_HEADER);
        lblSecTitle.setForeground(UserUITheme.COLOR_TEXT_MUTED);
        lblSecTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSecTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        section.add(lblSecTitle);
        section.add(Box.createVerticalStrut(6));

        section.add(createMediaRow(ChatIcons.createImageIcon(14, UserUITheme.COLOR_PRIMARY), "Ảnh & Video", "12 tệp"));
        section.add(Box.createVerticalStrut(4));
        section.add(createMediaRow(ChatIcons.createPaperclipIcon(14, new Color(16, 185, 129)), "Tài liệu & File", "4 tệp"));
        section.add(Box.createVerticalStrut(4));
        section.add(createMediaRow(ChatIcons.createAiSparkleIcon(14, new Color(139, 92, 246)), "Liên kết đã gửi", "2 liên kết"));

        return section;
    }

    private JPanel createMediaRow(Icon icon, String label, String countText) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(true);
        row.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        row.setBorder(new CompoundBorder(
                new UserUITheme.RoundedLineBorder(new Color(243, 244, 246), 6, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel(icon);
        row.add(lblIcon, BorderLayout.WEST);

        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 12));
        lblName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        row.add(lblName, BorderLayout.CENTER);

        JLabel lblCount = new JLabel(countText);
        lblCount.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 11));
        lblCount.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        row.add(lblCount, BorderLayout.EAST);

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(248, 250, 252));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(ChatDetailsPanel.this, "Danh sách " + label + " của hội thoại (" + countText + ").", label, JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return row;
    }

    /**
     * Nhóm 4: Cài đặt nhóm / Hội thoại (Settings Section)
     */
    private JPanel createSettingsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel lblSecTitle = new JLabel("CÀI ĐẶT HỘI THOẠI");
        lblSecTitle.setFont(UserUITheme.FONT_SECTION_HEADER);
        lblSecTitle.setForeground(UserUITheme.COLOR_TEXT_MUTED);
        lblSecTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSecTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        section.add(lblSecTitle);
        section.add(Box.createVerticalStrut(6));

        JCheckBox chkNotif = new JCheckBox("Thông báo cuộc trò chuyện", true);
        chkNotif.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 12));
        chkNotif.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        chkNotif.setOpaque(false);
        chkNotif.setAlignmentX(Component.CENTER_ALIGNMENT);
        chkNotif.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        section.add(chkNotif);

        JCheckBox chkPin = new JCheckBox("Ghim hội thoại lên đầu", false);
        chkPin.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 12));
        chkPin.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        chkPin.setOpaque(false);
        chkPin.setAlignmentX(Component.CENTER_ALIGNMENT);
        chkPin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        section.add(chkPin);

        return section;
    }

    /**
     * Nhóm 5: Hành động nguy hiểm (Hazard / Danger Actions) đặt ở cuối khu vực
     */
    private JPanel createHazardSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel lblSecTitle = new JLabel("HÀNH ĐỘNG NGUY HIỂM");
        lblSecTitle.setFont(UserUITheme.FONT_SECTION_HEADER);
        lblSecTitle.setForeground(new Color(220, 38, 38));
        lblSecTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSecTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        section.add(lblSecTitle);
        section.add(Box.createVerticalStrut(6));

        // 1. Nút Báo cáo spam
        JButton btnSpam = createStyledActionButton(
                "Báo cáo vi phạm / Spam",
                ChatIcons.createWarningIcon(14, new Color(217, 119, 6)),
                new Color(180, 83, 9),
                new Color(255, 251, 235),
                new Color(254, 243, 199),
                new Color(252, 211, 77)
        );
        btnSpam.addActionListener(e -> {
            if (listener != null) listener.onReportSpam(currentConversation);
        });
        section.add(btnSpam);
        section.add(Box.createVerticalStrut(6));

        // 2. Nút Xóa lịch sử (kèm hộp thoại xác nhận)
        JButton btnClear = createStyledActionButton(
                "Xóa toàn bộ tin nhắn",
                ChatIcons.createTrashIcon(14, new Color(185, 28, 28)),
                new Color(185, 28, 28),
                new Color(254, 242, 242),
                new Color(254, 226, 226),
                new Color(252, 165, 165)
        );
        btnClear.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn XÓA TOÀN BỘ tin nhắn của cuộc trò chuyện này không?\n"
                            + "Hành động này sẽ xóa vĩnh viễn và không thể hoàn tác.",
                    "Xác nhận xóa toàn bộ tin nhắn",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION && listener != null) {
                listener.onClearHistory(currentConversation);
            }
        });
        section.add(btnClear);
        section.add(Box.createVerticalStrut(6));

        // 3. Nút Chặn người dùng hoặc Rời nhóm (kèm hộp thoại xác nhận)
        if (!currentConversation.isGroup()) {
            JButton btnBlock = createStyledActionButton(
                    "Chặn người dùng này",
                    ChatIcons.createCloseIcon(13, new Color(220, 38, 38)),
                    new Color(220, 38, 38),
                    new Color(254, 242, 242),
                    new Color(254, 226, 226),
                    new Color(252, 165, 165)
            );
            btnBlock.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc chắn muốn chặn người dùng @" + currentConversation.getTitle() + "?\n"
                                + "Họ sẽ không thể gửi tin nhắn cho bạn nữa.",
                        "Xác nhận chặn người dùng",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION && listener != null) {
                    listener.onBlockUser(currentConversation.getTitle());
                }
            });
            section.add(btnBlock);
        } else {
            JButton btnLeave = createStyledActionButton(
                    "Rời khỏi nhóm chat",
                    ChatIcons.createLeaveIcon(14, new Color(220, 38, 38)),
                    new Color(220, 38, 38),
                    new Color(254, 242, 242),
                    new Color(254, 226, 226),
                    new Color(252, 165, 165)
            );
            btnLeave.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc chắn muốn rời khỏi nhóm \"" + currentConversation.getTitle() + "\" không?\n"
                                + "Bạn sẽ không nhận được tin nhắn tiếp theo từ nhóm này.",
                        "Xác nhận rời khỏi nhóm chat",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(this, "Bạn đã rời khỏi nhóm thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            section.add(btnLeave);
        }

        return section;
    }

    /**
     * Thông tin cá nhân (Chat 1-1)
     */
    private JPanel createContactSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel lblSecTitle = new JLabel("THÔNG TIN CÁ NHÂN");
        lblSecTitle.setFont(UserUITheme.FONT_SECTION_HEADER);
        lblSecTitle.setForeground(UserUITheme.COLOR_TEXT_MUTED);
        lblSecTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.add(lblSecTitle);
        section.add(Box.createVerticalStrut(6));

        String email = currentConversation.getEmail() != null ? currentConversation.getEmail() : "user@gmail.com";
        String address = currentConversation.getAddress() != null ? currentConversation.getAddress() : "Việt Nam";

        section.add(createInfoRow("Email:", email));
        section.add(Box.createVerticalStrut(6));
        section.add(createInfoRow("Địa chỉ:", address));
        section.add(Box.createVerticalStrut(6));
        section.add(createInfoRow("Trạng thái:", currentConversation.isOnline() ? "Đang trực tuyến" : "Ngoại tuyến"));

        return section;
    }

    private JButton createStyledActionButton(String text, Icon icon, Color textColor,
                                             Color bgColor, Color hoverColor, Color borderColor) {
        JButton btn = new JButton(text, icon);
        btn.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 12));
        btn.setForeground(textColor);
        btn.setBackground(bgColor);
        btn.setIconTextGap(8);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new UserUITheme.RoundedLineBorder(borderColor, 6, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btn.setPreferredSize(new Dimension(280, 34));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel lblKey = new JLabel(label);
        lblKey.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 11));
        lblKey.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 11));
        lblVal.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);

        row.add(lblKey, BorderLayout.WEST);
        row.add(lblVal, BorderLayout.CENTER);
        return row;
    }

    private JSeparator createDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(UserUITheme.COLOR_BORDER);
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
        public ScrollablePanel() {
            super();
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 48;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}

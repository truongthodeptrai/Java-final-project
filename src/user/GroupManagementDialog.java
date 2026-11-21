package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Hộp thoại Quản lý nhóm chat (GroupManagementDialog)
 * Cung cấp đầy đủ các tính năng:
 * - Đổi tên nhóm chat với cập nhật thời gian thực
 * - Nút thêm thành viên vào nhóm (mở AddGroupMemberDialog)
 * - Danh sách thành viên hiển thị badge "Admin" hoặc "Member"
 * - Nút "Gán quyền Admin" và nút "Xóa thành viên" (chỉ admin nhóm mới có quyền thao tác)
 * - Tùy chọn bật/tắt "Mã hóa nhóm" (End-to-End Encryption - E2EE)
 */
public class GroupManagementDialog extends JDialog {

    public interface GroupManagementListener {
        void onGroupUpdated(ChatDataModels.Conversation conversation);
        void onLeaveGroup(ChatDataModels.Conversation conversation);
    }

    private final ChatDataModels.Conversation conversation;
    private final String currentUsername;
    private final String currentDisplayName;
    private final List<String> allAvailableFriends;
    private final GroupManagementListener listener;

    private JTextField txtGroupName;
    private JButton btnSaveName;
    private JCheckBox chkEncrypted;
    private JLabel lblEncryptedStatus;
    private JPanel membersListPanel;
    private JLabel lblMemberCount;
    private JButton btnAddMember;

    public GroupManagementDialog(Window parent,
                                 ChatDataModels.Conversation conversation,
                                 String currentUsername,
                                 String currentDisplayName,
                                 List<String> allAvailableFriends,
                                 GroupManagementListener listener) {
        super(parent, "Quản lý nhóm: " + conversation.getTitle(), ModalityType.APPLICATION_MODAL);
        this.conversation = conversation;
        this.currentUsername = (currentUsername != null) ? currentUsername : "nguyen_van_a";
        this.currentDisplayName = (currentDisplayName != null) ? currentDisplayName : "Nguyễn Văn An";
        this.allAvailableFriends = (allAvailableFriends != null) ? allAvailableFriends : new ArrayList<>();
        this.listener = listener;

        initComponents();
    }

    private boolean isCurrentUserAdmin() {
        return conversation.isUserAdmin(currentUsername) ||
                conversation.isUserAdmin(currentDisplayName) ||
                conversation.isUserAdmin("Bạn");
    }

    private void initComponents() {
        setSize(560, 680);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // 1. Header Dialog
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Nội dung chính: Đổi tên, Mã hóa E2EE, Danh sách thành viên
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        contentPanel.add(createRenameSection());
        contentPanel.add(Box.createVerticalStrut(14));

        contentPanel.add(createEncryptionSection());
        contentPanel.add(Box.createVerticalStrut(14));

        contentPanel.add(createMembersSection());

        add(contentPanel, BorderLayout.CENTER);

        // 3. Footer Dialog
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBackground(UserUITheme.COLOR_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        AvatarComponent groupAvatar = new AvatarComponent(conversation.getTitle(), 48, conversation.getAvatarColor(), true, null);
        header.add(groupAvatar, BorderLayout.WEST);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel(conversation.getTitle());
        lblTitle.setFont(UserUITheme.FONT_TITLE_MEDIUM);
        lblTitle.setForeground(Color.WHITE);

        String roleNotice = isCurrentUserAdmin()
                ? "Vai trò: Quản trị viên nhóm (Toàn quyền quản lý)"
                : "Vai trò: Thành viên (Quyền hạn chế)";
        JLabel lblRole = new JLabel(roleNotice);
        lblRole.setFont(UserUITheme.FONT_SMALL);
        lblRole.setForeground(isCurrentUserAdmin() ? new Color(255, 215, 100) : new Color(185, 205, 230));

        titleBox.add(lblTitle);
        titleBox.add(lblRole);
        header.add(titleBox, BorderLayout.CENTER);

        return header;
    }

    /**
     * Mục 1: Đổi tên nhóm chat
     */
    private JPanel createRenameSection() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        JLabel lblHeader = new JLabel("TÊN NHÓM CHAT");
        lblHeader.setFont(UserUITheme.FONT_SECTION_HEADER);
        lblHeader.setForeground(UserUITheme.COLOR_TEXT_MUTED);

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        txtGroupName = UserUITheme.createTextField(null);
        txtGroupName.setText(conversation.getTitle());
        txtGroupName.setFont(UserUITheme.FONT_BODY_BOLD);
        txtGroupName.setPreferredSize(new Dimension(340, 38));

        btnSaveName = UserUITheme.createPrimaryButton("Lưu tên");
        btnSaveName.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnSaveName.addActionListener(e -> handleRenameGroup());

        row.add(txtGroupName, BorderLayout.CENTER);
        row.add(btnSaveName, BorderLayout.EAST);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.add(lblHeader);
        container.add(Box.createVerticalStrut(6));
        container.add(row);

        return container;
    }

    private void handleRenameGroup() {
        String newName = txtGroupName.getText().trim();
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhóm không được để trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newName.equals(conversation.getTitle())) {
            JOptionPane.showMessageDialog(this, "Tên nhóm không có thay đổi.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String oldName = conversation.getTitle();
        conversation.setTitle(newName);
        conversation.getMessages().add(new ChatDataModels.ChatMessage(
                "Hệ thống", currentDisplayName + " đã đổi tên nhóm thành \"" + newName + "\"", "Vừa xong", false
        ));

        if (listener != null) {
            listener.onGroupUpdated(conversation);
        }

        JOptionPane.showMessageDialog(this, "✓ Đã đổi tên nhóm thành: " + newName, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Mục 2: Tùy chọn Mã hóa nhóm (End-to-End Encryption)
     */
    private JPanel createEncryptionSection() {
        UserUITheme.RoundedPanel card = new UserUITheme.RoundedPanel(10, UserUITheme.COLOR_CARD_BACKGROUND, UserUITheme.COLOR_BORDER);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel iconLock = new JLabel(ChatIcons.createLockIcon(18, conversation.isEncrypted() ? new Color(30, 126, 68) : UserUITheme.COLOR_TEXT_MUTED));
        card.add(iconLock, BorderLayout.WEST);

        JPanel infoBox = new JPanel(new GridLayout(2, 1, 0, 2));
        infoBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Mã hóa đầu cuối (End-to-End Encryption)");
        lblTitle.setFont(UserUITheme.FONT_BODY_BOLD);
        lblTitle.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);

        lblEncryptedStatus = new JLabel(conversation.isEncrypted()
                ? "Tin nhắn trong nhóm được mã hóa bảo mật toàn vẹn"
                : "Mã hóa hiện đang tắt. Các tin nhắn ở dạng văn bản tiêu chuẩn");
        lblEncryptedStatus.setFont(UserUITheme.FONT_SMALL);
        lblEncryptedStatus.setForeground(conversation.isEncrypted() ? new Color(30, 126, 68) : UserUITheme.COLOR_TEXT_SECONDARY);

        infoBox.add(lblTitle);
        infoBox.add(lblEncryptedStatus);
        card.add(infoBox, BorderLayout.CENTER);

        chkEncrypted = new JCheckBox("Bật", conversation.isEncrypted());
        chkEncrypted.setFont(UserUITheme.FONT_BODY_BOLD);
        chkEncrypted.setForeground(conversation.isEncrypted() ? new Color(30, 126, 68) : UserUITheme.COLOR_TEXT_SECONDARY);
        chkEncrypted.setOpaque(false);
        chkEncrypted.setFocusPainted(false);
        chkEncrypted.addActionListener(e -> handleToggleEncryption(iconLock));
        card.add(chkEncrypted, BorderLayout.EAST);

        return card;
    }

    private void handleToggleEncryption(JLabel iconLock) {
        boolean active = chkEncrypted.isSelected();
        conversation.setEncrypted(active);

        iconLock.setIcon(ChatIcons.createLockIcon(18, active ? new Color(30, 126, 68) : UserUITheme.COLOR_TEXT_MUTED));
        chkEncrypted.setForeground(active ? new Color(30, 126, 68) : UserUITheme.COLOR_TEXT_SECONDARY);
        lblEncryptedStatus.setText(active
                ? "Tin nhắn trong nhóm được mã hóa bảo mật toàn vẹn"
                : "Mã hóa hiện đang tắt. Các tin nhắn ở dạng văn bản tiêu chuẩn");
        lblEncryptedStatus.setForeground(active ? new Color(30, 126, 68) : UserUITheme.COLOR_TEXT_SECONDARY);

        conversation.getMessages().add(new ChatDataModels.ChatMessage(
                "Hệ thống", currentDisplayName + (active ? " đã bật" : " đã tắt") + " chế độ mã hóa đầu cuối cho nhóm.",
                "Vừa xong", false
        ));

        if (listener != null) {
            listener.onGroupUpdated(conversation);
        }
    }

    /**
     * Mục 3: Danh sách thành viên, Nút Thêm, Badge Admin/Member, Gán Admin, Xóa thành viên
     */
    private JPanel createMembersSection() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setOpaque(false);

        // Header mục thành viên + Nút "+ Thêm thành viên"
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        lblMemberCount = new JLabel("THÀNH VIÊN NHÓM (" + conversation.getGroupMembers().size() + ")");
        lblMemberCount.setFont(UserUITheme.FONT_SECTION_HEADER);
        lblMemberCount.setForeground(UserUITheme.COLOR_TEXT_MUTED);
        headerRow.add(lblMemberCount, BorderLayout.WEST);

        btnAddMember = new JButton("+ Thêm thành viên");
        btnAddMember.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnAddMember.setForeground(UserUITheme.COLOR_PRIMARY);
        btnAddMember.setBackground(new Color(235, 243, 255));
        btnAddMember.setFocusPainted(false);
        btnAddMember.setBorderPainted(false);
        btnAddMember.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddMember.setBorder(new EmptyBorder(5, 10, 5, 10));
        btnAddMember.addActionListener(e -> openAddMemberDialog());
        headerRow.add(btnAddMember, BorderLayout.EAST);

        section.add(headerRow, BorderLayout.NORTH);

        // Danh sách cuộn chứa các dòng thành viên
        membersListPanel = new JPanel();
        membersListPanel.setLayout(new BoxLayout(membersListPanel, BoxLayout.Y_AXIS));
        membersListPanel.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        renderMembersList();

        JScrollPane scrollPane = new JScrollPane(membersListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UserUITheme.COLOR_BORDER));
        scrollPane.setPreferredSize(new Dimension(500, 240));
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    private void renderMembersList() {
        membersListPanel.removeAll();
        List<ChatDataModels.GroupMember> members = conversation.getGroupMembers();
        lblMemberCount.setText("THÀNH VIÊN NHÓM (" + members.size() + ")");

        boolean currentAdmin = isCurrentUserAdmin();

        for (ChatDataModels.GroupMember gm : members) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
            row.setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(245, 247, 250)),
                    new EmptyBorder(8, 12, 8, 12)
            ));

            // Left: Avatar + Tên + Badge
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            leftPanel.setOpaque(false);

            AvatarComponent avatar = new AvatarComponent(gm.getDisplayName(), 34, gm.getAvatarColor(), false, gm.isOnline());
            leftPanel.add(avatar);

            JPanel nameBox = new JPanel(new GridLayout(2, 1, 0, 1));
            nameBox.setOpaque(false);

            boolean isSelf = gm.getDisplayName().contains(currentDisplayName) ||
                    gm.getUsername().contains(currentUsername) ||
                    gm.getDisplayName().contains("Bạn");

            JLabel lblName = new JLabel(gm.getDisplayName() + (isSelf ? " (Bạn)" : ""));
            lblName.setFont(UserUITheme.FONT_BODY_BOLD);
            lblName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);

            JLabel lblUser = new JLabel("@" + gm.getUsername());
            lblUser.setFont(UserUITheme.FONT_SMALL);
            lblUser.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);

            nameBox.add(lblName);
            nameBox.add(lblUser);
            leftPanel.add(nameBox);

            // Badge Admin / Member
            leftPanel.add(createRoleBadge(gm.isAdmin()));

            row.add(leftPanel, BorderLayout.WEST);

            // Right: Bộ nút thao tác "Gán quyền Admin" và "Xóa thành viên"
            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            actionsPanel.setOpaque(false);

            if (!isSelf) {
                // Nút Gán / Gỡ Admin
                JButton btnToggleAdmin = new JButton(gm.isAdmin() ? "Hạ quyền" : "Gán Admin");
                btnToggleAdmin.setFont(UserUITheme.FONT_SMALL);
                btnToggleAdmin.setFocusPainted(false);
                btnToggleAdmin.setBorder(new EmptyBorder(4, 8, 4, 8));
                btnToggleAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));

                if (gm.isAdmin()) {
                    btnToggleAdmin.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
                    btnToggleAdmin.setBackground(new Color(240, 242, 245));
                } else {
                    btnToggleAdmin.setForeground(UserUITheme.COLOR_PRIMARY);
                    btnToggleAdmin.setBackground(new Color(235, 243, 255));
                }

                btnToggleAdmin.setEnabled(currentAdmin);
                if (!currentAdmin) {
                    btnToggleAdmin.setToolTipText("Chỉ Quản trị viên mới có quyền gán Admin");
                } else {
                    btnToggleAdmin.addActionListener(e -> handleToggleAdminRole(gm));
                }
                actionsPanel.add(btnToggleAdmin);

                // Nút Xóa thành viên
                JButton btnRemove = new JButton("Xóa");
                btnRemove.setFont(UserUITheme.FONT_SMALL_BOLD);
                btnRemove.setForeground(UserUITheme.COLOR_DANGER);
                btnRemove.setBackground(new Color(255, 235, 235));
                btnRemove.setFocusPainted(false);
                btnRemove.setBorderPainted(false);
                btnRemove.setBorder(new EmptyBorder(4, 10, 4, 10));
                btnRemove.setCursor(new Cursor(Cursor.HAND_CURSOR));

                btnRemove.setEnabled(currentAdmin);
                if (!currentAdmin) {
                    btnRemove.setToolTipText("Chỉ Quản trị viên mới có quyền xóa thành viên");
                } else {
                    btnRemove.addActionListener(e -> handleRemoveMember(gm));
                }
                actionsPanel.add(btnRemove);
            }

            row.add(actionsPanel, BorderLayout.EAST);
            membersListPanel.add(row);
        }

        membersListPanel.revalidate();
        membersListPanel.repaint();
    }

    /**
     * Tạo Badge "Admin" hoặc "Member" chuẩn nhận diện thương hiệu
     */
    private JPanel createRoleBadge(boolean isAdmin) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        badge.setOpaque(true);

        if (isAdmin) {
            badge.setBackground(new Color(254, 243, 199)); // Vàng hổ phách nhạt
            badge.setBorder(new UserUITheme.RoundedLineBorder(new Color(245, 158, 11), 8, 1));

            JLabel lbl = new JLabel("Admin");
            lbl.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 10));
            lbl.setForeground(new Color(180, 83, 9));
            badge.add(lbl);
        } else {
            badge.setBackground(new Color(241, 245, 249)); // Xám slate nhạt
            badge.setBorder(new UserUITheme.RoundedLineBorder(new Color(203, 213, 225), 8, 1));

            JLabel lbl = new JLabel("Member");
            lbl.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 10));
            lbl.setForeground(new Color(71, 85, 105));
            badge.add(lbl);
        }

        return badge;
    }

    private void handleToggleAdminRole(ChatDataModels.GroupMember gm) {
        boolean newRole = !gm.isAdmin();
        String actionName = newRole ? "gán quyền Admin" : "hạ quyền Admin";

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn " + actionName + " cho \"" + gm.getDisplayName() + "\"?",
                "Xác nhận phân quyền",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            conversation.setMemberAdmin(gm.getUsername(), newRole);
            conversation.getMessages().add(new ChatDataModels.ChatMessage(
                    "Hệ thống", currentDisplayName + " đã " + (newRole ? "thăng cấp Quản trị viên" : "gỡ quyền Quản trị viên") + " đối với " + gm.getDisplayName(),
                    "Vừa xong", false
            ));

            renderMembersList();
            if (listener != null) listener.onGroupUpdated(conversation);
        }
    }

    private void handleRemoveMember(ChatDataModels.GroupMember gm) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa \"" + gm.getDisplayName() + "\" khỏi nhóm?",
                "Xác nhận xóa thành viên",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            conversation.removeGroupMember(gm.getUsername());
            conversation.getMessages().add(new ChatDataModels.ChatMessage(
                    "Hệ thống", currentDisplayName + " đã xóa " + gm.getDisplayName() + " khỏi nhóm chat.",
                    "Vừa xong", false
            ));

            renderMembersList();
            if (listener != null) listener.onGroupUpdated(conversation);
        }
    }

    private void openAddMemberDialog() {
        // Lọc danh sách bạn bè chưa có trong nhóm
        List<String> unjoined = new ArrayList<>();
        List<ChatDataModels.GroupMember> currentMembers = conversation.getGroupMembers();

        for (String friend : allAvailableFriends) {
            boolean exists = false;
            for (ChatDataModels.GroupMember gm : currentMembers) {
                if (friend.contains(gm.getDisplayName()) || friend.contains(gm.getUsername())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                unjoined.add(friend);
            }
        }

        AddGroupMemberDialog dialog = new AddGroupMemberDialog(this, unjoined, newMembers -> {
            for (String f : newMembers) {
                String cleanName = f.replaceAll("\\(.*?\\)", "").trim();
                String userTag = cleanName.toLowerCase().replace(" ", "_");
                ChatDataModels.GroupMember newGm = new ChatDataModels.GroupMember(userTag, cleanName, false);
                conversation.addGroupMember(newGm);
            }

            conversation.getMessages().add(new ChatDataModels.ChatMessage(
                    "Hệ thống", currentDisplayName + " đã thêm " + newMembers.size() + " thành viên mới vào nhóm.",
                    "Vừa xong", false
            ));

            renderMembersList();
            if (listener != null) listener.onGroupUpdated(conversation);
        });
        dialog.setVisible(true);
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER));
        footer.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER),
                new EmptyBorder(10, 16, 10, 16)
        ));

        JButton btnLeave = UserUITheme.createDangerButton("Rời nhóm");
        btnLeave.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnLeave.setBorder(new EmptyBorder(8, 14, 8, 14));
        btnLeave.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn rời khỏi nhóm \"" + conversation.getTitle() + "\"?",
                    "Rời nhóm chat",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (listener != null) {
                    listener.onLeaveGroup(conversation);
                }
                dispose();
            }
        });
        footer.add(btnLeave, BorderLayout.WEST);

        JButton btnClose = UserUITheme.createSecondaryButton("Đóng");
        btnClose.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnClose.setBorder(new EmptyBorder(8, 16, 8, 16));
        btnClose.addActionListener(e -> dispose());
        footer.add(btnClose, BorderLayout.EAST);

        return footer;
    }
}

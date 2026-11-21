package user;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.util.List;

/**
 * Khung ứng dụng chính dành cho Người dùng (UserMainFrame)
 * Bố cục 3 cột hiện đại kết hợp BorderLayout và JSplitPane:
 * - Cột 1 (Sidebar trái): Header người dùng (Avatar, Tên, Online, Bánh răng), Thanh tìm kiếm nhanh, Thanh 5 Tab chuyển đổi.
 * - Cột 2 (Giữa): Khung trò chuyện chính (ChatAreaPanel) với lịch sử tin nhắn, gợi ý AI LLM, thanh nhập liệu.
 * - Cột 3 (Phải): Thông tin chi tiết cá nhân / nhóm, danh sách thành viên, mã hóa và tùy chọn bảo mật (ChatDetailsPanel).
 */
public class UserMainFrame extends JFrame {

    private final String currentUsername;
    private final String currentDisplayName;

    // 3 Cột chính
    private UserSidebar sidebar;
    private ChatAreaPanel chatArea;
    private ChatDetailsPanel chatDetails;

    // SplitPanes điều khiển co giãn
    private JSplitPane mainSplit;
    private JSplitPane rightSplit;
    private boolean isDetailsVisible = true;

    public UserMainFrame() {
        this("nguyen_van_a", "Nguyễn Văn An");
    }

    public UserMainFrame(String username) {
        this(username, "Nguyễn Văn An");
    }

    public UserMainFrame(String username, String displayName) {
        super("ChatConnect - Ứng dụng Trò chuyện Trực tuyến");
        this.currentUsername = (username != null && !username.trim().isEmpty()) ? username : "nguyen_van_a";
        this.currentDisplayName = (displayName != null && !displayName.trim().isEmpty()) ? displayName : "Nguyễn Văn An";

        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Áp dụng theme và font chuẩn Segoe UI / Arial
        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // =====================================================================
        // 1. KHỞI TẠO CỘT 1: SIDEBAR TRÁI (UserSidebar)
        // =====================================================================
        sidebar = new UserSidebar(currentUsername, currentDisplayName, new UserSidebar.SidebarListener() {
            @Override
            public void onConversationSelected(ChatDataModels.Conversation conv) {
                chatArea.setConversation(conv);
                chatDetails.setConversation(conv);
            }

            @Override
            public void onOpenProfile() {
                openProfileDialog();
            }

            @Override
            public void onChangePassword() {
                openChangePasswordDialog();
            }

            @Override
            public void onLogout() {
                handleLogout();
            }

            @Override
            public void onCreateGroupRequested() {
                openCreateGroupDialog();
            }

            @Override
            public void onGlobalSearchRequested() {
                openGlobalSearchDialog();
            }
        });

        // =====================================================================
        // 2. KHỞI TẠO CỘT 2: KHUNG CHAT CHÍNH (ChatAreaPanel)
        // =====================================================================
        chatArea = new ChatAreaPanel(new ChatAreaPanel.ChatAreaListener() {
            @Override
            public void onMessageSent(ChatDataModels.Conversation conversation, String message) {
                // Đồng bộ cập nhật đoạn chat gần nhất ở Sidebar
                sidebar.getChatsPanel().updateLastMessage(conversation.getId(), "Bạn: " + message, "Vừa xong");
            }

            @Override
            public void onToggleDetails() {
                toggleDetailsColumn();
            }

            @Override
            public void onSpamReported(ChatDataModels.Conversation conversation) {
                openSpamReportDialog(conversation);
            }
        });

        // =====================================================================
        // 3. KHỞI TẠO CỘT 3: THÔNG TIN CHI TIẾT (ChatDetailsPanel)
        // =====================================================================
        chatDetails = new ChatDetailsPanel(new ChatDetailsPanel.DetailsListener() {
            @Override
            public void onCloseDetails() {
                toggleDetailsColumn();
            }

            @Override
            public void onManageGroup(ChatDataModels.Conversation conversation) {
                openGroupManagementDialog(conversation);
            }

            @Override
            public void onBlockUser(String targetName) {
                int confirm = JOptionPane.showConfirmDialog(
                        UserMainFrame.this,
                        "Bạn có chắc chắn muốn chặn " + targetName + "?",
                        "Xác nhận chặn",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    sidebar.getFriendsPanel().removeFriend(targetName);
                    sidebar.getOnlinePanel().removeOnline(targetName);
                    JOptionPane.showMessageDialog(UserMainFrame.this, "Đã chặn @" + targetName + " thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            @Override
            public void onReportSpam(ChatDataModels.Conversation conversation) {
                openSpamReportDialog(conversation);
            }

            @Override
            public void onClearHistory(ChatDataModels.Conversation conversation) {
                conversation.getMessages().clear();
                conversation.setLastMessage("(Đã xóa toàn bộ lịch sử trò chuyện)");
                chatArea.setConversation(conversation);
                sidebar.getChatsPanel().updateLastMessage(conversation.getId(), "(Đã xóa toàn bộ tin nhắn)", "Vừa xong");
            }
        });

        // =====================================================================
        // 4. KẾT HỢP BỐ CỤC 3 CỘT BẰNG JSPLITPANE TINH TẾ
        // =====================================================================
        // SplitPane phụ bên phải: Cột 2 (ChatArea) + Cột 3 (ChatDetails)
        rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatArea, chatDetails);
        setupSplitPane(rightSplit);
        rightSplit.setResizeWeight(1.0); // Khung chat tự động co giãn, Cột chi tiết giữ kích thước chuẩn 320px

        // SplitPane chính: Cột 1 (Sidebar) + SplitPane phụ
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, rightSplit);
        setupSplitPane(mainSplit);
        mainSplit.setResizeWeight(0.0); // Sidebar giữ độ rộng cố định khi phóng to màn hình

        add(mainSplit, BorderLayout.CENTER);

        // Đăng ký phím tắt toàn cục: Ctrl+Shift+F và Ctrl+K để mở Tìm kiếm toàn cục
        KeyStroke ctrlShiftF = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK);
        getRootPane().registerKeyboardAction(e -> openGlobalSearchDialog(), ctrlShiftF, JComponent.WHEN_IN_FOCUSED_WINDOW);

        KeyStroke ctrlK = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        getRootPane().registerKeyboardAction(e -> openGlobalSearchDialog(), ctrlK, JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Lắng nghe thay đổi kích thước cửa sổ để giữ đúng tỷ lệ các cột
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                applyDividers();
            }

            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                applyDividers();
            }
        });

        // Nạp cuộc trò chuyện đầu tiên mặc định
        SwingUtilities.invokeLater(() -> {
            applyDividers();

            ChatDataModels.Conversation defaultConv = sidebar.getChatsPanel().getSelectedConversation();
            if (defaultConv != null) {
                chatArea.setConversation(defaultConv);
                chatDetails.setConversation(defaultConv);
            }
        });
    }

    private void applyDividers() {
        mainSplit.setDividerLocation(340);
        if (isDetailsVisible) {
            int rightWidth = rightSplit.getWidth();
            if (rightWidth >= 650) {
                rightSplit.setDividerLocation(rightWidth - 320);
            } else if (rightWidth > 0) {
                rightSplit.setDividerLocation((int)(rightWidth * 0.65));
            }
        }
    }

    /**
     * Tinh chỉnh thanh trượt JSplitPane hiện đại, phẳng và mảnh
     */
    private void setupSplitPane(JSplitPane splitPane) {
        splitPane.setBorder(null);
        splitPane.setDividerSize(4);
        splitPane.setContinuousLayout(true);
        splitPane.setOpaque(false);

        splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(UserUITheme.COLOR_BORDER);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        });
    }

    /**
     * Ẩn / Hiện Cột 3 bên phải
     */
    private void toggleDetailsColumn() {
        isDetailsVisible = !isDetailsVisible;
        chatDetails.setVisible(isDetailsVisible);
        if (isDetailsVisible) {
            rightSplit.setDividerLocation(rightSplit.getWidth() - 320);
            rightSplit.setDividerSize(4);
        } else {
            rightSplit.setDividerSize(0);
        }
        rightSplit.revalidate();
        rightSplit.repaint();
    }

    /**
     * Mở ProfileDialog (Cập nhật thông tin tài khoản)
     */
    public void openProfileDialog() {
        ProfileDialog dialog = new ProfileDialog(
                this, currentUsername, currentDisplayName, currentUsername + "@gmail.com",
                "227 Nguyễn Văn Cừ, Quận 5, TP.HCM", 15, 8, 2000, "Nam"
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            sidebar.updateDisplayName(dialog.getUpdatedFullName());
        }
    }

    /**
     * Mở ChangePasswordDialog (Đổi mật khẩu)
     */
    public void openChangePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this, currentUsername);
        dialog.setVisible(true);
    }

    /**
     * Mở CreateGroupDialog (Tạo nhóm chat mới)
     */
    public void openCreateGroupDialog() {
        List<String> friends = sidebar.getFriendsPanel().getFriendDisplayList();
        CreateGroupDialog dialog = new CreateGroupDialog(this, friends, (groupName, members, encrypted) -> {
            String newId = "chat_group_" + System.currentTimeMillis();
            ChatDataModels.Conversation newGroup = new ChatDataModels.Conversation(
                    newId, groupName, members.size() + 1 + " thành viên",
                    "Bạn đã tạo nhóm mới", "Vừa xong", 0, true, true, encrypted
            );
            // Người tạo nhóm là Admin mặc định
            newGroup.addGroupMember(new ChatDataModels.GroupMember(currentUsername, currentDisplayName, true));
            for (String m : members) {
                String cleanName = m.replaceAll("\\(.*?\\)", "").trim();
                String userTag = cleanName.toLowerCase().replace(" ", "_");
                newGroup.addGroupMember(new ChatDataModels.GroupMember(userTag, cleanName, false));
            }
            newGroup.getMessages().add(new ChatDataModels.ChatMessage(
                    "Hệ thống", "Nhóm \"" + groupName + "\" đã được tạo thành công." + (encrypted ? " Cuộc trò chuyện được mã hóa đầu cuối." : ""),
                    "Vừa xong", false
            ));

            sidebar.getChatsPanel().addConversation(newGroup);
            chatArea.setConversation(newGroup);
            chatDetails.setConversation(newGroup);
        });
        dialog.setVisible(true);
    }

    /**
     * Mở GroupManagementDialog (Quản lý nhóm chat & phân quyền)
     */
    public void openGroupManagementDialog(ChatDataModels.Conversation group) {
        if (group == null || !group.isGroup()) return;
        List<String> friends = sidebar.getFriendsPanel().getFriendDisplayList();
        GroupManagementDialog dialog = new GroupManagementDialog(
                this, group, currentUsername, currentDisplayName, friends,
                new GroupManagementDialog.GroupManagementListener() {
                    @Override
                    public void onGroupUpdated(ChatDataModels.Conversation updated) {
                        chatArea.setConversation(updated);
                        chatDetails.setConversation(updated);
                        sidebar.getChatsPanel().refreshConversationList();
                    }

                    @Override
                    public void onLeaveGroup(ChatDataModels.Conversation leftGroup) {
                        leftGroup.removeGroupMember(currentUsername);
                        leftGroup.removeGroupMember(currentDisplayName);
                        sidebar.getChatsPanel().refreshConversationList();
                        ChatDataModels.Conversation defaultConv = sidebar.getChatsPanel().getSelectedConversation();
                        if (defaultConv != null) {
                            chatArea.setConversation(defaultConv);
                            chatDetails.setConversation(defaultConv);
                        }
                    }
                }
        );
        dialog.setVisible(true);
    }

    /**
     * Mở SpamReportDialog (Báo cáo vi phạm / Spam)
     */
    public void openSpamReportDialog(ChatDataModels.Conversation conv) {
        if (conv == null) return;
        SpamReportDialog dialog = new SpamReportDialog(this, conv, currentUsername, report -> {
            if (report.isBlocked()) {
                sidebar.getFriendsPanel().removeFriend(conv.getTitle());
                sidebar.getOnlinePanel().removeOnline(conv.getTitle());
            }
            System.out.println("[SPAM REPORT] Đã ghi nhận báo cáo spam đối với: " + report.getTargetName()
                    + " | Lý do: " + report.getReason() + " | Người báo cáo: " + report.getReporter());
        });
        dialog.setVisible(true);
    }

    /**
     * Mở GlobalSearchDialog (Tìm kiếm chuỗi tin nhắn trên toàn bộ các cuộc trò chuyện)
     */
    public void openGlobalSearchDialog() {
        List<ChatDataModels.Conversation> allConvs = sidebar.getChatsPanel().getAllConversations();
        GlobalSearchDialog dialog = new GlobalSearchDialog(this, allConvs, (conversation, message, messageIndex) -> {
            sidebar.getChatsPanel().selectConversation(conversation.getId());
            chatArea.setConversation(conversation);
            chatDetails.setConversation(conversation);
            chatArea.scrollToAndHighlightMessage(messageIndex);
        });
        dialog.setVisible(true);
    }

    /**
     * Xử lý Đăng xuất -> Quay lại màn hình LoginFrame
     */
    private void handleLogout() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }

    // =========================================================================
    // GETTERS
    // =========================================================================
    public UserSidebar getSidebar() { return sidebar; }
    public ChatAreaPanel getChatArea() { return chatArea; }
    public ChatDetailsPanel getChatDetails() { return chatDetails; }
    public JSplitPane getMainSplit() { return mainSplit; }
    public JSplitPane getRightSplit() { return rightSplit; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserMainFrame frame = new UserMainFrame();
            frame.setVisible(true);
        });
    }
}

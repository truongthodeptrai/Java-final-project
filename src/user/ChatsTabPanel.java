package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab 1: Danh sách các đoạn chat gần đây (ChatsTabPanel)
 * Quản lý danh sách hội thoại cá nhân và nhóm, hiển thị tin nhắn cuối,
 * thời gian, huy hiệu tin chưa đọc và nút Tạo nhóm mới.
 */
public class ChatsTabPanel extends JPanel {

    private final UserSidebar sidebar;
    private final JPanel listContainer;
    private final List<ChatDataModels.Conversation> conversations = new ArrayList<>();
    private final List<ConversationItemPanel> itemPanels = new ArrayList<>();
    private String selectedConversationId = null;

    public ChatsTabPanel(UserSidebar sidebar) {
        this.sidebar = sidebar;
        setLayout(new BorderLayout());
        setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        // Sub-header: Tiêu đề và Nút "+ Tạo nhóm"
        JPanel subHeader = new JPanel(new BorderLayout());
        subHeader.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        subHeader.setBorder(new EmptyBorder(10, 14, 8, 14));

        JLabel lblTitle = new JLabel("Hội thoại gần đây");
        lblTitle.setFont(UserUITheme.FONT_BODY_BOLD);
        lblTitle.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        subHeader.add(lblTitle, BorderLayout.WEST);

        JButton btnNewGroup = new JButton("+ Tạo nhóm");
        btnNewGroup.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnNewGroup.setForeground(UserUITheme.COLOR_PRIMARY);
        btnNewGroup.setBackground(new Color(235, 243, 255));
        btnNewGroup.setFocusPainted(false);
        btnNewGroup.setBorderPainted(false);
        btnNewGroup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNewGroup.setBorder(new EmptyBorder(5, 10, 5, 10));
        btnNewGroup.addActionListener(e -> {
            if (sidebar != null) sidebar.notifyCreateGroupRequested();
        });
        subHeader.add(btnNewGroup, BorderLayout.EAST);

        add(subHeader, BorderLayout.NORTH);

        // Container danh sách cuộc trò chuyện
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        initMockConversations();
        renderList();

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initMockConversations() {
        ChatDataModels.Conversation c1 = new ChatDataModels.Conversation(
                "chat_group_1", "Nhóm Đồ án Java", "5 thành viên",
                "Trần Mai: Chiều nay 15h họp online hoàn thiện giao diện nhé!", "14:30", 2, true, true, true
        );
        c1.addGroupMember(new ChatDataModels.GroupMember("nguyen_van_a", "Nguyễn Văn An", true));
        c1.addGroupMember(new ChatDataModels.GroupMember("tran_mai", "Trần Thị Mai", true));
        c1.addGroupMember(new ChatDataModels.GroupMember("nam_le", "Lê Hoàng Nam", false));
        c1.addGroupMember(new ChatDataModels.GroupMember("tuan_pm", "Phạm Minh Tuấn", false));
        c1.addGroupMember(new ChatDataModels.GroupMember("hong_vo", "Võ Thị Hồng", false));

        c1.getMessages().add(new ChatDataModels.ChatMessage("Trần Thị Mai", "Chào cả nhóm! Mọi người đã xem xong file đặc tả đồ án chưa?", "14:15", false));
        c1.getMessages().add(new ChatDataModels.ChatMessage("Lê Hoàng Nam", "Mình xem rồi, phần giao diện người dùng yêu cầu 3 cột và 5 tab.", "14:20", false));
        c1.getMessages().add(new ChatDataModels.ChatMessage("Nguyễn Văn An", "Mình đang tiến hành xây dựng UserMainFrame và Sidebar nhé.", "14:25", true));
        c1.getMessages().add(new ChatDataModels.ChatMessage("Trần Thị Mai", "Chiều nay 15h họp online hoàn thiện giao diện nhé!", "14:30", false));

        ChatDataModels.Conversation c2 = new ChatDataModels.Conversation(
                "chat_friend_1", "Trần Thị Mai", "@tran_mai",
                "Bạn: Mình vừa cập nhật mã nguồn mới nhất lên rồi nhé", "13:45", 0, false, true, false
        );
        c2.setEmail("tranmai@gmail.com");
        c2.setAddress("TP. Hồ Chí Minh");
        c2.getMessages().add(new ChatDataModels.ChatMessage("Trần Thị Mai", "An ơi, cậu làm xong phần đổi mật khẩu chưa?", "13:30", false));
        c2.getMessages().add(new ChatDataModels.ChatMessage("Nguyễn Văn An", "Mình vừa cập nhật mã nguồn mới nhất lên rồi nhé", "13:45", true));

        ChatDataModels.Conversation c3 = new ChatDataModels.Conversation(
                "chat_friend_2", "Lê Hoàng Nam", "@nam_le",
                "Nam: Nhớ kiểm tra lại danh sách bạn online giúp mình", "11:10", 1, false, false, false
        );
        c3.setEmail("namle@gmail.com");
        c3.setAddress("Hà Nội");
        c3.getMessages().add(new ChatDataModels.ChatMessage("Lê Hoàng Nam", "Nhớ kiểm tra lại danh sách bạn online giúp mình", "11:10", false));

        ChatDataModels.Conversation c4 = new ChatDataModels.Conversation(
                "chat_friend_3", "Phạm Minh Tuấn", "@tuan_pm",
                "Tuấn: Cảm ơn bạn rất nhiều!", "Hôm qua", 0, false, true, false
        );
        c4.setEmail("tuanpm@gmail.com");
        c4.setAddress("Đà Nẵng");
        c4.getMessages().add(new ChatDataModels.ChatMessage("Phạm Minh Tuấn", "Cảm ơn bạn rất nhiều!", "Hôm qua", false));

        conversations.add(c1);
        conversations.add(c2);
        conversations.add(c3);
        conversations.add(c4);

        selectedConversationId = c1.getId();
    }

    private void renderList() {
        listContainer.removeAll();
        itemPanels.clear();

        for (ChatDataModels.Conversation c : conversations) {
            ConversationItemPanel item = new ConversationItemPanel(c);
            itemPanels.add(item);
            listContainer.add(item);
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    public void filter(String query) {
        listContainer.removeAll();
        for (ConversationItemPanel item : itemPanels) {
            if (query.isEmpty() || item.conversation.getTitle().toLowerCase().contains(query.toLowerCase())
                    || item.conversation.getLastMessage().toLowerCase().contains(query.toLowerCase())) {
                listContainer.add(item);
            }
        }
        listContainer.revalidate();
        listContainer.repaint();
    }

    public void selectConversation(String id) {
        selectedConversationId = id;
        for (ConversationItemPanel item : itemPanels) {
            item.setSelected(item.conversation.getId().equals(id));
        }
    }

    public void addConversation(ChatDataModels.Conversation newConv) {
        conversations.add(0, newConv);
        renderList();
        selectConversation(newConv.getId());
    }

    public void updateLastMessage(String convId, String message, String time) {
        for (ChatDataModels.Conversation c : conversations) {
            if (c.getId().equals(convId)) {
                c.setLastMessage(message);
                c.setLastMessageTime(time);
                break;
            }
        }
        renderList();
        selectConversation(convId);
    }

    public ChatDataModels.Conversation getConversation(String id) {
        for (ChatDataModels.Conversation c : conversations) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    public ChatDataModels.Conversation getSelectedConversation() {
        return getConversation(selectedConversationId);
    }

    public List<ChatDataModels.Conversation> getAllConversations() {
        return conversations;
    }

    public void refreshConversationList() {
        renderList();
    }

    /**
     * Từng dòng mục cuộc trò chuyện được tinh chỉnh hiện đại, gọn gàng, nền xanh nhạt khi chọn
     */
    private class ConversationItemPanel extends JPanel {
        private final ChatDataModels.Conversation conversation;
        private boolean isHover = false;
        private boolean isSelected = false;

        public ConversationItemPanel(ChatDataModels.Conversation conv) {
            this.conversation = conv;
            this.isSelected = conv.getId().equals(selectedConversationId);

            setLayout(new BorderLayout(12, 0));
            setOpaque(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(300, 58));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

            updateAppearance();

            // Left: Avatar nhỏ gọn, tinh tế 40px
            AvatarComponent avt = new AvatarComponent(
                    conv.getTitle(), 40, conv.getAvatarColor(), conv.isGroup(),
                    conv.isGroup() ? null : conv.isOnline()
            );
            add(avt, BorderLayout.WEST);

            // Center: Tiêu đề và nội dung tin nhắn giới hạn 1 dòng kèm dấu ...
            JPanel centerBox = new JPanel(new GridLayout(2, 1, 0, 2));
            centerBox.setOpaque(false);

            JLabel lblName = new JLabel(conv.getTitle());
            lblName.setFont(conv.getUnreadCount() > 0 ? UserUITheme.FONT_BODY_BOLD : UserUITheme.FONT_BODY);
            lblName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);

            String rawMsg = conv.getLastMessage() != null ? conv.getLastMessage() : "";
            String previewMsg = rawMsg;
            if (previewMsg.length() > 34) {
                previewMsg = previewMsg.substring(0, 31) + "...";
            }
            JLabel lblMsg = new JLabel(previewMsg);
            lblMsg.setFont(UserUITheme.FONT_SMALL);
            lblMsg.setForeground(conv.getUnreadCount() > 0 ? UserUITheme.COLOR_TEXT_PRIMARY : UserUITheme.COLOR_TEXT_SECONDARY);
            lblMsg.setToolTipText(rawMsg);

            centerBox.add(lblName);
            centerBox.add(lblMsg);
            add(centerBox, BorderLayout.CENTER);

            // Right: Thời gian gửi tin nhắn gần nhất và Huy hiệu tin chưa đọc
            JPanel rightBox = new JPanel();
            rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.Y_AXIS));
            rightBox.setOpaque(false);

            JLabel lblTime = new JLabel(conv.getLastMessageTime());
            lblTime.setFont(UserUITheme.FONT_TIMESTAMP);
            lblTime.setForeground(conv.getUnreadCount() > 0 ? UserUITheme.COLOR_PRIMARY : UserUITheme.COLOR_TEXT_MUTED);
            lblTime.setAlignmentX(Component.RIGHT_ALIGNMENT);
            rightBox.add(lblTime);

            if (conv.getUnreadCount() > 0) {
                rightBox.add(Box.createVerticalStrut(3));
                JLabel lblBadge = new JLabel(String.valueOf(conv.getUnreadCount())) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        UserUITheme.enableAntialiasing(g2);
                        g2.setColor(UserUITheme.COLOR_PRIMARY);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                lblBadge.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 10));
                lblBadge.setForeground(Color.WHITE);
                lblBadge.setHorizontalAlignment(SwingConstants.CENTER);
                lblBadge.setPreferredSize(new Dimension(18, 18));
                lblBadge.setMaximumSize(new Dimension(18, 18));
                lblBadge.setAlignmentX(Component.RIGHT_ALIGNMENT);
                rightBox.add(lblBadge);
            }

            add(rightBox, BorderLayout.EAST);

            // Sự kiện hover và click chuột
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHover = true;
                    updateAppearance();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHover = false;
                    updateAppearance();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    selectConversation(conv.getId());
                    conv.setUnreadCount(0);
                    if (sidebar != null) {
                        sidebar.notifyConversationSelected(conv);
                    }
                }
            });
        }

        private void updateAppearance() {
            if (isSelected) {
                setBackground(new Color(239, 246, 255)); // Nền xanh nhạt dịu mắt, đồng bộ toàn hệ thống
                setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 1, 0, UserUITheme.COLOR_PRIMARY), // Điểm nhấn viền xanh trái
                        new EmptyBorder(8, 11, 8, 14)
                ));
            } else if (isHover) {
                setBackground(new Color(248, 250, 252));
                setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)),
                        new EmptyBorder(8, 14, 8, 14)
                ));
            } else {
                setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
                setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)),
                        new EmptyBorder(8, 14, 8, 14)
                ));
            }
            repaint();
        }

        public void setSelected(boolean sel) {
            this.isSelected = sel;
            updateAppearance();
        }
    }
}

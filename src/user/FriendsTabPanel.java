package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab 2: Danh sách bạn bè (FriendsTabPanel)
 * Quản lý danh sách bạn bè với ô lọc theo tên, trạng thái trực tuyến/ngoại tuyến,
 * và các nút thao tác nhanh: Nhắn tin (Chat), Hủy kết bạn & Chặn tài khoản (Block).
 */
public class FriendsTabPanel extends JPanel {

    private final UserSidebar sidebar;
    private final JPanel listContainer;
    private final List<ChatDataModels.Friend> friends = new ArrayList<>();
    private final List<FriendItemPanel> itemPanels = new ArrayList<>();
    private final JLabel lblCount;

    public FriendsTabPanel(UserSidebar sidebar) {
        this.sidebar = sidebar;
        setLayout(new BorderLayout());
        setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        // Sub-header đếm số lượng bạn bè & ô lọc theo tên
        JPanel subHeader = new JPanel();
        subHeader.setLayout(new BoxLayout(subHeader, BoxLayout.Y_AXIS));
        subHeader.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        subHeader.setBorder(new EmptyBorder(10, 14, 8, 14));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        lblCount = new JLabel("Tất cả bạn bè (6)");
        lblCount.setFont(UserUITheme.FONT_BODY_BOLD);
        lblCount.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        titleRow.add(lblCount, BorderLayout.WEST);

        JButton btnAddFriend = new JButton("+ Thêm bạn");
        btnAddFriend.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnAddFriend.setForeground(UserUITheme.COLOR_PRIMARY);
        btnAddFriend.setBackground(new Color(235, 243, 255));
        btnAddFriend.setFocusPainted(false);
        btnAddFriend.setBorderPainted(false);
        btnAddFriend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddFriend.setBorder(new EmptyBorder(4, 10, 4, 10));
        btnAddFriend.setToolTipText("Tìm kiếm người dùng và gửi lời mời kết bạn (Mục 6b)");
        btnAddFriend.addActionListener(e -> {
            if (sidebar != null) sidebar.selectTab(4); // Chuyển sang Tab Tìm kiếm (Search)
        });
        titleRow.add(btnAddFriend, BorderLayout.EAST);
        subHeader.add(titleRow);
        subHeader.add(Box.createVerticalStrut(6));

        JTextField txtFilterFriends = UserUITheme.createTextField("Lọc bạn bè theo tên...");
        txtFilterFriends.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtFilterFriends.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(txtFilterFriends.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(txtFilterFriends.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(txtFilterFriends.getText()); }
        });
        subHeader.add(txtFilterFriends);

        add(subHeader, BorderLayout.NORTH);

        // Container danh sách
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        initMockFriends();
        renderList();

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initMockFriends() {
        friends.add(new ChatDataModels.Friend("tran_mai", "Trần Thị Mai", "tranmai@gmail.com", true, "Đang hoạt động"));
        friends.add(new ChatDataModels.Friend("nam_le", "Lê Hoàng Nam", "namle@gmail.com", false, "Hoạt động 2 giờ trước"));
        friends.add(new ChatDataModels.Friend("tuan_pm", "Phạm Minh Tuấn", "tuanpm@gmail.com", true, "Đang hoạt động"));
        friends.add(new ChatDataModels.Friend("hong_vo", "Võ Thị Hồng", "hongvo@gmail.com", true, "Đang hoạt động"));
        friends.add(new ChatDataModels.Friend("huy_dq", "Đặng Quốc Huy", "huydq@gmail.com", false, "Hoạt động hôm qua"));
        friends.add(new ChatDataModels.Friend("phuong_nb", "Nguyễn Bích Phương", "phuongnb@gmail.com", false, "Hoạt động 3 ngày trước"));
    }

    private void renderList() {
        listContainer.removeAll();
        itemPanels.clear();

        for (ChatDataModels.Friend f : friends) {
            FriendItemPanel item = new FriendItemPanel(f);
            itemPanels.add(item);
            listContainer.add(item);
        }

        lblCount.setText("Tất cả bạn bè (" + friends.size() + ")");
        listContainer.revalidate();
        listContainer.repaint();
    }

    public void filter(String query) {
        listContainer.removeAll();
        String q = query != null ? query.trim() : "";
        if (q.equals("Lọc bạn bè theo tên...")) q = "";

        for (FriendItemPanel item : itemPanels) {
            if (q.isEmpty() || item.friend.getFullName().toLowerCase().contains(q.toLowerCase())
                    || item.friend.getUsername().toLowerCase().contains(q.toLowerCase())) {
                listContainer.add(item);
            }
        }
        listContainer.revalidate();
        listContainer.repaint();
    }

    public void addFriend(ChatDataModels.Friend f) {
        friends.add(0, f);
        renderList();
    }

    public void removeFriend(String username) {
        friends.removeIf(f -> f.getUsername().equalsIgnoreCase(username));
        renderList();
    }

    public List<String> getFriendDisplayList() {
        List<String> list = new ArrayList<>();
        for (ChatDataModels.Friend f : friends) {
            list.add(f.getFullName() + " (@" + f.getUsername() + ")");
        }
        return list;
    }

    /**
     * Từng dòng mục bạn bè
     */
    private class FriendItemPanel extends JPanel {
        private final ChatDataModels.Friend friend;

        public FriendItemPanel(ChatDataModels.Friend friend) {
            this.friend = friend;
            setLayout(new BorderLayout(10, 0));
            setOpaque(true);
            setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
            setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 243, 248)),
                    new EmptyBorder(10, 14, 10, 14)
            ));

            // Left: Avatar
            AvatarComponent avt = new AvatarComponent(friend.getFullName(), 38, friend.getAvatarColor(), false, friend.isOnline());
            add(avt, BorderLayout.WEST);

            // Center: Tên, Username và Trạng thái
            JPanel centerBox = new JPanel(new GridLayout(2, 1, 0, 2));
            centerBox.setOpaque(false);

            JLabel lblName = new JLabel(friend.getFullName());
            lblName.setFont(UserUITheme.FONT_BODY_BOLD);
            lblName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);

            JPanel subStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            subStatus.setOpaque(false);

            JLabel dot = new JLabel("●");
            dot.setFont(new Font("Arial", Font.PLAIN, 9));
            dot.setForeground(friend.isOnline() ? UserUITheme.COLOR_STATUS_ONLINE : UserUITheme.COLOR_STATUS_OFFLINE);

            JLabel lblSub = new JLabel(friend.getStatusText());
            lblSub.setFont(UserUITheme.FONT_SMALL);
            lblSub.setForeground(friend.isOnline() ? UserUITheme.COLOR_STATUS_ONLINE : UserUITheme.COLOR_TEXT_MUTED);

            subStatus.add(dot);
            subStatus.add(lblSub);

            centerBox.add(lblName);
            centerBox.add(subStatus);
            add(centerBox, BorderLayout.CENTER);

            // Right: 3 Nút thao tác (Chat, Hủy bạn, Block)
            JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
            actionBox.setOpaque(false);

            // 1. Nút Chat
            JButton btnChat = new JButton("Chat");
            btnChat.setFont(UserUITheme.FONT_SMALL_BOLD);
            btnChat.setToolTipText("Nhắn tin với " + friend.getFullName());
            btnChat.setForeground(UserUITheme.COLOR_PRIMARY);
            btnChat.setBackground(new Color(235, 243, 255));
            btnChat.setFocusPainted(false);
            btnChat.setBorderPainted(false);
            btnChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnChat.setBorder(new EmptyBorder(4, 6, 4, 6));
            btnChat.addActionListener(e -> {
                if (sidebar != null) sidebar.startChatWithFriend(friend);
            });

            // 2. Nút Hủy kết bạn
            JButton btnUnfriend = new JButton("Hủy bạn");
            btnUnfriend.setFont(UserUITheme.FONT_SMALL);
            btnUnfriend.setToolTipText("Hủy kết bạn");
            btnUnfriend.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
            btnUnfriend.setBackground(new Color(245, 247, 250));
            btnUnfriend.setFocusPainted(false);
            btnUnfriend.setBorderPainted(false);
            btnUnfriend.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnUnfriend.setBorder(new EmptyBorder(4, 5, 4, 5));
            btnUnfriend.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc chắn muốn hủy kết bạn với " + friend.getFullName() + " (@" + friend.getUsername() + ")?",
                        "Xác nhận hủy kết bạn",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    removeFriend(friend.getUsername());
                    JOptionPane.showMessageDialog(this, "Đã hủy kết bạn với @" + friend.getUsername(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            // 3. Nút Block (Chặn)
            JButton btnBlock = new JButton("Chặn");
            btnBlock.setFont(UserUITheme.FONT_SMALL_BOLD);
            btnBlock.setToolTipText("Chặn tài khoản (Hủy kết bạn & chặn vĩnh viễn)");
            btnBlock.setForeground(Color.WHITE);
            btnBlock.setBackground(UserUITheme.COLOR_DANGER);
            btnBlock.setFocusPainted(false);
            btnBlock.setBorderPainted(false);
            btnBlock.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnBlock.setBorder(new EmptyBorder(4, 6, 4, 6));
            btnBlock.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Chặn người dùng " + friend.getFullName() + " (@" + friend.getUsername() + ")?\n\n"
                                + "Hành động này sẽ hủy kết bạn và người này sẽ KHÔNG THỂ\n"
                                + "tìm thấy hay gửi lời mời kết bạn lại cho bạn.",
                        "Xác nhận chặn tài khoản",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    removeFriend(friend.getUsername());
                    if (sidebar != null && sidebar.getOnlinePanel() != null) {
                        sidebar.getOnlinePanel().removeOnline(friend.getUsername());
                    }
                    JOptionPane.showMessageDialog(this, "Đã chặn người dùng @" + friend.getUsername() + " thành công!", "Đã chặn", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            actionBox.add(btnChat);
            actionBox.add(btnUnfriend);
            actionBox.add(btnBlock);
            add(actionBox, BorderLayout.EAST);
        }
    }
}

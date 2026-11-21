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
 * Tab 3: Danh sách bạn bè đang online (OnlineTabPanel)
 * Hiển thị bạn bè đang hoạt động với chấm tròn trực tuyến xanh lá,
 * hỗ trợ tìm kiếm nhanh, nút nhắn tin ngay và nút tạo nhóm trò chuyện.
 */
public class OnlineTabPanel extends JPanel {

    private final UserSidebar sidebar;
    private final JPanel listContainer;
    private final List<ChatDataModels.Friend> onlineFriends = new ArrayList<>();
    private final List<OnlineItemPanel> itemPanels = new ArrayList<>();
    private final JLabel lblCount;

    public OnlineTabPanel(UserSidebar sidebar) {
        this.sidebar = sidebar;
        setLayout(new BorderLayout());
        setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        // Sub-header
        JPanel subHeader = new JPanel();
        subHeader.setLayout(new BoxLayout(subHeader, BoxLayout.Y_AXIS));
        subHeader.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        subHeader.setBorder(new EmptyBorder(10, 14, 8, 14));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        lblCount = new JLabel("Đang hoạt động (3)");
        lblCount.setFont(UserUITheme.FONT_BODY_BOLD);
        lblCount.setForeground(UserUITheme.COLOR_STATUS_ONLINE);
        titleRow.add(lblCount, BorderLayout.WEST);

        JButton btnCreateGroup = new JButton("+ Tạo nhóm");
        btnCreateGroup.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnCreateGroup.setForeground(UserUITheme.COLOR_PRIMARY);
        btnCreateGroup.setBackground(new Color(235, 243, 255));
        btnCreateGroup.setFocusPainted(false);
        btnCreateGroup.setBorderPainted(false);
        btnCreateGroup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreateGroup.setBorder(new EmptyBorder(4, 10, 4, 10));
        btnCreateGroup.addActionListener(e -> {
            if (sidebar != null) sidebar.notifyCreateGroupRequested();
        });
        titleRow.add(btnCreateGroup, BorderLayout.EAST);
        subHeader.add(titleRow);
        subHeader.add(Box.createVerticalStrut(6));

        JTextField txtFilterOnline = UserUITheme.createTextField("Lọc bạn online...");
        txtFilterOnline.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtFilterOnline.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(txtFilterOnline.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(txtFilterOnline.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(txtFilterOnline.getText()); }
        });
        subHeader.add(txtFilterOnline);

        add(subHeader, BorderLayout.NORTH);

        // Container danh sách
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        initMockOnlineFriends();
        renderList();

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initMockOnlineFriends() {
        onlineFriends.add(new ChatDataModels.Friend("tran_mai", "Trần Thị Mai", "tranmai@gmail.com", true, "Đang hoạt động"));
        onlineFriends.add(new ChatDataModels.Friend("tuan_pm", "Phạm Minh Tuấn", "tuanpm@gmail.com", true, "Đang hoạt động"));
        onlineFriends.add(new ChatDataModels.Friend("hong_vo", "Võ Thị Hồng", "hongvo@gmail.com", true, "Đang hoạt động"));
    }

    private void renderList() {
        listContainer.removeAll();
        itemPanels.clear();

        for (ChatDataModels.Friend f : onlineFriends) {
            OnlineItemPanel item = new OnlineItemPanel(f);
            itemPanels.add(item);
            listContainer.add(item);
        }

        lblCount.setText("Đang hoạt động (" + onlineFriends.size() + ")");
        listContainer.revalidate();
        listContainer.repaint();
    }

    public void filter(String query) {
        listContainer.removeAll();
        String q = query != null ? query.trim() : "";
        if (q.equals("Lọc bạn online...")) q = "";

        for (OnlineItemPanel item : itemPanels) {
            if (q.isEmpty() || item.friend.getFullName().toLowerCase().contains(q.toLowerCase())
                    || item.friend.getUsername().toLowerCase().contains(q.toLowerCase())) {
                listContainer.add(item);
            }
        }
        listContainer.revalidate();
        listContainer.repaint();
    }

    public void removeOnline(String username) {
        onlineFriends.removeIf(f -> f.getUsername().equalsIgnoreCase(username));
        renderList();
    }

    /**
     * Từng dòng bạn online
     */
    private class OnlineItemPanel extends JPanel {
        private final ChatDataModels.Friend friend;

        public OnlineItemPanel(ChatDataModels.Friend friend) {
            this.friend = friend;
            setLayout(new BorderLayout(10, 0));
            setOpaque(true);
            setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
            setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 243, 248)),
                    new EmptyBorder(10, 14, 10, 14)
            ));

            // Left: Avatar có chấm xanh
            AvatarComponent avt = new AvatarComponent(friend.getFullName(), 42, friend.getAvatarColor(), false, true);
            add(avt, BorderLayout.WEST);

            // Center: Họ tên và username
            JPanel centerBox = new JPanel(new GridLayout(2, 1, 0, 2));
            centerBox.setOpaque(false);

            JLabel lblName = new JLabel(friend.getFullName());
            lblName.setFont(UserUITheme.FONT_BODY_BOLD);
            lblName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);

            JLabel lblSub = new JLabel("● Trực tuyến (@" + friend.getUsername() + ")");
            lblSub.setFont(UserUITheme.FONT_SMALL);
            lblSub.setForeground(UserUITheme.COLOR_STATUS_ONLINE);

            centerBox.add(lblName);
            centerBox.add(lblSub);
            add(centerBox, BorderLayout.CENTER);

            // Right: Nút "Nhắn tin ngay" và "+ Nhóm"
            JPanel btnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            btnBox.setOpaque(false);

            JButton btnChatNow = new JButton("Nhắn tin");
            btnChatNow.setFont(UserUITheme.FONT_SMALL_BOLD);
            btnChatNow.setForeground(Color.WHITE);
            btnChatNow.setBackground(UserUITheme.COLOR_PRIMARY);
            btnChatNow.setFocusPainted(false);
            btnChatNow.setBorderPainted(false);
            btnChatNow.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnChatNow.setBorder(new EmptyBorder(5, 10, 5, 10));
            btnChatNow.addActionListener(e -> {
                if (sidebar != null) sidebar.startChatWithFriend(friend);
            });

            JButton btnAddToGroup = new JButton("+ Nhóm");
            btnAddToGroup.setFont(UserUITheme.FONT_SMALL);
            btnAddToGroup.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            btnAddToGroup.setBackground(new Color(245, 247, 250));
            btnAddToGroup.setFocusPainted(false);
            btnAddToGroup.setBorderPainted(false);
            btnAddToGroup.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnAddToGroup.setBorder(new EmptyBorder(5, 8, 5, 8));
            btnAddToGroup.addActionListener(e -> {
                if (sidebar != null) sidebar.notifyCreateGroupRequested();
            });

            btnBox.add(btnChatNow);
            btnBox.add(btnAddToGroup);
            add(btnBox, BorderLayout.EAST);
        }
    }
}

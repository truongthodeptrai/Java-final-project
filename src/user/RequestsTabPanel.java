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
 * Tab 4: Danh sách lời mời kết bạn chờ duyệt (RequestsTabPanel)
 * Quản lý các yêu cầu kết bạn được gửi đến, hỗ trợ lọc theo tên, hiển thị lời nhắn,
 * thời gian gửi và hai nút thao tác: Đồng ý / Từ chối (Mục 7a, 7b).
 */
public class RequestsTabPanel extends JPanel {

    private final UserSidebar sidebar;
    private final JPanel listContainer;
    private final List<ChatDataModels.FriendRequest> requests = new ArrayList<>();
    private final List<RequestItemPanel> itemPanels = new ArrayList<>();
    private final JLabel lblCount;
    private JTextField txtFilterRequests;

    public RequestsTabPanel(UserSidebar sidebar) {
        this.sidebar = sidebar;
        setLayout(new BorderLayout());
        setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        // Sub-header đếm số lượng & ô lọc theo tên (Mục 7a)
        JPanel subHeader = new JPanel();
        subHeader.setLayout(new BoxLayout(subHeader, BoxLayout.Y_AXIS));
        subHeader.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        subHeader.setBorder(new EmptyBorder(10, 14, 8, 14));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        lblCount = new JLabel("Lời mời kết bạn chờ duyệt (3)");
        lblCount.setFont(UserUITheme.FONT_BODY_BOLD);
        lblCount.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        titleRow.add(lblCount, BorderLayout.WEST);
        subHeader.add(titleRow);
        subHeader.add(Box.createVerticalStrut(6));

        txtFilterRequests = UserUITheme.createTextField("Lọc lời mời theo tên...");
        txtFilterRequests.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtFilterRequests.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(txtFilterRequests.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(txtFilterRequests.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(txtFilterRequests.getText()); }
        });
        subHeader.add(txtFilterRequests);

        add(subHeader, BorderLayout.NORTH);

        // List container
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        initMockRequests();
        renderList();

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initMockRequests() {
        requests.add(new ChatDataModels.FriendRequest("req_1", "long_hv", "Hoàng Văn Long", "10 phút trước", "Chào bạn, mình học cùng lớp Java!"));
        requests.add(new ChatDataModels.FriendRequest("req_2", "linh_dt", "Đỗ Thùy Linh", "2 giờ trước", "Kết bạn với mình để trao đổi đồ án nhé."));
        requests.add(new ChatDataModels.FriendRequest("req_3", "son_tc", "Trịnh Công Sơn", "Hôm qua", "Rất vui được kết nối với bạn!"));
    }

    private void renderList() {
        listContainer.removeAll();
        itemPanels.clear();

        if (requests.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);
            JLabel lblEmpty = new JLabel("Không có lời mời kết bạn nào.");
            lblEmpty.setFont(UserUITheme.FONT_BODY);
            lblEmpty.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            emptyPanel.add(lblEmpty);
            listContainer.add(emptyPanel);
        } else {
            for (ChatDataModels.FriendRequest req : requests) {
                RequestItemPanel item = new RequestItemPanel(req);
                itemPanels.add(item);
                listContainer.add(item);
            }
        }

        lblCount.setText("Lời mời kết bạn chờ duyệt (" + requests.size() + ")");
        listContainer.revalidate();
        listContainer.repaint();
    }

    /**
     * Lọc danh sách yêu cầu kết bạn theo tên hoặc username (Mục 7a)
     */
    public void filter(String query) {
        listContainer.removeAll();
        String q = query != null ? query.trim().toLowerCase() : "";
        if (q.equals("lọc lời mời theo tên...")) q = "";

        boolean any = false;
        for (RequestItemPanel item : itemPanels) {
            String fullName = item.req.getFullName().toLowerCase();
            String username = item.req.getUsername().toLowerCase();
            if (q.isEmpty() || fullName.contains(q) || username.contains(q)) {
                listContainer.add(item);
                any = true;
            }
        }

        if (!any && !requests.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setOpaque(false);
            JLabel lblEmpty = new JLabel("Không tìm thấy lời mời phù hợp.");
            lblEmpty.setFont(UserUITheme.FONT_BODY);
            lblEmpty.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            empty.add(lblEmpty);
            listContainer.add(empty);
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    /**
     * Từng dòng yêu cầu kết bạn
     */
    private class RequestItemPanel extends JPanel {
        private final ChatDataModels.FriendRequest req;

        public RequestItemPanel(ChatDataModels.FriendRequest req) {
            this.req = req;
            setLayout(new BorderLayout(10, 0));
            setOpaque(true);
            setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
            setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 243, 248)),
                    new EmptyBorder(12, 14, 12, 14)
            ));

            // Left: Avatar
            AvatarComponent avt = new AvatarComponent(req.getFullName(), 44, req.getAvatarColor(), false, null);
            add(avt, BorderLayout.WEST);

            // Center: Thông tin & Lời nhắn
            JPanel centerBox = new JPanel();
            centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
            centerBox.setOpaque(false);

            JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            nameRow.setOpaque(false);
            JLabel lblName = new JLabel(req.getFullName());
            lblName.setFont(UserUITheme.FONT_BODY_BOLD);
            lblName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);

            JLabel lblTime = new JLabel("• " + req.getTimeAgo());
            lblTime.setFont(UserUITheme.FONT_TIMESTAMP);
            lblTime.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            nameRow.add(lblName);
            nameRow.add(lblTime);

            JLabel lblUser = new JLabel("@" + req.getUsername());
            lblUser.setFont(UserUITheme.FONT_SMALL);
            lblUser.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);

            JLabel lblNote = new JLabel("\"" + req.getNote() + "\"");
            lblNote.setFont(UserUITheme.FONT_SMALL);
            lblNote.setForeground(new Color(80, 95, 115));
            lblNote.setBorder(new EmptyBorder(4, 0, 6, 0));

            centerBox.add(nameRow);
            centerBox.add(lblUser);
            centerBox.add(lblNote);

            // Hàng 2 nút: Đồng ý & Từ chối
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            btnRow.setOpaque(false);

            JButton btnAccept = new JButton("Đồng ý");
            btnAccept.setFont(UserUITheme.FONT_SMALL_BOLD);
            btnAccept.setForeground(Color.WHITE);
            btnAccept.setBackground(UserUITheme.COLOR_STATUS_ONLINE);
            btnAccept.setFocusPainted(false);
            btnAccept.setBorderPainted(false);
            btnAccept.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnAccept.setBorder(new EmptyBorder(5, 14, 5, 14));
            btnAccept.addActionListener(e -> {
                requests.remove(req);
                if (sidebar != null && sidebar.getFriendsPanel() != null) {
                    sidebar.getFriendsPanel().addFriend(new ChatDataModels.Friend(
                            req.getUsername(), req.getFullName(), req.getUsername() + "@gmail.com", true, "Đang hoạt động"
                    ));
                }
                renderList();
                JOptionPane.showMessageDialog(this, "Đã chấp nhận kết bạn với " + req.getFullName() + "!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            });

            JButton btnDecline = new JButton("Từ chối");
            btnDecline.setFont(UserUITheme.FONT_SMALL_BOLD);
            btnDecline.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            btnDecline.setBackground(new Color(240, 242, 245));
            btnDecline.setFocusPainted(false);
            btnDecline.setBorderPainted(false);
            btnDecline.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDecline.setBorder(new EmptyBorder(5, 14, 5, 14));
            btnDecline.addActionListener(e -> {
                requests.remove(req);
                renderList();
                JOptionPane.showMessageDialog(this, "Đã từ chối lời mời từ @" + req.getUsername(), "Đã từ chối", JOptionPane.INFORMATION_MESSAGE);
            });

            btnRow.add(btnAccept);
            btnRow.add(btnDecline);
            centerBox.add(btnRow);

            add(centerBox, BorderLayout.CENTER);
        }
    }
}

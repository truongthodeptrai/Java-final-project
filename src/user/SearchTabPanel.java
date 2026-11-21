package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab 5: Tìm kiếm người dùng hệ thống (SearchTabPanel)
 * Cho phép tìm kiếm người dùng mới theo tên hoặc username,
 * hiển thị trạng thái quan hệ và hỗ trợ gửi lời mời kết bạn hoặc nhắn tin.
 */
public class SearchTabPanel extends JPanel {

    private final UserSidebar sidebar;
    private final JTextField txtSearchInput;
    private final JPanel resultsContainer;
    private final List<ChatDataModels.SearchUser> searchResults = new ArrayList<>();

    public SearchTabPanel(UserSidebar sidebar) {
        this.sidebar = sidebar;
        setLayout(new BorderLayout());
        setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        // Header ô nhập tìm kiếm
        JPanel searchHeader = new JPanel(new BorderLayout(8, 0));
        searchHeader.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        searchHeader.setBorder(new EmptyBorder(10, 14, 10, 14));

        txtSearchInput = UserUITheme.createTextField("Nhập tên hoặc @username cần tìm...");
        txtSearchInput.setPreferredSize(new Dimension(200, 36));

        JButton btnSearch = UserUITheme.createPrimaryButton("Tìm");
        btnSearch.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnSearch.setBorder(new EmptyBorder(8, 14, 8, 14));
        btnSearch.addActionListener(e -> executeSearch());

        txtSearchInput.addActionListener(e -> executeSearch());

        searchHeader.add(txtSearchInput, BorderLayout.CENTER);
        searchHeader.add(btnSearch, BorderLayout.EAST);

        add(searchHeader, BorderLayout.NORTH);

        // Container hiển thị kết quả
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        initMockSearchResults();
        renderResults();

        JScrollPane scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initMockSearchResults() {
        searchResults.add(new ChatDataModels.SearchUser("tuan_ba", "Bùi Anh Tuấn", "buianhtuan@gmail.com", "Hà Nội", false, false, true));
        searchResults.add(new ChatDataModels.SearchUser("duyen_nm", "Ngô Mỹ Duyên", "duyennm@gmail.com", "TP. Hồ Chí Minh", false, false, false));
        searchResults.add(new ChatDataModels.SearchUser("hau_pv", "Phan Văn Hậu", "haupv@gmail.com", "Đà Nẵng", false, true, true));
        searchResults.add(new ChatDataModels.SearchUser("tran_mai", "Trần Thị Mai", "tranmai@gmail.com", "TP. Hồ Chí Minh", true, false, true));
    }

    private void executeSearch() {
        String keyword = txtSearchInput.getText().trim();
        if (keyword.equals("Nhập tên hoặc @username cần tìm...")) {
            keyword = "";
        }
        resultsContainer.removeAll();

        boolean found = false;
        for (ChatDataModels.SearchUser u : searchResults) {
            if (keyword.isEmpty() || u.getFullName().toLowerCase().contains(keyword.toLowerCase())
                    || u.getUsername().toLowerCase().contains(keyword.toLowerCase())) {
                resultsContainer.add(new SearchResultItemPanel(u));
                found = true;
            }
        }

        if (!found) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setOpaque(false);
            JLabel lblEmpty = new JLabel("Không tìm thấy người dùng phù hợp.");
            lblEmpty.setFont(UserUITheme.FONT_BODY);
            lblEmpty.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            empty.add(lblEmpty);
            resultsContainer.add(empty);
        }

        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private void renderResults() {
        executeSearch();
    }

    /**
     * Từng dòng kết quả tìm kiếm
     */
    private class SearchResultItemPanel extends JPanel {
        private final ChatDataModels.SearchUser user;

        public SearchResultItemPanel(ChatDataModels.SearchUser user) {
            this.user = user;
            setLayout(new BorderLayout(10, 0));
            setOpaque(true);
            setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
            setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 243, 248)),
                    new EmptyBorder(10, 14, 10, 14)
            ));

            // Left: Avatar
            AvatarComponent avt = new AvatarComponent(user.getFullName(), 42, user.getAvatarColor(), false, user.isOnline());
            add(avt, BorderLayout.WEST);

            // Center: Tên, Username và Địa chỉ/Email
            JPanel centerBox = new JPanel(new GridLayout(2, 1, 0, 2));
            centerBox.setOpaque(false);

            JLabel lblName = new JLabel(user.getFullName());
            lblName.setFont(UserUITheme.FONT_BODY_BOLD);
            lblName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);

            JLabel lblSub = new JLabel("@" + user.getUsername() + " • " + user.getLocation());
            lblSub.setFont(UserUITheme.FONT_SMALL);
            lblSub.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);

            centerBox.add(lblName);
            centerBox.add(lblSub);
            add(centerBox, BorderLayout.CENTER);

            // Right: Trạng thái & Nút hành động
            // Right: Trạng thái & Nút hành động (Mục 9: "Sau đó có thể chat, tạo group")
            JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            rightBox.setOpaque(false);

            // Nút Nhắn tin (có thể chat ngay với người tìm thấy)
            JButton btnChat = new JButton("Nhắn tin");
            btnChat.setFont(UserUITheme.FONT_SMALL_BOLD);
            btnChat.setForeground(UserUITheme.COLOR_PRIMARY);
            btnChat.setBackground(new Color(235, 243, 255));
            btnChat.setFocusPainted(false);
            btnChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnChat.setBorder(new EmptyBorder(5, 8, 5, 8));
            btnChat.addActionListener(e -> {
                if (sidebar != null) {
                    sidebar.startChatWithFriend(new ChatDataModels.Friend(
                            user.getUsername(), user.getFullName(), user.getEmail(), user.isOnline(), ""
                    ));
                }
            });
            rightBox.add(btnChat);

            // Nút Tạo nhóm (có thể tạo group với người này)
            JButton btnGroup = new JButton("+ Nhóm");
            btnGroup.setFont(UserUITheme.FONT_SMALL);
            btnGroup.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            btnGroup.setBackground(new Color(245, 247, 250));
            btnGroup.setFocusPainted(false);
            btnGroup.setBorderPainted(false);
            btnGroup.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnGroup.setBorder(new EmptyBorder(5, 7, 5, 7));
            btnGroup.addActionListener(e -> {
                if (sidebar != null) sidebar.notifyCreateGroupRequested();
            });
            rightBox.add(btnGroup);

            // Trạng thái quan hệ bạn bè / Nút gửi lời mời kết bạn
            if (!user.isFriend()) {
                if (user.isPending()) {
                    JLabel lblPending = new JLabel("Đã gửi mời");
                    lblPending.setFont(UserUITheme.FONT_SMALL);
                    lblPending.setForeground(UserUITheme.COLOR_TEXT_MUTED);
                    rightBox.add(lblPending);
                } else {
                    JButton btnAdd = new JButton("Kết bạn");
                    btnAdd.setFont(UserUITheme.FONT_SMALL_BOLD);
                    btnAdd.setForeground(Color.WHITE);
                    btnAdd.setBackground(UserUITheme.COLOR_STATUS_ONLINE);
                    btnAdd.setFocusPainted(false);
                    btnAdd.setBorderPainted(false);
                    btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    btnAdd.setBorder(new EmptyBorder(5, 8, 5, 8));
                    btnAdd.addActionListener(e -> {
                        user.setPending(true);
                        renderResults();
                        JOptionPane.showMessageDialog(
                                this,
                                "Đã gửi lời mời kết bạn đến " + user.getFullName() + " (@" + user.getUsername() + ")!",
                                "Đã gửi yêu cầu",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    });
                    rightBox.add(btnAdd);
                }
            }

            add(rightBox, BorderLayout.EAST);
        }
    }
}

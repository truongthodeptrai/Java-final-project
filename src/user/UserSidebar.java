package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Cột Sidebar trái của giao diện chính (UserSidebar)
 * Đã được tách module thành các Panel con độc lập:
 * - Header người dùng: Avatar, Tên, Trạng thái Online, Cài đặt bánh răng (Profile, Đổi pass, Đăng xuất)
 * - Thanh tìm kiếm nhanh: Tìm kiếm bạn bè, nhóm hoặc tin nhắn
 * - Thanh chuyển 5 Tab: Chats, Bạn bè, Online, Lời mời, Tìm kiếm
 * - Điều hướng nội dung qua CardLayout với các panel con:
 *   [ChatsTabPanel], [FriendsTabPanel], [OnlineTabPanel], [RequestsTabPanel], [SearchTabPanel]
 */
public class UserSidebar extends JPanel {

    private final String currentUsername;
    private final String currentDisplayName;

    // Header người dùng
    private AvatarComponent userAvatar;
    private JLabel lblUserName;
    private JButton btnSettings;

    // Thanh tìm kiếm nhanh
    private JTextField txtQuickSearch;

    // Thanh chuyển 5 Tab
    private JPanel tabsBarPanel;
    private final List<TabButton> tabButtons = new ArrayList<>();
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // 5 Panel nội dung độc lập
    private ChatsTabPanel chatsPanel;
    private FriendsTabPanel friendsPanel;
    private OnlineTabPanel onlinePanel;
    private RequestsTabPanel requestsPanel;
    private SearchTabPanel searchPanel;

    // Listener giao tiếp với Frame chính
    public interface SidebarListener {
        void onConversationSelected(ChatDataModels.Conversation conversation);
        void onOpenProfile();
        void onChangePassword();
        void onLogout();
        void onCreateGroupRequested();
        void onGlobalSearchRequested();
    }

    private SidebarListener listener;

    public UserSidebar(String username, String displayName, SidebarListener listener) {
        this.currentUsername = (username != null && !username.trim().isEmpty()) ? username : "nguyen_van_a";
        this.currentDisplayName = (displayName != null && !displayName.trim().isEmpty()) ? displayName : "Nguyễn Văn An";
        this.listener = listener;

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UserUITheme.COLOR_BORDER));
        setPreferredSize(new Dimension(340, 700));
        setMinimumSize(new Dimension(280, 500));

        // 1. TOP SECTION: Header người dùng & Thanh tìm kiếm & Thanh Tab
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);

        // Header người dùng
        topSection.add(createUserHeader());

        // Thanh tìm kiếm nhanh
        topSection.add(createQuickSearchBar());

        // Thanh chuyển Tab (Custom Segmented Buttons)
        topSection.add(createTabButtonsBar());

        add(topSection, BorderLayout.NORTH);

        // 2. CENTER SECTION: CardLayout chứa 5 Tab Panel độc lập
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UserUITheme.COLOR_BACKGROUND);

        chatsPanel = new ChatsTabPanel(this);
        friendsPanel = new FriendsTabPanel(this);
        onlinePanel = new OnlineTabPanel(this);
        requestsPanel = new RequestsTabPanel(this);
        searchPanel = new SearchTabPanel(this);

        cardPanel.add(chatsPanel, "chats");
        cardPanel.add(friendsPanel, "friends");
        cardPanel.add(onlinePanel, "online");
        cardPanel.add(requestsPanel, "requests");
        cardPanel.add(searchPanel, "search");

        add(cardPanel, BorderLayout.CENTER);

        // Mặc định chọn Tab 1: Chats
        selectTab(0);
    }

    /**
     * Tạo Header người dùng: Avatar nhỏ gọn, Tên, Trạng thái Online, Bánh răng cài đặt vector
     */
    private JPanel createUserHeader() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(UserUITheme.COLOR_SIDEBAR);
        panel.setBorder(new EmptyBorder(8, 14, 8, 14));

        // Left: Avatar người dùng nhỏ gọn 36px
        userAvatar = new AvatarComponent(currentDisplayName, 36, false, true);
        panel.add(userAvatar, BorderLayout.WEST);

        // Center: Tên người dùng và trạng thái trực tuyến
        JPanel infoBox = new JPanel(new GridLayout(2, 1, 0, 1));
        infoBox.setOpaque(false);

        lblUserName = new JLabel(currentDisplayName);
        lblUserName.setFont(UserUITheme.FONT_BODY_BOLD);
        lblUserName.setForeground(Color.WHITE);

        JPanel statusBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        statusBox.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Arial", Font.PLAIN, 9));
        dot.setForeground(UserUITheme.COLOR_STATUS_ONLINE);

        JLabel lblStatus = new JLabel("Trực tuyến • @" + currentUsername);
        lblStatus.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 11));
        lblStatus.setForeground(new Color(190, 215, 240));

        statusBox.add(dot);
        statusBox.add(lblStatus);

        infoBox.add(lblUserName);
        infoBox.add(statusBox);
        panel.add(infoBox, BorderLayout.CENTER);

        // Right: Icon bánh răng cài đặt vector sắc nét
        btnSettings = new JButton(ChatIcons.createGearIcon(16, Color.WHITE));
        btnSettings.setBackground(UserUITheme.COLOR_SIDEBAR);
        btnSettings.setFocusPainted(false);
        btnSettings.setBorderPainted(false);
        btnSettings.setContentAreaFilled(false);
        btnSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSettings.setToolTipText("Cài đặt tài khoản & Tùy chọn");
        btnSettings.setBorder(new EmptyBorder(4, 6, 4, 6));

        btnSettings.addActionListener(e -> showSettingsPopupMenu());
        panel.add(btnSettings, BorderLayout.EAST);

        return panel;
    }

    /**
     * Menu Pop-up khi nhấn icon bánh răng: Profile, Đổi mật khẩu, Đăng xuất
     */
    private void showSettingsPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        menu.setBorder(BorderFactory.createLineBorder(UserUITheme.COLOR_BORDER));

        JMenuItem mnuProfile = new JMenuItem("Hồ sơ cá nhân");
        mnuProfile.setFont(UserUITheme.FONT_BODY);
        mnuProfile.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        mnuProfile.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        mnuProfile.addActionListener(e -> {
            if (listener != null) listener.onOpenProfile();
        });

        JMenuItem mnuChangePass = new JMenuItem("Đổi mật khẩu");
        mnuChangePass.setFont(UserUITheme.FONT_BODY);
        mnuChangePass.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        mnuChangePass.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        mnuChangePass.addActionListener(e -> {
            if (listener != null) listener.onChangePassword();
        });

        JMenuItem mnuLogout = new JMenuItem("Đăng xuất");
        mnuLogout.setFont(UserUITheme.FONT_BODY_BOLD);
        mnuLogout.setForeground(UserUITheme.COLOR_DANGER);
        mnuLogout.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        mnuLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn đăng xuất khỏi ChatConnect?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION && listener != null) {
                listener.onLogout();
            }
        });

        menu.add(mnuProfile);
        menu.add(mnuChangePass);
        menu.addSeparator();
        menu.add(mnuLogout);

        menu.show(btnSettings, 0, btnSettings.getHeight());
    }

    /**
     * Thanh tìm kiếm nhanh: Tìm kiếm bạn bè, nhóm hoặc tin nhắn
     */
    private JPanel createQuickSearchBar() {
        JPanel container = new JPanel(new BorderLayout(8, 0));
        container.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        container.setBorder(new EmptyBorder(10, 14, 8, 14));

        JPanel searchWrapper = new UserUITheme.RoundedPanel(10, new Color(245, 247, 250), UserUITheme.COLOR_BORDER);
        searchWrapper.setLayout(new BorderLayout(8, 0));
        searchWrapper.setBorder(new EmptyBorder(6, 12, 6, 10));

        JLabel iconSearch = new JLabel("🔍");
        iconSearch.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        iconSearch.setForeground(UserUITheme.COLOR_TEXT_MUTED);
        searchWrapper.add(iconSearch, BorderLayout.WEST);

        txtQuickSearch = new JTextField();
        txtQuickSearch.setFont(UserUITheme.FONT_BODY);
        txtQuickSearch.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        txtQuickSearch.setBackground(new Color(245, 247, 250));
        txtQuickSearch.setBorder(null);
        txtQuickSearch.setCaretColor(UserUITheme.COLOR_PRIMARY);

        String placeholder = "Tìm bạn bè, nhóm hoặc tin nhắn...";
        txtQuickSearch.setText(placeholder);
        txtQuickSearch.setForeground(UserUITheme.COLOR_TEXT_MUTED);

        txtQuickSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtQuickSearch.getText().equals(placeholder)) {
                    txtQuickSearch.setText("");
                    txtQuickSearch.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtQuickSearch.getText().trim().isEmpty()) {
                    txtQuickSearch.setText(placeholder);
                    txtQuickSearch.setForeground(UserUITheme.COLOR_TEXT_MUTED);
                }
            }
        });

        txtQuickSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterCurrentTab(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterCurrentTab(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterCurrentTab(); }
        });

        searchWrapper.add(txtQuickSearch, BorderLayout.CENTER);

        JButton btnClear = new JButton("X");
        btnClear.setFont(new Font("Arial", Font.BOLD, 11));
        btnClear.setForeground(UserUITheme.COLOR_TEXT_MUTED);
        btnClear.setBorderPainted(false);
        btnClear.setContentAreaFilled(false);
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.setBorder(new EmptyBorder(0, 4, 0, 2));
        btnClear.addActionListener(e -> {
            txtQuickSearch.setText("");
            filterCurrentTab();
        });
        searchWrapper.add(btnClear, BorderLayout.EAST);

        container.add(searchWrapper, BorderLayout.CENTER);

        JButton btnGlobalSearch = new JButton(ChatIcons.createSearchIcon(13, UserUITheme.COLOR_PRIMARY));
        btnGlobalSearch.setToolTipText("Tìm kiếm tin nhắn toàn cục (Ctrl+Shift+F)");
        btnGlobalSearch.setBackground(new Color(235, 243, 255));
        btnGlobalSearch.setFocusPainted(false);
        btnGlobalSearch.setBorderPainted(false);
        btnGlobalSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGlobalSearch.setPreferredSize(new Dimension(36, 36));
        btnGlobalSearch.addActionListener(e -> {
            if (listener != null) listener.onGlobalSearchRequested();
        });
        container.add(btnGlobalSearch, BorderLayout.EAST);

        return container;
    }

    private void filterCurrentTab() {
        String query = txtQuickSearch.getText().trim();
        if (query.equals("Tìm bạn bè, nhóm hoặc tin nhắn...")) {
            query = "";
        }
        chatsPanel.filter(query);
        friendsPanel.filter(query);
        onlinePanel.filter(query);
        requestsPanel.filter(query);
    }

    /**
     * Thanh chuyển Tab (Custom Buttons): Chats, Bạn bè, Online, Lời mời, Tìm kiếm
     */
    /**
     * Thanh chuyển Tab với icon Vector đồng bộ và hiệu ứng hover/active mềm mại
     */
    private JPanel createTabButtonsBar() {
        tabsBarPanel = new JPanel(new GridLayout(1, 5, 4, 0));
        tabsBarPanel.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        tabsBarPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UserUITheme.COLOR_BORDER),
                new EmptyBorder(6, 6, 6, 6)
        ));

        tabButtons.add(new TabButton(0, "Chats", "Đoạn chat gần đây", "3"));
        tabButtons.add(new TabButton(1, "Bạn bè", "Danh sách bạn bè", null));
        tabButtons.add(new TabButton(2, "Online", "Bạn bè đang trực tuyến", "3"));
        tabButtons.add(new TabButton(3, "Lời mời", "Lời mời kết bạn", "3"));
        tabButtons.add(new TabButton(4, "Tìm kiếm", "Tìm kiếm người dùng", null));

        for (TabButton tb : tabButtons) {
            tabsBarPanel.add(tb);
        }

        return tabsBarPanel;
    }

    public void selectTab(int index) {
        for (int i = 0; i < tabButtons.size(); i++) {
            tabButtons.get(i).setActive(i == index);
        }
        String[] cardNames = {"chats", "friends", "online", "requests", "search"};
        cardLayout.show(cardPanel, cardNames[index]);
    }

    /**
     * Bắt đầu cuộc trò chuyện với bạn bè: chuyển sang tab Chat, nạp hội thoại và mở
     */
    public void startChatWithFriend(ChatDataModels.Friend friend) {
        String convId = "chat_friend_" + friend.getUsername();
        ChatDataModels.Conversation existing = chatsPanel.getConversation(convId);
        if (existing == null) {
            existing = new ChatDataModels.Conversation(
                    convId, friend.getFullName(), "@" + friend.getUsername(),
                    "Bắt đầu trò chuyện mới", "Vừa xong", 0, false, friend.isOnline(), false
            );
            existing.setEmail(friend.getEmail());
            chatsPanel.addConversation(existing);
        }
        selectTab(0);
        chatsPanel.selectConversation(convId);
        if (listener != null) {
            listener.onConversationSelected(existing);
        }
    }

    public void notifyConversationSelected(ChatDataModels.Conversation conv) {
        if (listener != null) {
            listener.onConversationSelected(conv);
        }
    }

    public void notifyCreateGroupRequested() {
        if (listener != null) {
            listener.onCreateGroupRequested();
        }
    }

    // =========================================================================
    // NÚT CHUYỂN TAB TÙY BIẾN VỚI ICON VECTOR VÀ HIỆU ỨNG HOVER
    // =========================================================================
    private class TabButton extends JPanel {
        private final int index;
        private final String labelText;
        private final String badgeText;
        private boolean isActive = false;
        private boolean isHovered = false;

        public TabButton(int index, String labelText, String tooltip, String badgeText) {
            this.index = index;
            this.labelText = labelText;
            this.badgeText = badgeText;

            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText(tooltip);
            setPreferredSize(new Dimension(60, 48));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    selectTab(index);
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            UserUITheme.enableAntialiasing(g2);

            int w = getWidth();
            int h = getHeight();

            // Hiệu ứng nền khi Active hoặc Hover
            if (isActive) {
                g2.setColor(new Color(239, 246, 255)); // Nền xanh nhạt thanh lịch
                g2.fillRoundRect(1, 1, w - 2, h - 2, 8, 8);

                // Thanh gạch chân indicator xanh
                g2.setColor(UserUITheme.COLOR_PRIMARY);
                g2.fillRoundRect(8, h - 3, w - 16, 3, 2, 2);
            } else if (isHovered) {
                g2.setColor(new Color(248, 250, 252)); // Nền hover xám nhạt dịu mắt
                g2.fillRoundRect(1, 1, w - 2, h - 2, 8, 8);
            }

            // Vẽ Icon Vector sắc nét
            Color iconColor = isActive ? UserUITheme.COLOR_PRIMARY
                    : (isHovered ? UserUITheme.COLOR_TEXT_PRIMARY : UserUITheme.COLOR_TEXT_SECONDARY);
            Icon icon = getIconForTab(index, iconColor);
            if (icon != null) {
                int ix = (w - icon.getIconWidth()) / 2;
                icon.paintIcon(this, g2, ix, 6);
            }

            // Vẽ Nhãn chữ
            g2.setFont(new Font(UserUITheme.FONT_FAMILY, isActive ? Font.BOLD : Font.PLAIN, 11));
            FontMetrics fmLabel = g2.getFontMetrics();
            int labelW = fmLabel.stringWidth(labelText);
            g2.setColor(isActive ? UserUITheme.COLOR_PRIMARY
                    : (isHovered ? UserUITheme.COLOR_TEXT_PRIMARY : UserUITheme.COLOR_TEXT_SECONDARY));
            g2.drawString(labelText, (w - labelW) / 2, 35);

            // Vẽ Huy hiệu số lượng chưa đọc
            if (badgeText != null && !badgeText.isEmpty()) {
                g2.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 9));
                FontMetrics fmBadge = g2.getFontMetrics();
                int bw = Math.max(14, fmBadge.stringWidth(badgeText) + 6);
                int bh = 14;
                int bx = w - bw - 4;
                int by = 3;

                g2.setColor(UserUITheme.COLOR_PRIMARY);
                g2.fillRoundRect(bx, by, bw, bh, bh, bh);

                g2.setColor(Color.WHITE);
                int tw = fmBadge.stringWidth(badgeText);
                g2.drawString(badgeText, bx + (bw - tw) / 2, by + bh - 3);
            }

            g2.dispose();
        }

        private Icon getIconForTab(int tabIndex, Color color) {
            switch (tabIndex) {
                case 0: return ChatIcons.createMessageIcon(15, color);
                case 1: return ChatIcons.createUsersIcon(15, color);
                case 2:
                    return new Icon() {
                        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(UserUITheme.COLOR_STATUS_ONLINE);
                            g2.fillOval(x + 2, y + 2, 10, 10);
                            g2.dispose();
                        }
                        @Override public int getIconWidth() { return 14; }
                        @Override public int getIconHeight() { return 14; }
                    };
                case 3: return ChatIcons.createMailIcon(15, color);
                case 4: return ChatIcons.createSearchIcon(14, color);
                default: return null;
            }
        }
    }

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================
    public ChatsTabPanel getChatsPanel() { return chatsPanel; }
    public FriendsTabPanel getFriendsPanel() { return friendsPanel; }
    public OnlineTabPanel getOnlinePanel() { return onlinePanel; }
    public RequestsTabPanel getRequestsPanel() { return requestsPanel; }
    public SearchTabPanel getSearchPanel() { return searchPanel; }

    public void updateDisplayName(String newDisplayName) {
        lblUserName.setText(newDisplayName);
    }
}

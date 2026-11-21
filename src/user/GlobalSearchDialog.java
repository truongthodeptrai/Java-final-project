package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Hộp thoại Tìm kiếm toàn cục (GlobalSearchDialog)
 * Tìm kiếm chuỗi tin nhắn trên toàn bộ các cuộc trò chuyện (cá nhân và nhóm chat).
 * Hỗ trợ:
 * - Tìm kiếm thời gian thực (Real-time search)
 * - Lọc theo: Tất cả, Đoạn chat cá nhân, Nhóm chat
 * - Hiển thị ngữ cảnh tin nhắn với từ khóa được làm nổi bật (Highlight)
 * - Nhấp vào kết quả để chuyển hướng ngay tới tin nhắn trong cuộc trò chuyện tương ứng
 */
public class GlobalSearchDialog extends JDialog {

    public interface OnSearchResultSelectedListener {
        void onSearchResultSelected(ChatDataModels.Conversation conversation,
                                    ChatDataModels.ChatMessage message,
                                    int messageIndex);
    }

    private final List<ChatDataModels.Conversation> allConversations;
    private final OnSearchResultSelectedListener listener;

    private JTextField txtSearch;
    private JLabel lblResultCount;
    private JPanel resultsContainer;

    // Bộ lọc: 0 = Tất cả, 1 = Cá nhân, 2 = Nhóm
    private int currentFilter = 0;
    private JButton btnFilterAll;
    private JButton btnFilterDirect;
    private JButton btnFilterGroup;

    private final List<ChatDataModels.GlobalSearchResult> searchResults = new ArrayList<>();
    private final List<SearchResultCard> resultCards = new ArrayList<>();

    public GlobalSearchDialog(Window parent,
                              List<ChatDataModels.Conversation> allConversations,
                              OnSearchResultSelectedListener listener) {
        super(parent, "Tìm kiếm tin nhắn trên toàn bộ cuộc trò chuyện", ModalityType.MODELESS);
        this.allConversations = allConversations != null ? allConversations : new ArrayList<>();
        this.listener = listener;

        initComponents();
        setupShortcuts();
    }

    private void initComponents() {
        setSize(700, 600);
        setMinimumSize(new Dimension(580, 450));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // 1. TOP: Header với ô nhập tìm kiếm lớn phong cách Spotlight / Command Palette
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        topPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UserUITheme.COLOR_BORDER),
                new EmptyBorder(14, 18, 12, 18)
        ));

        // Ô tìm kiếm chính
        JPanel searchBox = new UserUITheme.RoundedPanel(10, new Color(245, 247, 250), UserUITheme.COLOR_BORDER);
        searchBox.setLayout(new BorderLayout(10, 0));
        searchBox.setBorder(new EmptyBorder(8, 14, 8, 14));
        searchBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JLabel iconSearch = new JLabel(ChatIcons.createSearchIcon(16, UserUITheme.COLOR_PRIMARY));
        searchBox.add(iconSearch, BorderLayout.WEST);

        txtSearch = new JTextField();
        txtSearch.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 15));
        txtSearch.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        txtSearch.setBackground(new Color(245, 247, 250));
        txtSearch.setBorder(null);
        txtSearch.setCaretColor(UserUITheme.COLOR_PRIMARY);

        String placeholder = "Nhập từ khóa tìm kiếm tin nhắn trên tất cả hội thoại...";
        txtSearch.setText(placeholder);
        txtSearch.setForeground(UserUITheme.COLOR_TEXT_MUTED);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().equals(placeholder)) {
                    txtSearch.setText("");
                    txtSearch.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText(placeholder);
                    txtSearch.setForeground(UserUITheme.COLOR_TEXT_MUTED);
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { executeSearch(); }
            @Override public void removeUpdate(DocumentEvent e) { executeSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { executeSearch(); }
        });

        searchBox.add(txtSearch, BorderLayout.CENTER);

        JButton btnClear = new JButton("X");
        btnClear.setFont(new Font("Arial", Font.BOLD, 12));
        btnClear.setForeground(UserUITheme.COLOR_TEXT_MUTED);
        btnClear.setBorderPainted(false);
        btnClear.setContentAreaFilled(false);
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            executeSearch();
        });
        searchBox.add(btnClear, BorderLayout.EAST);

        topPanel.add(searchBox);
        topPanel.add(Box.createVerticalStrut(10));

        // Thanh công cụ bộ lọc (Chips) và Đếm kết quả
        JPanel filterRow = new JPanel(new BorderLayout());
        filterRow.setOpaque(false);
        filterRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chipsPanel.setOpaque(false);

        btnFilterAll = createFilterChip("Tất cả", true);
        btnFilterDirect = createFilterChip("Cá nhân (1-1)", false);
        btnFilterGroup = createFilterChip("Nhóm chat", false);

        btnFilterAll.addActionListener(e -> setFilter(0));
        btnFilterDirect.addActionListener(e -> setFilter(1));
        btnFilterGroup.addActionListener(e -> setFilter(2));

        chipsPanel.add(btnFilterAll);
        chipsPanel.add(btnFilterDirect);
        chipsPanel.add(btnFilterGroup);
        filterRow.add(chipsPanel, BorderLayout.WEST);

        lblResultCount = new JLabel("Nhập từ khóa để bắt đầu");
        lblResultCount.setFont(UserUITheme.FONT_SMALL);
        lblResultCount.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        filterRow.add(lblResultCount, BorderLayout.EAST);

        topPanel.add(filterRow);
        add(topPanel, BorderLayout.NORTH);

        // 2. CENTER: Vùng hiển thị kết quả tìm kiếm cuộn mượt mà
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(UserUITheme.COLOR_BACKGROUND);
        resultsContainer.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Hiển thị trạng thái khởi tạo
        showInitialState();

        // 3. BOTTOM: Hướng dẫn phím tắt
        JPanel bottomHint = new JPanel(new BorderLayout());
        bottomHint.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        bottomHint.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER),
                new EmptyBorder(8, 18, 8, 18)
        ));

        JLabel lblKeyHint = new JLabel("Mẹo: Nhấn phím Enter để chọn kết quả đầu tiên • Esc để đóng");
        lblKeyHint.setFont(UserUITheme.FONT_TIMESTAMP);
        lblKeyHint.setForeground(UserUITheme.COLOR_TEXT_MUTED);
        bottomHint.add(lblKeyHint, BorderLayout.WEST);

        JButton btnClose = UserUITheme.createSecondaryButton("Đóng");
        btnClose.setFont(UserUITheme.FONT_SMALL);
        btnClose.setBorder(new EmptyBorder(4, 12, 4, 12));
        btnClose.addActionListener(e -> dispose());
        bottomHint.add(btnClose, BorderLayout.EAST);

        add(bottomHint, BorderLayout.SOUTH);
    }

    private JButton createFilterChip(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(UserUITheme.FONT_SMALL);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyFilterChipStyle(btn, active);
        return btn;
    }

    private void applyFilterChipStyle(JButton btn, boolean active) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        if (active) {
            btn.setBackground(UserUITheme.COLOR_PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.setBorder(new CompoundBorder(
                    new UserUITheme.RoundedLineBorder(UserUITheme.COLOR_PRIMARY, 6, 1),
                    new EmptyBorder(4, 12, 4, 12)
            ));
        } else {
            btn.setBackground(new Color(245, 247, 250));
            btn.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            btn.setBorder(new CompoundBorder(
                    new UserUITheme.RoundedLineBorder(UserUITheme.COLOR_BORDER, 6, 1),
                    new EmptyBorder(4, 12, 4, 12)
            ));
        }
    }

    private void setFilter(int filterIndex) {
        this.currentFilter = filterIndex;
        applyFilterChipStyle(btnFilterAll, filterIndex == 0);
        applyFilterChipStyle(btnFilterDirect, filterIndex == 1);
        applyFilterChipStyle(btnFilterGroup, filterIndex == 2);
        executeSearch();
    }

    private void setupShortcuts() {
        // Phím Esc đóng Dialog
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Phím Enter chọn kết quả đầu tiên
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !searchResults.isEmpty()) {
                    selectResult(searchResults.get(0));
                }
            }
        });
    }

    private void showInitialState() {
        resultsContainer.removeAll();
        JPanel emptyBox = new JPanel(new GridBagLayout());
        emptyBox.setOpaque(false);
        emptyBox.setBorder(new EmptyBorder(60, 20, 60, 20));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        JLabel icon = new JLabel("🔍");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(icon);
        inner.add(Box.createVerticalStrut(10));

        JLabel title = new JLabel("Tìm kiếm tin nhắn trong tất cả cuộc trò chuyện");
        title.setFont(UserUITheme.FONT_TITLE_SMALL);
        title.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(title);
        inner.add(Box.createVerticalStrut(4));

        JLabel sub = new JLabel("Hỗ trợ tìm kiếm tiếng Việt có dấu, không phân biệt hoa thường");
        sub.setFont(UserUITheme.FONT_SMALL);
        sub.setForeground(UserUITheme.COLOR_TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(sub);

        emptyBox.add(inner);
        resultsContainer.add(emptyBox);
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private void executeSearch() {
        String keyword = txtSearch.getText().trim();
        String placeholder = "Nhập từ khóa tìm kiếm tin nhắn trên tất cả hội thoại...";
        if (keyword.equals(placeholder) || keyword.isEmpty()) {
            showInitialState();
            lblResultCount.setText("Nhập từ khóa để bắt đầu");
            return;
        }

        resultsContainer.removeAll();
        searchResults.clear();
        resultCards.clear();

        String kwLower = keyword.toLowerCase();

        for (ChatDataModels.Conversation conv : allConversations) {
            // Áp dụng bộ lọc loại hội thoại
            if (currentFilter == 1 && conv.isGroup()) continue;
            if (currentFilter == 2 && !conv.isGroup()) continue;

            List<ChatDataModels.ChatMessage> msgs = conv.getMessages();
            for (int i = 0; i < msgs.size(); i++) {
                ChatDataModels.ChatMessage msg = msgs.get(i);
                if (msg.getContent().toLowerCase().contains(kwLower)) {
                    ChatDataModels.GlobalSearchResult result = new ChatDataModels.GlobalSearchResult(
                            conv, msg, i, msg.getContent()
                    );
                    searchResults.add(result);
                }
            }
        }

        if (searchResults.isEmpty()) {
            lblResultCount.setText("Không tìm thấy kết quả");
            JPanel notFoundBox = new JPanel(new GridBagLayout());
            notFoundBox.setOpaque(false);
            notFoundBox.setBorder(new EmptyBorder(60, 20, 60, 20));

            JPanel inner = new JPanel();
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
            inner.setOpaque(false);

            JLabel title = new JLabel("Không tìm thấy tin nhắn nào chứa \"" + keyword + "\"");
            title.setFont(UserUITheme.FONT_BODY_BOLD);
            title.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            inner.add(title);
            inner.add(Box.createVerticalStrut(4));

            JLabel sub = new JLabel("Vui lòng thử lại với từ khóa khác");
            sub.setFont(UserUITheme.FONT_SMALL);
            sub.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);
            inner.add(sub);

            notFoundBox.add(inner);
            resultsContainer.add(notFoundBox);
        } else {
            lblResultCount.setText("Tìm thấy " + searchResults.size() + " tin nhắn phù hợp");
            for (ChatDataModels.GlobalSearchResult res : searchResults) {
                SearchResultCard card = new SearchResultCard(res, keyword);
                resultCards.add(card);
                resultsContainer.add(card);
                resultsContainer.add(Box.createVerticalStrut(8));
            }
        }

        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private void selectResult(ChatDataModels.GlobalSearchResult res) {
        if (listener != null) {
            listener.onSearchResultSelected(res.getConversation(), res.getMessage(), res.getMessageIndex());
        }
        dispose();
    }

    /**
     * Thẻ hiển thị một kết quả tìm kiếm tin nhắn
     */
    private class SearchResultCard extends JPanel {
        private final ChatDataModels.GlobalSearchResult result;
        private boolean isHover = false;

        public SearchResultCard(ChatDataModels.GlobalSearchResult result, String keyword) {
            this.result = result;
            setLayout(new BorderLayout(10, 6));
            setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
            setBorder(new CompoundBorder(
                    new UserUITheme.RoundedLineBorder(UserUITheme.COLOR_BORDER, 8, 1),
                    new EmptyBorder(10, 14, 10, 14)
            ));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            ChatDataModels.Conversation conv = result.getConversation();
            ChatDataModels.ChatMessage msg = result.getMessage();

            // 1. Dòng trên: Avatar + Tên cuộc trò chuyện + Badge loại + Thời gian
            JPanel topRow = new JPanel(new BorderLayout(8, 0));
            topRow.setOpaque(false);

            JPanel infoLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            infoLeft.setOpaque(false);

            AvatarComponent avatar = new AvatarComponent(conv.getTitle(), 28, conv.getAvatarColor(), conv.isGroup(), null);
            infoLeft.add(avatar);

            JLabel lblConvTitle = new JLabel(conv.getTitle());
            lblConvTitle.setFont(UserUITheme.FONT_BODY_BOLD);
            lblConvTitle.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            infoLeft.add(lblConvTitle);

            // Badge nhóm hoặc cá nhân
            JPanel badge = createTypeBadge(conv.isGroup());
            infoLeft.add(badge);

            topRow.add(infoLeft, BorderLayout.WEST);

            JLabel lblTime = new JLabel(msg.getTimestamp());
            lblTime.setFont(UserUITheme.FONT_TIMESTAMP);
            lblTime.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            topRow.add(lblTime, BorderLayout.EAST);

            add(topRow, BorderLayout.NORTH);

            // 2. Dòng dưới: Tên người gửi + Nội dung highlight từ khóa
            JPanel contentBox = new JPanel(new BorderLayout(6, 0));
            contentBox.setOpaque(false);
            contentBox.setBorder(new EmptyBorder(2, 36, 0, 0));

            String sender = msg.isSelf() ? "Bạn: " : (msg.getSenderName() + ": ");
            String highlightedHtml = createHighlightedHtml(sender, msg.getContent(), keyword);

            JLabel lblContent = new JLabel(highlightedHtml);
            lblContent.setFont(UserUITheme.FONT_BODY);
            contentBox.add(lblContent, BorderLayout.CENTER);

            add(contentBox, BorderLayout.CENTER);

            // Hiệu ứng hover và sự kiện click
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHover = true;
                    setBackground(UserUITheme.COLOR_ITEM_HOVER);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHover = false;
                    setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    selectResult(result);
                }
            });
        }

        private JPanel createTypeBadge(boolean isGroup) {
            JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 1));
            b.setOpaque(true);
            if (isGroup) {
                b.setBackground(new Color(230, 240, 255));
                b.setBorder(new UserUITheme.RoundedLineBorder(new Color(180, 210, 250), 6, 1));
                JLabel l = new JLabel("Nhóm");
                l.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 10));
                l.setForeground(UserUITheme.COLOR_PRIMARY);
                b.add(l);
            } else {
                b.setBackground(new Color(240, 244, 248));
                b.setBorder(new UserUITheme.RoundedLineBorder(new Color(220, 226, 235), 6, 1));
                JLabel l = new JLabel("Cá nhân");
                l.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 10));
                l.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
                b.add(l);
            }
            return b;
        }

        private String createHighlightedHtml(String sender, String content, String keyword) {
            String safeContent = content.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
            String safeKw = keyword.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

            // Thay thế không phân biệt hoa thường giữ nguyên chữ gốc
            String regex = "(?i)" + java.util.regex.Pattern.quote(safeKw);
            String replacement = "<span style='background-color: #FEF08A; color: #1E293B; font-weight: bold;'>$0</span>";
            String highlighted = safeContent.replaceAll(regex, replacement);

            return "<html><body style='font-family: sans-serif; font-size: 11px; color: #1E293B;'>"
                    + "<b>" + sender + "</b>" + highlighted + "</body></html>";
        }
    }
}

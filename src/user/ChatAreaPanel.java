package user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Khung trò chuyện trung tâm (ChatAreaPanel)
 * Kiến trúc module sạch đẹp, phân tách rõ ràng:
 * - Header (ChatHeaderPanel): Tên, Badge ổ khóa xanh "Đã mã hóa đầu cuối", Bộ nút công cụ
 * - Thanh tìm kiếm (ChatSearchBar): Ô nhập, Tìm kiếm, Lên/Xuống duyệt tin khớp (Mục 11f)
 * - Lịch sử tin nhắn: JScrollPane + ChatMessageBubble (chuột phải xóa tin nhắn - Mục 11e)
 * - Thanh nhập liệu (ChatInputArea): Ô nhập, Nút Gửi, Nút Robot AI LLM Assist (Mục 11h)
 */
public class ChatAreaPanel extends JPanel {

    public interface ChatAreaListener {
        void onMessageSent(ChatDataModels.Conversation conversation, String message);
        void onToggleDetails();
        void onSpamReported(ChatDataModels.Conversation conversation);
    }

    private final ChatAreaListener listener;
    private ChatDataModels.Conversation currentConversation;

    // Các thành phần module
    private ChatHeaderPanel headerPanel;
    private ChatSearchBar searchBar;
    private JPanel messagesPanel;
    private JScrollPane messagesScrollPane;
    private ChatInputArea inputArea;

    // Quản lý tìm kiếm trong đoạn chat
    private final List<ChatMessageBubble> bubbleList = new ArrayList<>();
    private final List<Integer> matchedBubbleIndices = new ArrayList<>();
    private int currentMatchPointer = -1;

    public ChatAreaPanel(ChatAreaListener listener) {
        this.listener = listener;
        initComponents();
        setupShortcuts();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UserUITheme.COLOR_BACKGROUND);

        // 1. HEADER KHUNG CHAT & THANH TÌM KIẾM
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        headerPanel = new ChatHeaderPanel(new ChatHeaderPanel.HeaderListener() {
            @Override
            public void onToggleSearch() {
                toggleSearchBar();
            }

            @Override
            public void onClearHistory() {
                handleClearHistory();
            }

            @Override
            public void onReportSpam() {
                handleReportSpam();
            }

            @Override
            public void onToggleDetails() {
                if (listener != null) listener.onToggleDetails();
            }
        });
        topContainer.add(headerPanel);

        // Thanh tìm kiếm trong lịch sử chat 1 người (Mục 11f)
        searchBar = new ChatSearchBar(new ChatSearchBar.SearchListener() {
            @Override
            public void onSearch(String keyword) {
                performSearch(keyword);
            }

            @Override
            public void onNavigateNext() {
                navigateMatch(1);
            }

            @Override
            public void onNavigatePrev() {
                navigateMatch(-1);
            }

            @Override
            public void onCloseSearch() {
                toggleSearchBar();
            }
        });
        searchBar.setVisible(false);
        topContainer.add(searchBar);

        add(topContainer, BorderLayout.NORTH);

        // 2. VÙNG LỊCH SỬ TIN NHẮN (JSCROLLPANE)
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(UserUITheme.COLOR_BACKGROUND);
        messagesPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        messagesScrollPane = new JScrollPane(messagesPanel);
        messagesScrollPane.setBorder(null);
        messagesScrollPane.setBackground(UserUITheme.COLOR_BACKGROUND);
        messagesScrollPane.getViewport().setBackground(UserUITheme.COLOR_BACKGROUND);
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        messagesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(messagesScrollPane, BorderLayout.CENTER);

        // 3. THANH NHẬP LIỆU BÊN DƯỚI & NÚT ROBOT AI (Mục 11h)
        inputArea = new ChatInputArea(new ChatInputArea.InputListener() {
            @Override
            public void onSendMessage(String text) {
                sendMessage(text);
            }

            @Override
            public void onOpenLlmAssist(String currentDraft) {
                openLlmAssistDialog(currentDraft);
            }

            @Override
            public void onAttachFile() {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(ChatAreaPanel.this) == JFileChooser.APPROVE_OPTION) {
                    sendMessage("[Tệp đính kèm: " + chooser.getSelectedFile().getName() + "]");
                }
            }

            @Override
            public void onAttachImage() {
                JOptionPane.showMessageDialog(ChatAreaPanel.this, "Đã chọn chức năng gửi ảnh.", "Gửi hình ảnh", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        add(inputArea, BorderLayout.SOUTH);
    }

    /**
     * Đăng ký phím tắt Ctrl+F để mở thanh tìm kiếm
     */
    private void setupShortcuts() {
        KeyStroke ctrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ctrlF, "actionCtrlF");
        getActionMap().put("actionCtrlF", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!searchBar.isVisible()) {
                    toggleSearchBar();
                } else {
                    searchBar.focusAndSelect();
                }
            }
        });
    }

    public void setConversation(ChatDataModels.Conversation conv) {
        this.currentConversation = conv;
        headerPanel.updateConversation(conv);
        resetSearchState();
        renderMessages();
    }

    /**
     * Vẽ lại toàn bộ danh sách bong bóng tin nhắn
     */
    public void renderMessages() {
        messagesPanel.removeAll();
        bubbleList.clear();

        if (currentConversation == null || currentConversation.getMessages().isEmpty()) {
            JPanel emptyBox = new JPanel(new GridBagLayout());
            emptyBox.setOpaque(false);
            JLabel lblEmpty = new JLabel("Chưa có tin nhắn nào. Hãy gửi lời chào đầu tiên! 👋");
            lblEmpty.setFont(UserUITheme.FONT_BODY);
            lblEmpty.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            emptyBox.add(lblEmpty);
            messagesPanel.add(emptyBox);
        } else {
            // Thanh phân cách ngày
            JPanel dateDivider = new JPanel(new FlowLayout(FlowLayout.CENTER));
            dateDivider.setOpaque(false);
            dateDivider.setBorder(new EmptyBorder(6, 0, 10, 0));

            JLabel lblDate = new JLabel("— Hôm nay —");
            lblDate.setFont(UserUITheme.FONT_TIMESTAMP);
            lblDate.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            dateDivider.add(lblDate);
            messagesPanel.add(dateDivider);

            // Thêm từng tin nhắn dưới dạng ChatMessageBubble
            for (ChatDataModels.ChatMessage msg : currentConversation.getMessages()) {
                ChatMessageBubble bubble = new ChatMessageBubble(msg, new ChatMessageBubble.BubbleListener() {
                    @Override
                    public void onDeleteMessage(ChatDataModels.ChatMessage m) {
                        deleteSingleMessage(m);
                    }

                    @Override
                    public void onReplyMessage(ChatDataModels.ChatMessage m) {
                        String sender = m.isSelf() ? "Bạn" : m.getSenderName();
                        inputArea.setInputText(">> [" + sender + "]: " + (m.getContent().length() > 25 ? m.getContent().substring(0, 22) + "..." : m.getContent()) + " \n");
                    }

                    @Override
                    public void onForwardMessage(ChatDataModels.ChatMessage m) {
                        JOptionPane.showMessageDialog(ChatAreaPanel.this, "Đã chọn chuyển tiếp tin nhắn:\n\"" + m.getContent() + "\"", "Chuyển tiếp tin nhắn", JOptionPane.INFORMATION_MESSAGE);
                    }

                    @Override
                    public void onPinMessage(ChatDataModels.ChatMessage m) {
                        JOptionPane.showMessageDialog(ChatAreaPanel.this, "Đã ghim tin nhắn lên đầu cuộc trò chuyện:\n\"" + m.getContent() + "\"", "Ghim tin nhắn", JOptionPane.INFORMATION_MESSAGE);
                    }

                    @Override
                    public void onDeleteMultipleRequested() {
                        openDeleteMultipleMessagesDialog();
                    }
                });
                bubbleList.add(bubble);
                messagesPanel.add(bubble);
            }
        }

        messagesPanel.revalidate();
        messagesPanel.repaint();

        // Cuộn xuống dòng cuối cùng
        SwingUtilities.invokeLater(() -> {
            JScrollBar vBar = messagesScrollPane.getVerticalScrollBar();
            vBar.setValue(vBar.getMaximum());
        });
    }

    /**
     * Xóa 1 tin nhắn cụ thể khỏi cuộc trò chuyện (Mục 11e)
     */
    private void deleteSingleMessage(ChatDataModels.ChatMessage msg) {
        if (currentConversation == null) return;

        currentConversation.getMessages().remove(msg);
        if (currentConversation.getMessages().isEmpty()) {
            currentConversation.setLastMessage("(Không có tin nhắn)");
        } else {
            ChatDataModels.ChatMessage last = currentConversation.getMessages().get(currentConversation.getMessages().size() - 1);
            currentConversation.setLastMessage((last.isSelf() ? "Bạn: " : "") + last.getContent());
            currentConversation.setLastMessageTime(last.getTimestamp());
        }

        renderMessages();
        JOptionPane.showMessageDialog(this, "Đã xóa tin nhắn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Hộp thoại chọn và xóa một hoặc vài dòng tin nhắn (Mục 11e)
     */
    public void openDeleteMultipleMessagesDialog() {
        if (currentConversation == null || currentConversation.getMessages().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có tin nhắn nào trong cuộc trò chuyện để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Chọn một hoặc nhiều tin nhắn cần xóa (Mục 11e)", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 500);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UserUITheme.COLOR_SIDEBAR);
        header.setBorder(new EmptyBorder(14, 18, 14, 18));
        JLabel lblTitle = new JLabel("Chọn các dòng tin nhắn cần xóa");
        lblTitle.setFont(UserUITheme.FONT_TITLE_SMALL);
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);
        dialog.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        listPanel.setBorder(new EmptyBorder(10, 14, 10, 14));

        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<ChatDataModels.ChatMessage> messages = currentConversation.getMessages();

        for (ChatDataModels.ChatMessage m : messages) {
            String sender = m.isSelf() ? "Bạn" : m.getSenderName();
            String snippet = m.getContent().length() > 60 ? m.getContent().substring(0, 57) + "..." : m.getContent();
            String labelText = "<html><b>" + sender + "</b> (" + m.getTimestamp() + "): " + snippet + "</html>";
            JCheckBox chk = new JCheckBox(labelText);
            chk.setFont(UserUITheme.FONT_BODY);
            chk.setOpaque(false);
            chk.setBorder(new EmptyBorder(6, 4, 6, 4));
            checkBoxes.add(chk);
            listPanel.add(chk);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(UserUITheme.COLOR_BACKGROUND);
        bottomBar.setBorder(new EmptyBorder(10, 14, 10, 14));

        JCheckBox chkSelectAll = new JCheckBox("Chọn tất cả");
        chkSelectAll.setFont(UserUITheme.FONT_SMALL_BOLD);
        chkSelectAll.setOpaque(false);
        chkSelectAll.addActionListener(e -> {
            boolean sel = chkSelectAll.isSelected();
            for (JCheckBox cb : checkBoxes) cb.setSelected(sel);
        });
        bottomBar.add(chkSelectAll, BorderLayout.WEST);

        JPanel btnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBox.setOpaque(false);

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(UserUITheme.FONT_SMALL);
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnDeleteSelected = new JButton("Xóa đã chọn");
        btnDeleteSelected.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnDeleteSelected.setForeground(Color.WHITE);
        btnDeleteSelected.setBackground(UserUITheme.COLOR_DANGER);
        btnDeleteSelected.setFocusPainted(false);
        btnDeleteSelected.setBorderPainted(false);
        btnDeleteSelected.setBorder(new EmptyBorder(6, 14, 6, 14));
        btnDeleteSelected.addActionListener(e -> {
            List<ChatDataModels.ChatMessage> toRemove = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    toRemove.add(messages.get(i));
                }
            }

            if (toRemove.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn ít nhất 1 tin nhắn để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    dialog,
                    "Bạn có chắc muốn xóa " + toRemove.size() + " tin nhắn đã chọn?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                messages.removeAll(toRemove);
                if (messages.isEmpty()) {
                    currentConversation.setLastMessage("(Không có tin nhắn)");
                } else {
                    ChatDataModels.ChatMessage last = messages.get(messages.size() - 1);
                    currentConversation.setLastMessage((last.isSelf() ? "Bạn: " : "") + last.getContent());
                    currentConversation.setLastMessageTime(last.getTimestamp());
                }
                renderMessages();
                dialog.dispose();
                JOptionPane.showMessageDialog(ChatAreaPanel.this, "Đã xóa " + toRemove.size() + " tin nhắn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnBox.add(btnCancel);
        btnBox.add(btnDeleteSelected);
        bottomBar.add(btnBox, BorderLayout.EAST);

        dialog.add(bottomBar, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Bật/Tắt thanh tìm kiếm
     */
    private void toggleSearchBar() {
        boolean show = !searchBar.isVisible();
        searchBar.setVisible(show);
        if (show) {
            searchBar.focusAndSelect();
        } else {
            resetSearchState();
        }
        revalidate();
        repaint();
    }

    /**
     * Thực hiện tìm kiếm tin nhắn theo từ khóa (Mục 11f)
     */
    private void performSearch(String keyword) {
        clearHighlights();
        matchedBubbleIndices.clear();
        currentMatchPointer = -1;

        if (keyword.isEmpty()) {
            searchBar.resetStatus();
            return;
        }

        for (int i = 0; i < bubbleList.size(); i++) {
            if (bubbleList.get(i).matchesKeyword(keyword)) {
                matchedBubbleIndices.add(i);
            }
        }

        if (matchedBubbleIndices.isEmpty()) {
            searchBar.updateMatchStatus(-1, 0);
        } else {
            currentMatchPointer = 0;
            focusMatchedBubble(currentMatchPointer);
            searchBar.updateMatchStatus(currentMatchPointer, matchedBubbleIndices.size());
        }
    }

    /**
     * Điều hướng Lên / Xuống qua các kết quả khớp từ khóa
     */
    private void navigateMatch(int direction) {
        if (matchedBubbleIndices.isEmpty()) return;

        currentMatchPointer += direction;
        if (currentMatchPointer >= matchedBubbleIndices.size()) {
            currentMatchPointer = 0; // Vòng lại đầu
        } else if (currentMatchPointer < 0) {
            currentMatchPointer = matchedBubbleIndices.size() - 1; // Vòng xuống cuối
        }

        focusMatchedBubble(currentMatchPointer);
        searchBar.updateMatchStatus(currentMatchPointer, matchedBubbleIndices.size());
    }

    private void focusMatchedBubble(int pointerIndex) {
        clearHighlights();
        if (pointerIndex >= 0 && pointerIndex < matchedBubbleIndices.size()) {
            int bubbleIdx = matchedBubbleIndices.get(pointerIndex);
            ChatMessageBubble bubble = bubbleList.get(bubbleIdx);
            bubble.setHighlighted(true);

            // Tự động cuộn đến tin nhắn
            SwingUtilities.invokeLater(() -> {
                Rectangle bounds = bubble.getBounds();
                messagesPanel.scrollRectToVisible(bounds);
            });
        }
    }

    private void clearHighlights() {
        for (ChatMessageBubble b : bubbleList) {
            b.setHighlighted(false);
        }
    }

    private void resetSearchState() {
        clearHighlights();
        matchedBubbleIndices.clear();
        currentMatchPointer = -1;
        searchBar.resetStatus();
    }

    /**
     * Mở Dialog Trợ lý AI (LLM Assist - Mục 11h)
     */
    private void openLlmAssistDialog(String currentDraft) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        LlmAssistDialog dialog = new LlmAssistDialog(parentWindow, currentDraft, text -> {
            inputArea.setInputText(text);
        });
        dialog.setVisible(true);
    }

    public void sendMessage(String text) {
        if (currentConversation == null || text.trim().isEmpty()) return;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        String timeNow = LocalTime.now().format(dtf);

        ChatDataModels.ChatMessage newMsg = new ChatDataModels.ChatMessage("Nguyễn Văn An", text, timeNow, true);
        currentConversation.getMessages().add(newMsg);
        currentConversation.setLastMessage("Bạn: " + text);
        currentConversation.setLastMessageTime(timeNow);

        renderMessages();

        if (listener != null) {
            listener.onMessageSent(currentConversation, text);
        }
    }

    private void handleClearHistory() {
        if (currentConversation == null) return;

        Object[] options = {"Xóa toàn bộ lịch sử (Mục 11d)", "Chọn vài tin nhắn để xóa (Mục 11e)", "Hủy"};
        int selected = JOptionPane.showOptionDialog(
                this,
                "Bạn muốn xóa lịch sử cuộc trò chuyện với \"" + currentConversation.getTitle() + "\" theo hình thức nào?",
                "Quản lý xóa lịch sử trò chuyện (Mục 11d, 11e)",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected == 0) { // Xóa toàn bộ
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn XÓA TOÀN BỘ lịch sử chat với \"" + currentConversation.getTitle() + "\"?\n"
                            + "Hành động này sẽ xóa sạch tất cả tin nhắn và không thể khôi phục.",
                    "Xác nhận xóa toàn bộ lịch sử",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                currentConversation.getMessages().clear();
                currentConversation.setLastMessage("(Đã xóa toàn bộ lịch sử trò chuyện)");
                renderMessages();
                JOptionPane.showMessageDialog(this, "Đã xóa toàn bộ lịch sử trò chuyện.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (selected == 1) { // Chọn một hoặc vài dòng
            openDeleteMultipleMessagesDialog();
        }
    }

    private void handleReportSpam() {
        if (currentConversation == null) return;
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        SpamReportDialog dialog = new SpamReportDialog(parentWindow, currentConversation, "nguyen_van_a", report -> {
            if (listener != null) {
                listener.onSpamReported(currentConversation);
            }
        });
        dialog.setVisible(true);
    }

    /**
     * Cuộn đến và làm nổi bật một tin nhắn cụ thể (dùng khi chọn kết quả Tìm kiếm toàn cục)
     */
    public void scrollToAndHighlightMessage(int messageIndex) {
        if (messageIndex >= 0 && messageIndex < bubbleList.size()) {
            clearHighlights();
            ChatMessageBubble bubble = bubbleList.get(messageIndex);
            bubble.setHighlighted(true);

            SwingUtilities.invokeLater(() -> {
                Rectangle bounds = bubble.getBounds();
                messagesPanel.scrollRectToVisible(bounds);
            });
        }
    }

    public ChatDataModels.Conversation getCurrentConversation() {
        return currentConversation;
    }

    public ChatHeaderPanel getHeaderPanel() {
        return headerPanel;
    }

    public ChatSearchBar getSearchBar() {
        return searchBar;
    }

    public ChatInputArea getInputArea() {
        return inputArea;
    }
}

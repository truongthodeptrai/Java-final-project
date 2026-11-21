package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Thanh nhập liệu hiện đại kiểu dáng thu gọn (ChatInputArea)
 * - Tích hợp icon Đính kèm (📎), Ảnh (🖼️), Emoji/AI (😊/✨) ngay bên trái ô nhập
 * - Hỗ trợ gõ tin nhắn nhiều dòng, tự động co giãn chiều cao (1 - 4 dòng)
 * - Phím Enter gửi tin nhắn, Shift + Enter xuống dòng
 * - Nút gửi màu xanh chủ đạo bo tròn có hiệu ứng hover
 */
public class ChatInputArea extends JPanel {

    public interface InputListener {
        void onSendMessage(String text);
        void onOpenLlmAssist(String currentDraft);
        void onAttachFile();
        void onAttachImage();
    }

    private final InputListener listener;
    private final JTextArea txtInput;
    private final JButton btnSend;
    private final JButton btnAttach;
    private final JButton btnImage;
    private final JButton btnEmoji;

    private static final String PLACEHOLDER = "Nhập tin nhắn...";
    private boolean isPlaceholderActive = true;

    private final JPanel inputContainer;

    public ChatInputArea(InputListener listener) {
        this.listener = listener;

        setLayout(new BorderLayout());
        setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER),
                new EmptyBorder(8, 14, 10, 14)
        ));

        // Khung bọc thanh nhập dạng pill/card bo góc 16px hiện đại
        inputContainer = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UserUITheme.enableAntialiasing(g2);
                g2.setColor(new Color(248, 250, 252));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inputContainer.setOpaque(false);
        inputContainer.setBorder(new EmptyBorder(4, 8, 4, 6));

        // 1. Nhóm nút icon tiện ích bên trái ô nhập: Đính kèm, Ảnh, Emoji / Gợi ý AI
        JPanel leftActionBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        leftActionBox.setOpaque(false);

        btnAttach = createIconOnlyButton(ChatIcons.createPaperclipIcon(17, new Color(100, 116, 139)), "Đính kèm tệp tin tài liệu");
        btnAttach.addActionListener(e -> {
            if (listener != null) listener.onAttachFile();
        });
        leftActionBox.add(btnAttach);

        btnImage = createIconOnlyButton(ChatIcons.createImageIcon(17, new Color(100, 116, 139)), "Gửi hình ảnh");
        btnImage.addActionListener(e -> {
            if (listener != null) listener.onAttachImage();
        });
        leftActionBox.add(btnImage);

        btnEmoji = createIconOnlyButton(ChatIcons.createEmojiIcon(17, new Color(100, 116, 139)), "Chèn biểu cảm & Gợi ý AI (LLM Assist)");
        btnEmoji.addActionListener(e -> {
            if (listener != null) listener.onOpenLlmAssist(getInputText());
        });
        leftActionBox.add(btnEmoji);

        inputContainer.add(leftActionBox, BorderLayout.WEST);

        // 2. Ô nhập văn bản JTextArea tự động co giãn chiều cao
        txtInput = new JTextArea(1, 20);
        txtInput.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 13));
        txtInput.setLineWrap(true);
        txtInput.setWrapStyleWord(true);
        txtInput.setOpaque(false);
        txtInput.setBorder(new EmptyBorder(7, 4, 5, 4));
        txtInput.setCaretColor(UserUITheme.COLOR_PRIMARY);

        // Placeholder logic
        txtInput.setText(PLACEHOLDER);
        txtInput.setForeground(UserUITheme.COLOR_TEXT_MUTED);

        txtInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderActive) {
                    txtInput.setText("");
                    txtInput.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
                    isPlaceholderActive = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtInput.getText().trim().isEmpty()) {
                    txtInput.setText(PLACEHOLDER);
                    txtInput.setForeground(UserUITheme.COLOR_TEXT_MUTED);
                    isPlaceholderActive = true;
                }
            }
        });

        // Xử lý phím: Enter gửi tin nhắn, Shift + Enter xuống dòng
        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.isShiftDown()) {
                        // Cho phép chèn ký tự xuống dòng
                    } else {
                        e.consume(); // Chặn xuống dòng
                        handleSend();
                    }
                }
            }
        });

        // Tự động điều chỉnh chiều cao khi văn bản dài
        txtInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { adjustHeight(); }
            @Override public void removeUpdate(DocumentEvent e) { adjustHeight(); }
            @Override public void changedUpdate(DocumentEvent e) { adjustHeight(); }
        });

        JScrollPane inputScroll = new JScrollPane(txtInput);
        inputScroll.setBorder(null);
        inputScroll.setOpaque(false);
        inputScroll.getViewport().setOpaque(false);
        inputScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputScroll.getVerticalScrollBar().setUnitIncrement(10);
        inputContainer.add(inputScroll, BorderLayout.CENTER);

        // 3. Nút Gửi tròn màu xanh chủ đạo (#2563EB)
        btnSend = new JButton(ChatIcons.createSendIcon(14, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UserUITheme.enableAntialiasing(g2);
                g2.setColor(getModel().isRollover() ? new Color(29, 78, 216) : UserUITheme.COLOR_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSend.setOpaque(false);
        btnSend.setContentAreaFilled(false);
        btnSend.setBorderPainted(false);
        btnSend.setFocusPainted(false);
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSend.setPreferredSize(new Dimension(36, 36));
        btnSend.setMaximumSize(new Dimension(36, 36));
        btnSend.setToolTipText("Gửi tin nhắn (Enter)");
        btnSend.addActionListener(e -> handleSend());

        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 1));
        rightBox.setOpaque(false);
        rightBox.add(btnSend);
        inputContainer.add(rightBox, BorderLayout.EAST);

        add(inputContainer, BorderLayout.CENTER);
    }

    /**
     * Nút icon tiện ích không chữ, có hiệu ứng đổi màu khi hover
     */
    private JButton createIconOnlyButton(Icon icon, String tooltip) {
        JButton btn = new JButton(icon);
        btn.setToolTipText(tooltip);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 32));
        btn.setBorder(new EmptyBorder(4, 4, 4, 4));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setContentAreaFilled(true);
                btn.setBackground(new Color(241, 245, 249));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setContentAreaFilled(false);
            }
        });

        return btn;
    }

    /**
     * Tự động điều chỉnh chiều cao thanh nhập liệu (tối đa 4 dòng ~85px)
     */
    private void adjustHeight() {
        if (isPlaceholderActive) return;
        int lineCount = txtInput.getLineCount();
        int targetHeight = Math.min(88, Math.max(40, lineCount * 20 + 16));
        inputContainer.setPreferredSize(new Dimension(inputContainer.getWidth(), targetHeight));
        revalidate();
    }

    private void handleSend() {
        String text = getInputText();
        if (!text.isEmpty()) {
            if (listener != null) {
                listener.onSendMessage(text);
            }
            clearInput();
        }
    }

    public String getInputText() {
        if (isPlaceholderActive) return "";
        return txtInput.getText().trim();
    }

    public void setInputText(String text) {
        txtInput.setText(text);
        txtInput.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        isPlaceholderActive = false;
        txtInput.requestFocus();
        adjustHeight();
    }

    public void clearInput() {
        txtInput.setText("");
        txtInput.setPreferredSize(null);
        inputContainer.setPreferredSize(null);
        revalidate();
        txtInput.requestFocus();
    }

    public void requestFocusInInput() {
        txtInput.requestFocus();
    }
}

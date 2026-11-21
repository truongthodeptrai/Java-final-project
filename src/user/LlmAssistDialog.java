package user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Hộp thoại Trợ lý AI (LLM Assist - Mục 11h của đề bài)
 * - Tab 1: Gợi ý các câu phản hồi nhanh đa dạng theo tình huống
 * - Tab 2: Viết lại tin nhắn trang trọng hơn, thân thiện hơn, hoặc súc tích hơn
 */
public class LlmAssistDialog extends JDialog {

    private final Consumer<String> onApplyCallback;
    private final String initialDraftText;

    // Tab 1 Components
    private JList<String> listQuickReplies;
    private DefaultListModel<String> replyListModel;
    private JTextArea txtPreviewQuick;

    // Tab 2 Components
    private JTabbedPane tabbedPane;
    private JTextArea txtDraftInput;
    private JComboBox<String> cboTone;
    private JTextArea txtRewriteResult;

    public LlmAssistDialog(Window owner, String currentDraft, Consumer<String> onApplyCallback) {
        super(owner, "Trợ lý AI (LLM Assist) - Gợi ý phản hồi & Viết lại tin nhắn", ModalityType.APPLICATION_MODAL);
        this.initialDraftText = (currentDraft != null && !currentDraft.trim().isEmpty()
                && !currentDraft.startsWith("Nhập tin nhắn")) ? currentDraft : "";
        this.onApplyCallback = onApplyCallback;

        initComponents();
    }

    private void initComponents() {
        setSize(700, 560);
        setMinimumSize(new Dimension(620, 480));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // 1. Header Dialog
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(67, 56, 202));
        headerPanel.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblHeaderTitle = new JLabel("Trợ lý AI (LLM Assist)", ChatIcons.createAiSparkleIcon(18, Color.WHITE), SwingConstants.LEFT);
        lblHeaderTitle.setIconTextGap(8);
        lblHeaderTitle.setFont(UserUITheme.FONT_TITLE_MEDIUM);
        lblHeaderTitle.setForeground(Color.WHITE);
        headerPanel.add(lblHeaderTitle, BorderLayout.WEST);

        JLabel lblHeaderSub = new JLabel("Được cung cấp bởi mô hình ngôn ngữ lớn (LLM)");
        lblHeaderSub.setFont(UserUITheme.FONT_SMALL);
        lblHeaderSub.setForeground(new Color(224, 231, 255));
        headerPanel.add(lblHeaderSub, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Tabbed Pane: 2 Chức năng chính theo Mục 11h
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UserUITheme.FONT_BODY_BOLD);
        tabbedPane.setBackground(UserUITheme.COLOR_BACKGROUND);

        tabbedPane.addTab("Gợi ý phản hồi nhanh", createQuickRepliesTab());
        tabbedPane.addTab("Viết lại & Tinh chỉnh tin nhắn", createRewriteTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void selectTab(int index) {
        if (tabbedPane != null && index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    /**
     * Tab 1: Gợi ý các câu phản hồi thông minh nhanh
     */
    private JPanel createQuickRepliesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblGuide = new JLabel("Chọn một câu gợi ý phù hợp để chèn vào khung chat (nhấp đúp để áp dụng ngay):");
        lblGuide.setFont(UserUITheme.FONT_BODY);
        lblGuide.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        panel.add(lblGuide, BorderLayout.NORTH);

        // Danh sách gợi ý
        replyListModel = new DefaultListModel<>();
        String[] samples = {
                "Mình hoàn toàn nhất trí với phương án này, chúng ta tiến hành tiếp nhé!",
                "Ý kiến rất hay và thực tế, mình sẽ bổ sung vào bản kế hoạch ngay.",
                "Chiều nay lúc 15h chúng ta họp online để thống nhất chi tiết nhé.",
                "Mình đang chuẩn bị tài liệu liên quan, khoảng 20 phút nữa sẽ gửi bạn.",
                "Cảm ơn bạn rất nhiều vì đã nhiệt tình hỗ trợ và phản hồi sớm!",
                "Hiện tại mình đang bận một chút, mình sẽ kiểm tra và phản hồi lại sớm nhất nhé.",
                "Rất vui được hợp tác cùng bạn, tiến độ công việc đang rất suôn sẻ.",
                "Bạn có thể gửi thêm tài liệu mô tả chi tiết yêu cầu này giúp mình được không?"
        };
        for (String s : samples) replyListModel.addElement(s);

        listQuickReplies = new JList<>(replyListModel);
        listQuickReplies.setFont(UserUITheme.FONT_BODY);
        listQuickReplies.setFixedCellHeight(38);
        listQuickReplies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listQuickReplies.setSelectedIndex(0);

        listQuickReplies.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String val = listQuickReplies.getSelectedValue();
                if (val != null) txtPreviewQuick.setText(val);
            }
        });

        listQuickReplies.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) applyQuickReply();
            }
        });

        JScrollPane listScroll = new JScrollPane(listQuickReplies);
        listScroll.setBorder(new UserUITheme.RoundedLineBorder(UserUITheme.COLOR_BORDER, 8, 1));
        panel.add(listScroll, BorderLayout.CENTER);

        // Khu vực xem trước & nút áp dụng
        JPanel bottomBox = new JPanel(new BorderLayout(8, 8));
        bottomBox.setOpaque(false);

        txtPreviewQuick = new JTextArea(replyListModel.getElementAt(0));
        txtPreviewQuick.setFont(UserUITheme.FONT_BODY);
        txtPreviewQuick.setLineWrap(true);
        txtPreviewQuick.setWrapStyleWord(true);
        txtPreviewQuick.setEditable(false);
        txtPreviewQuick.setRows(2);
        txtPreviewQuick.setBackground(new Color(245, 248, 252));
        txtPreviewQuick.setBorder(new EmptyBorder(8, 10, 8, 10));
        bottomBox.add(txtPreviewQuick, BorderLayout.CENTER);

        JButton btnApplyQuick = UserUITheme.createPrimaryButton("Áp dụng vào khung chat");
        btnApplyQuick.setFont(UserUITheme.FONT_BODY_BOLD);
        btnApplyQuick.setPreferredSize(new Dimension(215, 40));
        btnApplyQuick.addActionListener(e -> applyQuickReply());
        bottomBox.add(btnApplyQuick, BorderLayout.EAST);

        panel.add(bottomBox, BorderLayout.SOUTH);
        return panel;
    }

    private void applyQuickReply() {
        String selected = listQuickReplies.getSelectedValue();
        if (selected != null && onApplyCallback != null) {
            onApplyCallback.accept(selected);
            dispose();
        }
    }

    /**
     * Tab 2: Viết lại tin nhắn theo các văn phong (Trang trọng, Thân thiện, Súc tích)
     */
    private JPanel createRewriteTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Khung nhập bản nháp
        JPanel topBox = new JPanel(new BorderLayout(6, 6));
        topBox.setOpaque(false);

        JLabel lblInput = new JLabel("Nội dung tin nhắn gốc / bản nháp cần tinh chỉnh:");
        lblInput.setFont(UserUITheme.FONT_BODY_BOLD);
        lblInput.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        topBox.add(lblInput, BorderLayout.NORTH);

        txtDraftInput = new JTextArea(initialDraftText.isEmpty() ? "chiều nay họp lúc mấy giờ bạn ơi" : initialDraftText);
        txtDraftInput.setFont(UserUITheme.FONT_BODY);
        txtDraftInput.setRows(3);
        txtDraftInput.setLineWrap(true);
        txtDraftInput.setWrapStyleWord(true);
        JScrollPane draftScroll = new JScrollPane(txtDraftInput);
        draftScroll.setBorder(new UserUITheme.RoundedLineBorder(UserUITheme.COLOR_BORDER, 8, 1));
        topBox.add(draftScroll, BorderLayout.CENTER);

        // Tùy chọn giọng điệu & Nút chạy AI
        JPanel toneRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        toneRow.setOpaque(false);

        JLabel lblTone = new JLabel("Chọn phong cách viết:");
        lblTone.setFont(UserUITheme.FONT_BODY);
        toneRow.add(lblTone);

        cboTone = new JComboBox<>(new String[]{
                "Trang trọng & Chuyên nghiệp (Formal)",
                "Thân thiện & Ấm áp (Friendly)",
                "Ngắn gọn & Súc tích (Concise)",
                "Sửa chính tả & Chuẩn hóa ngữ pháp (Grammar)"
        });
        cboTone.setFont(UserUITheme.FONT_BODY);
        toneRow.add(cboTone);

        JButton btnGenerate = new JButton("AI Viết lại", ChatIcons.createAiSparkleIcon(12, Color.WHITE));
        btnGenerate.setFont(UserUITheme.FONT_BODY_BOLD);
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setBackground(new Color(99, 102, 241));
        btnGenerate.setIconTextGap(6);
        btnGenerate.setFocusPainted(false);
        btnGenerate.setBorderPainted(false);
        btnGenerate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerate.setBorder(new EmptyBorder(7, 14, 7, 14));
        btnGenerate.addActionListener(e -> runAiRewrite());
        toneRow.add(btnGenerate);

        topBox.add(toneRow, BorderLayout.SOUTH);
        panel.add(topBox, BorderLayout.NORTH);

        // Khung kết quả viết lại
        JPanel centerBox = new JPanel(new BorderLayout(6, 6));
        centerBox.setOpaque(false);

        JLabel lblOutput = new JLabel("Kết quả AI đề xuất (bạn có thể chỉnh sửa thêm trước khi dùng):");
        lblOutput.setFont(UserUITheme.FONT_BODY_BOLD);
        lblOutput.setForeground(new Color(67, 56, 202));
        centerBox.add(lblOutput, BorderLayout.NORTH);

        txtRewriteResult = new JTextArea("Xin chào bạn, không biết cuộc họp chiều nay của chúng ta dự kiến bắt đầu vào khung giờ nào để mình tiện chuẩn bị tài liệu?");
        txtRewriteResult.setFont(UserUITheme.FONT_BODY);
        txtRewriteResult.setLineWrap(true);
        txtRewriteResult.setWrapStyleWord(true);
        txtRewriteResult.setBackground(new Color(248, 250, 255));
        JScrollPane resultScroll = new JScrollPane(txtRewriteResult);
        resultScroll.setBorder(new UserUITheme.RoundedLineBorder(new Color(199, 210, 254), 8, 1));
        centerBox.add(resultScroll, BorderLayout.CENTER);

        panel.add(centerBox, BorderLayout.CENTER);

        // Nút áp dụng
        JPanel bottomBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomBox.setOpaque(false);

        JButton btnApplyRewrite = UserUITheme.createPrimaryButton("Sử dụng nội dung này");
        btnApplyRewrite.setFont(UserUITheme.FONT_BODY_BOLD);
        btnApplyRewrite.setPreferredSize(new Dimension(190, 40));
        btnApplyRewrite.addActionListener(e -> {
            String res = txtRewriteResult.getText().trim();
            if (!res.isEmpty() && onApplyCallback != null) {
                onApplyCallback.accept(res);
                dispose();
            }
        });
        bottomBox.add(btnApplyRewrite);

        panel.add(bottomBox, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Giả lập xử lý LLM chuyển đổi phong cách văn bản mượt mà
     */
    private void runAiRewrite() {
        String text = txtDraftInput.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập văn bản gốc cần AI viết lại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int index = cboTone.getSelectedIndex();
        String rewritten;

        if (index == 0) { // Trang trọng & Chuyên nghiệp
            rewritten = "Kính gửi bạn, liên quan đến nội dung trao đổi: \"" + text
                    + "\", xin phép được xin ý kiến phản hồi chính thức từ bạn để hai bên phối hợp thuận tiện nhất. Trân trọng cảm ơn!";
        } else if (index == 1) { // Thân thiện
            rewritten = "Chào bạn nhé! Về việc " + text.toLowerCase()
                    + ", khi nào tiện bạn nhắn lại cho mình để cùng triển khai nhé, cảm ơn bạn nhiều!";
        } else if (index == 2) { // Ngắn gọn
            rewritten = "V/v: " + text + ". Nhờ bạn xác nhận sớm để kịp tiến độ.";
        } else { // Sửa lỗi chính tả & chuẩn hóa
            String corrected = text.replaceAll("\\s+", " ").trim();
            if (!corrected.isEmpty()) {
                corrected = Character.toUpperCase(corrected.charAt(0)) + corrected.substring(1);
                if (!corrected.endsWith(".") && !corrected.endsWith("?") && !corrected.endsWith("!")) {
                    corrected += ".";
                }
            }
            rewritten = corrected;
        }

        txtRewriteResult.setText(rewritten);
    }
}

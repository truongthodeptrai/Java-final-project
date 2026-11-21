package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Hộp thoại Báo cáo Spam & Vi phạm (SpamReportDialog)
 * Cho phép người dùng:
 * - Chọn lý do báo cáo vi phạm (Spam, Lừa đảo, Quấy rối, Giả mạo, Lý do khác)
 * - Nhập mô tả chi tiết bằng chứng vi phạm
 * - Tùy chọn đồng thời chặn người dùng
 * - Tùy chọn gửi kèm bằng chứng lịch sử tin nhắn gần nhất tới Admin
 */
public class SpamReportDialog extends JDialog {

    public interface OnSpamReportSubmittedListener {
        void onSpamReportSubmitted(ChatDataModels.SpamReport report);
    }

    private final ChatDataModels.Conversation targetConversation;
    private final String reporterUsername;
    private final OnSpamReportSubmittedListener listener;

    private final List<JRadioButton> reasonRadios = new ArrayList<>();
    private final ButtonGroup reasonGroup = new ButtonGroup();
    private JTextArea txtDescription;
    private JCheckBox chkBlock;
    private JCheckBox chkAttachEvidence;
    private JButton btnSubmit;
    private JButton btnCancel;
    private JLabel lblError;

    private static final String[] SPAM_REASONS = {
            "Spam tin nhắn / Quảng cáo rác không mong muốn",
            "Lừa đảo, giả mạo hoặc chứa liên kết độc hại (Scam/Phishing)",
            "Quấy rối, đe dọa hoặc dùng từ ngữ thù địch xúc phạm",
            "Nội dung phản cảm, đồi trụy hoặc vi phạm tiêu chuẩn cộng đồng",
            "Lý do khác..."
    };

    public SpamReportDialog(Window parent,
                            ChatDataModels.Conversation targetConversation,
                            String reporterUsername,
                            OnSpamReportSubmittedListener listener) {
        super(parent, "Báo cáo vi phạm / Spam", ModalityType.APPLICATION_MODAL);
        this.targetConversation = targetConversation;
        this.reporterUsername = (reporterUsername != null) ? reporterUsername : "nguyen_van_a";
        this.listener = listener;

        initComponents();
    }

    private void initComponents() {
        setSize(500, 640);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // 1. Header cảnh báo màu cam nổi bật
        JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
        headerPanel.setBackground(new Color(254, 243, 199));
        headerPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(245, 158, 11)),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel iconWarning = new JLabel(ChatIcons.createWarningIcon(24, new Color(180, 83, 9)));
        headerPanel.add(iconWarning, BorderLayout.WEST);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Báo cáo vi phạm / Spam");
        lblTitle.setFont(UserUITheme.FONT_TITLE_SMALL);
        lblTitle.setForeground(new Color(146, 64, 14));

        JLabel lblSub = new JLabel("Báo cáo sẽ được chuyển tới Ban Quản Trị hệ thống (Admin) để xử lý.");
        lblSub.setFont(UserUITheme.FONT_SMALL);
        lblSub.setForeground(new Color(180, 83, 9));

        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        headerPanel.add(titleBox, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Nội dung Form
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(new EmptyBorder(16, 22, 12, 22));

        // 2.1. Thẻ thông tin đối tượng bị báo cáo
        UserUITheme.RoundedPanel targetCard = new UserUITheme.RoundedPanel(8, UserUITheme.COLOR_CARD_BACKGROUND, UserUITheme.COLOR_BORDER);
        targetCard.setLayout(new BorderLayout(12, 0));
        targetCard.setBorder(new EmptyBorder(8, 12, 8, 12));
        targetCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        targetCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String targetTitle = (targetConversation != null) ? targetConversation.getTitle() : "Người dùng";
        AvatarComponent avt = new AvatarComponent(targetTitle, 36,
                (targetConversation != null) ? targetConversation.getAvatarColor() : null,
                targetConversation != null && targetConversation.isGroup(), null);
        targetCard.add(avt, BorderLayout.WEST);

        JPanel targetInfo = new JPanel(new GridLayout(2, 1, 0, 1));
        targetInfo.setOpaque(false);

        JLabel lblTargetName = new JLabel("Đối tượng: " + targetTitle);
        lblTargetName.setFont(UserUITheme.FONT_BODY_BOLD);
        lblTargetName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);

        String subInfo = (targetConversation != null) ? targetConversation.getSubtitle() : "";
        JLabel lblTargetSub = new JLabel(subInfo.isEmpty() ? "Tài khoản người dùng" : subInfo);
        lblTargetSub.setFont(UserUITheme.FONT_SMALL);
        lblTargetSub.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);

        targetInfo.add(lblTargetName);
        targetInfo.add(lblTargetSub);
        targetCard.add(targetInfo, BorderLayout.CENTER);

        bodyPanel.add(targetCard);
        bodyPanel.add(Box.createVerticalStrut(14));

        // 2.2. Danh sách chọn lý do báo cáo
        JLabel lblReasonTitle = new JLabel("Vui lòng chọn lý do báo cáo vi phạm *");
        lblReasonTitle.setFont(UserUITheme.FONT_BODY_BOLD);
        lblReasonTitle.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        lblReasonTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblReasonTitle);
        bodyPanel.add(Box.createVerticalStrut(6));

        JPanel reasonPanel = new JPanel();
        reasonPanel.setLayout(new BoxLayout(reasonPanel, BoxLayout.Y_AXIS));
        reasonPanel.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        reasonPanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(UserUITheme.COLOR_BORDER),
                new EmptyBorder(8, 12, 8, 12)
        ));
        reasonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        reasonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < SPAM_REASONS.length; i++) {
            String r = SPAM_REASONS[i];
            JRadioButton radio = new JRadioButton(r);
            radio.setFont(UserUITheme.FONT_BODY);
            radio.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            radio.setOpaque(false);
            radio.setFocusPainted(false);
            radio.setBorder(new EmptyBorder(3, 0, 3, 0));
            if (i == 0) radio.setSelected(true); // Mặc định chọn lý do đầu tiên

            radio.addActionListener(e -> {
                if (r.startsWith("Lý do khác")) {
                    txtDescription.requestFocus();
                }
            });

            reasonGroup.add(radio);
            reasonRadios.add(radio);
            reasonPanel.add(radio);
        }
        bodyPanel.add(reasonPanel);
        bodyPanel.add(Box.createVerticalStrut(12));

        // 2.3. Mô tả chi tiết bổ sung
        JLabel lblDescTitle = new JLabel("Mô tả chi tiết thêm (Tùy chọn)");
        lblDescTitle.setFont(UserUITheme.FONT_BODY_BOLD);
        lblDescTitle.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        lblDescTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblDescTitle);
        bodyPanel.add(Box.createVerticalStrut(6));

        txtDescription = new JTextArea();
        txtDescription.setFont(UserUITheme.FONT_BODY);
        txtDescription.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setRows(3);
        txtDescription.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setBorder(BorderFactory.createLineBorder(UserUITheme.COLOR_BORDER));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(descScroll);
        bodyPanel.add(Box.createVerticalStrut(10));

        // 2.4. Tùy chọn bổ trợ
        chkBlock = new JCheckBox("Đồng thời chặn tài khoản này (Không nhận tin nhắn nữa)", true);
        chkBlock.setFont(UserUITheme.FONT_BODY);
        chkBlock.setForeground(UserUITheme.COLOR_DANGER);
        chkBlock.setOpaque(false);
        chkBlock.setFocusPainted(false);
        chkBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(chkBlock);
        bodyPanel.add(Box.createVerticalStrut(4));

        chkAttachEvidence = new JCheckBox("Đính kèm các tin nhắn gần nhất làm bằng chứng xác thực", true);
        chkAttachEvidence.setFont(UserUITheme.FONT_BODY);
        chkAttachEvidence.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        chkAttachEvidence.setOpaque(false);
        chkAttachEvidence.setFocusPainted(false);
        chkAttachEvidence.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(chkAttachEvidence);
        bodyPanel.add(Box.createVerticalStrut(6));

        lblError = new JLabel(" ");
        lblError.setFont(UserUITheme.FONT_SMALL_BOLD);
        lblError.setForeground(UserUITheme.COLOR_DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblError);

        add(bodyPanel, BorderLayout.CENTER);

        // 3. Footer buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        footer.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER));

        btnCancel = UserUITheme.createSecondaryButton("Hủy");
        btnCancel.addActionListener(e -> dispose());

        btnSubmit = UserUITheme.createDangerButton("Gửi báo cáo vi phạm");
        btnSubmit.addActionListener(e -> handleSubmitReport());

        footer.add(btnCancel);
        footer.add(btnSubmit);
        add(footer, BorderLayout.SOUTH);
    }

    private void handleSubmitReport() {
        String selectedReason = null;
        for (JRadioButton radio : reasonRadios) {
            if (radio.isSelected()) {
                selectedReason = radio.getText();
                break;
            }
        }

        if (selectedReason == null) {
            lblError.setText("⚠️ Vui lòng chọn một lý do báo cáo!");
            return;
        }

        String desc = txtDescription.getText().trim();
        boolean block = chkBlock.isSelected();
        int evidenceCount = (chkAttachEvidence.isSelected() && targetConversation != null)
                ? targetConversation.getMessages().size() : 0;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String reportTime = LocalDateTime.now().format(dtf);

        String targetId = (targetConversation != null) ? targetConversation.getId() : "unknown_target";
        String targetName = (targetConversation != null) ? targetConversation.getTitle() : "Người dùng";

        ChatDataModels.SpamReport report = new ChatDataModels.SpamReport(
                targetId, targetName, reporterUsername, selectedReason, desc, block, reportTime, evidenceCount
        );

        if (listener != null) {
            listener.onSpamReportSubmitted(report);
        }

        JOptionPane.showMessageDialog(
                this,
                "✓ Báo cáo vi phạm đối với \"" + targetName + "\" đã được gửi thành công!\n"
                        + "Ban Quản Trị hệ thống sẽ tiến hành xem xét và xử lý theo quy định.\n"
                        + (block ? "Hệ thống đã chặn tin nhắn từ tài khoản này." : ""),
                "Đã gửi báo cáo",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }
}

package user;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Thành phần hiển thị 1 bong bóng tin nhắn mẫu (ChatMessageBubble)
 * - Thiết kế chuẩn ứng dụng chat hiện đại (Telegram / Messenger / Zalo):
 *   + Tin nhắn nhận: Avatar tròn nhỏ bên trái, Tên người gửi, nền trắng bo góc 14px, viền mảnh.
 *   + Tin nhắn gửi: Nền xanh nhạt thanh lịch, căn phải, thời gian + trạng thái đã đọc ✓✓.
 *   + Message Context Menu đầy đủ: Trả lời, Sao chép, Chuyển tiếp, Ghim tin nhắn, Xóa tin nhắn.
 *   + Tối ưu khoảng trống và chiều cao, gọn gàng và thoáng mắt.
 */
public class ChatMessageBubble extends JPanel {

    public interface BubbleListener {
        void onDeleteMessage(ChatDataModels.ChatMessage message);
        default void onReplyMessage(ChatDataModels.ChatMessage message) {}
        default void onForwardMessage(ChatDataModels.ChatMessage message) {}
        default void onPinMessage(ChatDataModels.ChatMessage message) {}
        default void onDeleteMultipleRequested() {}
    }

    private final ChatDataModels.ChatMessage message;
    private final BubbleListener listener;
    private final UserUITheme.RoundedPanel bubblePanel;
    private final JTextArea txtContent;
    private final Color defaultBg;
    private final Color defaultBorder;
    private JPopupMenu contextMenu;

    public ChatMessageBubble(ChatDataModels.ChatMessage message, BubbleListener listener) {
        this.message = message;
        this.listener = listener;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(3, 12, 3, 12));

        boolean isSelf = message.isSelf();
        // Màu sắc bubble hiện đại
        defaultBg = isSelf ? new Color(224, 237, 255) : Color.WHITE; // Xanh nhạt cho tin gửi, trắng cho tin nhận
        defaultBorder = isSelf ? new Color(191, 219, 254) : new Color(226, 232, 240);
        Color textColor = UserUITheme.COLOR_TEXT_PRIMARY;

        // Container bọc toàn bộ dòng tin nhắn
        JPanel rowBox = new JPanel(new FlowLayout(isSelf ? FlowLayout.RIGHT : FlowLayout.LEFT, 8, 0));
        rowBox.setOpaque(false);

        // 1. Avatar người gửi (chỉ hiển thị cho tin nhắn nhận)
        if (!isSelf) {
            String senderName = (message.getSenderName() != null && !message.getSenderName().trim().isEmpty())
                    ? message.getSenderName() : "Người dùng";
            AvatarComponent avatar = new AvatarComponent(senderName, 28, false, null);
            avatar.setToolTipText(senderName);
            rowBox.add(avatar);
        }

        // 2. Khung bong bóng bo góc 14px mềm mại
        bubblePanel = new UserUITheme.RoundedPanel(14, defaultBg, defaultBorder);
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.Y_AXIS));
        bubblePanel.setBorder(new EmptyBorder(6, 12, 6, 12));

        // Tên người gửi ở đầu bong bóng (dành cho tin nhận)
        if (!isSelf && message.getSenderName() != null && !message.getSenderName().trim().isEmpty()) {
            JLabel lblSender = new JLabel(message.getSenderName());
            lblSender.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, 12));
            lblSender.setForeground(new Color(37, 99, 235)); // Primary Blue
            lblSender.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblSender.setBorder(new EmptyBorder(0, 0, 3, 4));
            bubblePanel.add(lblSender);
        }

        // Nội dung tin nhắn
        txtContent = new JTextArea(message.getContent());
        txtContent.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 13));
        txtContent.setForeground(textColor);
        txtContent.setOpaque(false);
        txtContent.setEditable(false);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setFocusable(false);
        int cols = Math.min(38, Math.max(14, message.getContent().length()));
        txtContent.setColumns(cols);
        txtContent.setAlignmentX(Component.LEFT_ALIGNMENT);
        bubblePanel.add(txtContent);

        // Dòng footer: Thời gian & Trạng thái gửi
        JPanel footerRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        footerRow.setOpaque(false);
        footerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String timeStr = message.getTimestamp() != null ? message.getTimestamp() : "";
        JLabel lblTime = new JLabel(timeStr);
        lblTime.setFont(new Font(UserUITheme.FONT_FAMILY, Font.PLAIN, 11));
        lblTime.setForeground(new Color(148, 163, 184));
        footerRow.add(lblTime);

        if (isSelf) {
            JLabel lblDot = new JLabel("•");
            lblDot.setFont(new Font("Arial", Font.PLAIN, 10));
            lblDot.setForeground(new Color(148, 163, 184));
            footerRow.add(lblDot);

            JLabel lblCheck = new JLabel(ChatIcons.createDoubleCheckIcon(15, 11, new Color(37, 99, 235)));
            lblCheck.setToolTipText("Đã gửi & Đã đọc");
            footerRow.add(lblCheck);
        }

        bubblePanel.add(Box.createVerticalStrut(2));
        bubblePanel.add(footerRow);

        rowBox.add(bubblePanel);
        add(rowBox, isSelf ? BorderLayout.EAST : BorderLayout.WEST);

        // Gắn Context Menu chuột phải
        setupContextMenu();
    }

    /**
     * Menu chuột phải (Message Context Menu) chuẩn thiết kế:
     * - Trả lời (Reply)
     * - Sao chép (Copy)
     * - Chuyển tiếp (Forward)
     * - Ghim tin nhắn (Pin)
     * - Xóa tin nhắn (Delete - Danger Red)
     */
    private void setupContextMenu() {
        contextMenu = new JPopupMenu();
        contextMenu.setBackground(Color.WHITE);
        contextMenu.setBorder(new CompoundBorder(
                new UserUITheme.RoundedLineBorder(UserUITheme.COLOR_BORDER, 8, 1),
                new EmptyBorder(4, 4, 4, 4)
        ));

        // 1. Trả lời
        JMenuItem miReply = createContextMenuItem("Trả lời", ChatIcons.createReplyIcon(14, UserUITheme.COLOR_TEXT_PRIMARY), false);
        miReply.addActionListener(e -> {
            if (listener != null) listener.onReplyMessage(message);
        });
        contextMenu.add(miReply);

        // 2. Sao chép
        JMenuItem miCopy = createContextMenuItem("Sao chép", ChatIcons.createCopyIcon(14, UserUITheme.COLOR_TEXT_PRIMARY), false);
        miCopy.addActionListener(e -> {
            StringSelection selection = new StringSelection(message.getContent());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        });
        contextMenu.add(miCopy);

        // 3. Chuyển tiếp
        JMenuItem miForward = createContextMenuItem("Chuyển tiếp", ChatIcons.createForwardIcon(14, UserUITheme.COLOR_TEXT_PRIMARY), false);
        miForward.addActionListener(e -> {
            if (listener != null) listener.onForwardMessage(message);
        });
        contextMenu.add(miForward);

        // 4. Ghim tin nhắn
        JMenuItem miPin = createContextMenuItem("Ghim tin nhắn", ChatIcons.createPinIcon(14, UserUITheme.COLOR_TEXT_PRIMARY), false);
        miPin.addActionListener(e -> {
            if (listener != null) listener.onPinMessage(message);
        });
        contextMenu.add(miPin);

        contextMenu.addSeparator();

        // 5. Xóa tin nhắn (Màu đỏ cảnh báo)
        JMenuItem miDelete = createContextMenuItem("Xóa tin nhắn", ChatIcons.createTrashIcon(14, UserUITheme.COLOR_DANGER), true);
        miDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn xóa tin nhắn này khỏi cuộc trò chuyện?\n\""
                            + (message.getContent().length() > 40 ? message.getContent().substring(0, 37) + "..." : message.getContent()) + "\"",
                    "Xác nhận xóa tin nhắn",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION && listener != null) {
                listener.onDeleteMessage(message);
            }
        });
        contextMenu.add(miDelete);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showMenu(e);
            }

            private void showMenu(MouseEvent e) {
                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        };

        bubblePanel.addMouseListener(mouseHandler);
        txtContent.addMouseListener(mouseHandler);
    }

    private JMenuItem createContextMenuItem(String text, Icon icon, boolean isDanger) {
        JMenuItem item = new JMenuItem(text, icon);
        item.setFont(UserUITheme.FONT_BODY);
        item.setForeground(isDanger ? UserUITheme.COLOR_DANGER : UserUITheme.COLOR_TEXT_PRIMARY);
        item.setBackground(Color.WHITE);
        item.setIconTextGap(8);
        item.setBorder(new EmptyBorder(6, 12, 6, 12));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(isDanger ? new Color(254, 242, 242) : new Color(248, 250, 252));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(Color.WHITE);
            }
        });

        return item;
    }

    /**
     * Bật / tắt trạng thái Highlight khi khớp từ khóa tìm kiếm
     */
    public void setHighlighted(boolean active) {
        if (active) {
            bubblePanel.setBackground(new Color(254, 247, 225)); // Nền vàng ấm
            bubblePanel.setBorder(new UserUITheme.RoundedLineBorder(new Color(245, 158, 11), 14, 2)); // Viền cam nổi bật
        } else {
            bubblePanel.setBackground(defaultBg);
            bubblePanel.setBorder(new UserUITheme.RoundedLineBorder(defaultBorder, 14, 1));
        }
        revalidate();
        repaint();
    }

    /**
     * Kiểm tra nội dung tin nhắn có chứa từ khóa không
     */
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return false;
        return message.getContent().toLowerCase().contains(keyword.trim().toLowerCase());
    }

    public ChatDataModels.ChatMessage getMessage() {
        return message;
    }

    public void showContextMenu(int x, int y) {
        if (contextMenu != null) {
            contextMenu.show(this, x, y);
        }
    }

    public void hideContextMenu() {
        if (contextMenu != null) {
            contextMenu.setVisible(false);
        }
    }

    public JPopupMenu getContextMenu() {
        return contextMenu;
    }
}

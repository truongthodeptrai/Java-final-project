package user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Thanh tìm kiếm trong lịch sử chat 1 người (ChatSearchBar - Mục 11f)
 * - Ô nhập từ khóa tìm kiếm
 * - Nút Tìm kiếm
 * - Nút Lên (▲) / Xuống (▼) duyệt qua các tin nhắn khớp từ khóa
 * - Hiển thị số lượng kết quả (ví dụ: "1 / 3 kết quả")
 * - Nút đóng ✕
 */
public class ChatSearchBar extends JPanel {

    public interface SearchListener {
        void onSearch(String keyword);
        void onNavigateNext();
        void onNavigatePrev();
        void onCloseSearch();
    }

    private final SearchListener listener;

    private final JTextField txtKeyword;
    private final JButton btnSearch;
    private final JButton btnPrev;
    private final JButton btnNext;
    private final JLabel lblMatchStatus;
    private final JButton btnClose;

    public ChatSearchBar(SearchListener listener) {
        this.listener = listener;

        setLayout(new BorderLayout(8, 0));
        setBackground(new Color(243, 246, 251));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UserUITheme.COLOR_BORDER),
                new EmptyBorder(8, 16, 8, 16)
        ));

        // 1. Bên trái: Icon tìm kiếm & Ô nhập từ khóa
        JPanel leftBox = new JPanel(new BorderLayout(8, 0));
        leftBox.setOpaque(false);

        JLabel lblIcon = new JLabel(ChatIcons.createSearchIcon(14, UserUITheme.COLOR_PRIMARY));
        leftBox.add(lblIcon, BorderLayout.WEST);

        txtKeyword = UserUITheme.createTextField("Nhập từ khóa tìm trong đoạn chat này...");
        txtKeyword.setPreferredSize(new Dimension(280, 34));
        txtKeyword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    triggerSearch();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (listener != null) listener.onCloseSearch();
                }
            }
        });
        leftBox.add(txtKeyword, BorderLayout.CENTER);

        add(leftBox, BorderLayout.CENTER);

        // 2. Bên phải: Nút Tìm, Nhãn kết quả, Nút Lên/Xuống, Nút Đóng
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightBox.setOpaque(false);

        btnSearch = new JButton("Tìm kiếm", ChatIcons.createSearchIcon(12, Color.WHITE));
        btnSearch.setFont(UserUITheme.FONT_SMALL_BOLD);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBackground(UserUITheme.COLOR_PRIMARY);
        btnSearch.setIconTextGap(5);
        btnSearch.setToolTipText("Bắt đầu tìm kiếm tin nhắn (Enter)");
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnSearch.addActionListener(e -> triggerSearch());
        rightBox.add(btnSearch);

        lblMatchStatus = new JLabel("");
        lblMatchStatus.setFont(UserUITheme.FONT_SMALL_BOLD);
        lblMatchStatus.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        lblMatchStatus.setBorder(new EmptyBorder(0, 6, 0, 6));
        rightBox.add(lblMatchStatus);

        btnPrev = createNavButton("▲ Lên", "Tin nhắn khớp phía trước");
        btnPrev.setEnabled(false);
        btnPrev.addActionListener(e -> {
            if (listener != null) listener.onNavigatePrev();
        });
        rightBox.add(btnPrev);

        btnNext = createNavButton("▼ Xuống", "Tin nhắn khớp tiếp theo");
        btnNext.setEnabled(false);
        btnNext.addActionListener(e -> {
            if (listener != null) listener.onNavigateNext();
        });
        rightBox.add(btnNext);

        btnClose = new JButton(ChatIcons.createCloseIcon(12, UserUITheme.COLOR_TEXT_SECONDARY));
        btnClose.setToolTipText("Đóng thanh tìm kiếm (Esc)");
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setPreferredSize(new Dimension(28, 28));
        btnClose.addActionListener(e -> {
            if (listener != null) listener.onCloseSearch();
        });
        rightBox.add(btnClose);

        add(rightBox, BorderLayout.EAST);
    }

    private void triggerSearch() {
        String kw = txtKeyword.getText().trim();
        String placeholder = "Nhập từ khóa tìm trong đoạn chat này...";
        if (kw.equals(placeholder)) kw = "";
        if (listener != null) {
            listener.onSearch(kw);
        }
    }

    private JButton createNavButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(UserUITheme.FONT_SMALL_BOLD);
        btn.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new UserUITheme.RoundedLineBorder(UserUITheme.COLOR_BORDER, 8, 1));
        btn.setPreferredSize(new Dimension(74, 32));
        return btn;
    }

    public void updateMatchStatus(int currentIndex, int totalMatches) {
        if (totalMatches <= 0) {
            lblMatchStatus.setText("Không tìm thấy");
            lblMatchStatus.setForeground(UserUITheme.COLOR_DANGER);
            btnPrev.setEnabled(false);
            btnNext.setEnabled(false);
        } else {
            lblMatchStatus.setText((currentIndex + 1) + " / " + totalMatches + " kết quả");
            lblMatchStatus.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            btnPrev.setEnabled(totalMatches > 1);
            btnNext.setEnabled(totalMatches > 1);
        }
    }

    public void resetStatus() {
        lblMatchStatus.setText("");
        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);
    }

    public void focusAndSelect() {
        txtKeyword.requestFocus();
        txtKeyword.selectAll();
    }

    public String getKeyword() {
        String text = txtKeyword.getText().trim();
        if (text.equals("Nhập từ khóa tìm trong đoạn chat này...")) return "";
        return text;
    }

    public void setKeyword(String text) {
        txtKeyword.setText(text);
        txtKeyword.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
    }
}

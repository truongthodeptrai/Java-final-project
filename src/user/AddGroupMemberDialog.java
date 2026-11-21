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
 * Hộp thoại Thêm thành viên mới vào nhóm chat (AddGroupMemberDialog)
 * Hiển thị danh sách bạn bè chưa có trong nhóm, hỗ trợ tìm kiếm nhanh,
 * chọn nhiều bạn bè và thêm vào nhóm theo chuẩn đồ án.
 */
public class AddGroupMemberDialog extends JDialog {

    public interface OnMembersAddedListener {
        void onMembersAdded(List<String> newMemberNames);
    }

    private final List<String> availableFriends;
    private final OnMembersAddedListener listener;

    private JTextField txtSearch;
    private JPanel listPanel;
    private final List<FriendItemRow> itemRows = new ArrayList<>();
    private JLabel lblSelectedCount;
    private JButton btnAdd;
    private JButton btnCancel;

    public AddGroupMemberDialog(Window parent, List<String> availableFriends, OnMembersAddedListener listener) {
        super(parent, "Thêm thành viên vào nhóm", ModalityType.APPLICATION_MODAL);
        this.availableFriends = availableFriends != null ? availableFriends : new ArrayList<>();
        this.listener = listener;

        initComponents();
    }

    private void initComponents() {
        setSize(460, 540);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UserUITheme.COLOR_SIDEBAR);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Thêm thành viên vào nhóm");
        lblTitle.setFont(UserUITheme.FONT_TITLE_SMALL);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Chọn bạn bè từ danh bạ để mời vào cuộc trò chuyện");
        lblSub.setFont(UserUITheme.FONT_SMALL);
        lblSub.setForeground(new Color(185, 205, 230));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);
        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        headerPanel.add(titleBox, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Body
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(new EmptyBorder(14, 20, 10, 20));

        // Thanh tìm kiếm bạn bè
        txtSearch = UserUITheme.createTextField("Tìm theo tên hoặc @username...");
        txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterList(); }
            @Override public void removeUpdate(DocumentEvent e) { filterList(); }
            @Override public void changedUpdate(DocumentEvent e) { filterList(); }
        });
        bodyPanel.add(txtSearch);
        bodyPanel.add(Box.createVerticalStrut(10));

        // Thanh công cụ chọn nhanh
        JPanel toolsRow = new JPanel(new BorderLayout());
        toolsRow.setOpaque(false);
        toolsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        lblSelectedCount = new JLabel("Đã chọn: 0 người");
        lblSelectedCount.setFont(UserUITheme.FONT_SMALL_BOLD);
        lblSelectedCount.setForeground(UserUITheme.COLOR_PRIMARY);
        toolsRow.add(lblSelectedCount, BorderLayout.WEST);

        JPanel selectActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        selectActions.setOpaque(false);

        JButton btnSelectAll = new JButton("Chọn tất cả");
        styleMiniLink(btnSelectAll);
        btnSelectAll.addActionListener(e -> setAllSelected(true));

        JButton btnDeselect = new JButton("Bỏ chọn");
        styleMiniLink(btnDeselect);
        btnDeselect.addActionListener(e -> setAllSelected(false));

        selectActions.add(btnSelectAll);
        selectActions.add(new JLabel("•"));
        selectActions.add(btnDeselect);
        toolsRow.add(selectActions, BorderLayout.EAST);

        bodyPanel.add(toolsRow);
        bodyPanel.add(Box.createVerticalStrut(8));

        // Danh sách bạn bè có Checkbox
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        if (availableFriends.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(30, 10, 30, 10));
            JLabel lblEmpty = new JLabel("Tất cả bạn bè đã có trong nhóm.");
            lblEmpty.setFont(UserUITheme.FONT_BODY);
            lblEmpty.setForeground(UserUITheme.COLOR_TEXT_MUTED);
            emptyPanel.add(lblEmpty);
            listPanel.add(emptyPanel);
        } else {
            for (String friend : availableFriends) {
                FriendItemRow row = new FriendItemRow(friend);
                itemRows.add(row);
                listPanel.add(row);
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UserUITheme.COLOR_BORDER));
        scrollPane.setPreferredSize(new Dimension(420, 240));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        bodyPanel.add(scrollPane);

        add(bodyPanel, BorderLayout.CENTER);

        // 3. Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UserUITheme.COLOR_BORDER));

        btnCancel = UserUITheme.createSecondaryButton("Hủy");
        btnCancel.addActionListener(e -> dispose());

        btnAdd = UserUITheme.createPrimaryButton("Thêm vào nhóm");
        btnAdd.addActionListener(e -> handleAddMembers());

        footer.add(btnCancel);
        footer.add(btnAdd);
        add(footer, BorderLayout.SOUTH);
    }

    private void styleMiniLink(JButton btn) {
        btn.setFont(UserUITheme.FONT_SMALL);
        btn.setForeground(UserUITheme.COLOR_PRIMARY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 2, 0, 2));
    }

    private void setAllSelected(boolean selected) {
        for (FriendItemRow row : itemRows) {
            if (row.isVisible()) {
                row.setSelected(selected);
            }
        }
        updateCount();
    }

    private void filterList() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.equals("tìm theo tên hoặc @username...")) keyword = "";

        for (FriendItemRow row : itemRows) {
            boolean visible = keyword.isEmpty() || row.friendName.toLowerCase().contains(keyword);
            row.setVisible(visible);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void updateCount() {
        int count = 0;
        for (FriendItemRow row : itemRows) {
            if (row.isSelected()) count++;
        }
        lblSelectedCount.setText("Đã chọn: " + count + " người");
        btnAdd.setText(count > 0 ? "Thêm vào nhóm (" + count + ")" : "Thêm vào nhóm");
    }

    private void handleAddMembers() {
        List<String> chosen = new ArrayList<>();
        for (FriendItemRow row : itemRows) {
            if (row.isSelected()) {
                chosen.add(row.friendName);
            }
        }

        if (chosen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 bạn bè để thêm vào nhóm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (listener != null) {
            listener.onMembersAdded(chosen);
        }

        dispose();
    }

    /**
     * Dòng hiển thị bạn bè với Checkbox + Avatar + Tên
     */
    private class FriendItemRow extends JPanel {
        private final String friendName;
        private final JCheckBox checkBox;

        public FriendItemRow(String friendName) {
            this.friendName = friendName;
            setLayout(new BorderLayout(10, 0));
            setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
            setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(245, 247, 250)),
                    new EmptyBorder(8, 12, 8, 12)
            ));

            checkBox = new JCheckBox();
            checkBox.setOpaque(false);
            checkBox.setFocusPainted(false);
            checkBox.addActionListener(e -> updateCount());
            add(checkBox, BorderLayout.WEST);

            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            infoPanel.setOpaque(false);

            AvatarComponent avatar = new AvatarComponent(friendName, 32, false, null);
            infoPanel.add(avatar);

            JLabel lblName = new JLabel(friendName);
            lblName.setFont(UserUITheme.FONT_BODY);
            lblName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            infoPanel.add(lblName);

            add(infoPanel, BorderLayout.CENTER);

            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    checkBox.setSelected(!checkBox.isSelected());
                    updateCount();
                }
            });
        }

        public boolean isSelected() {
            return checkBox.isSelected();
        }

        public void setSelected(boolean sel) {
            checkBox.setSelected(sel);
        }
    }
}

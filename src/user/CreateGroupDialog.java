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
 * Hộp thoại Tạo nhóm chat mới (CreateGroupDialog)
 * Cho phép:
 * - Nhập tên nhóm trò chuyện mới
 * - Tìm kiếm và chọn danh sách bạn bè tham gia (yêu cầu tối thiểu 1 người)
 * - Tùy chọn kích hoạt Mã hóa đầu cuối (End-to-End Encryption - E2EE)
 */
public class CreateGroupDialog extends JDialog {

    public interface OnGroupCreatedListener {
        void onGroupCreated(String groupName, List<String> members, boolean encrypted);
    }

    private JTextField txtGroupName;
    private JTextField txtSearchFriend;
    private final List<FriendCheckItem> checkItems = new ArrayList<>();
    private final List<String> availableFriends;
    private JCheckBox chkEncrypted;
    private JLabel lblSelectedCount;
    private JLabel lblError;
    private JButton btnCreate;
    private JButton btnCancel;

    private boolean groupCreated = false;
    private String createdGroupName = null;
    private final List<String> selectedMembers = new ArrayList<>();
    private boolean isEncrypted = true;
    private final OnGroupCreatedListener listener;

    public CreateGroupDialog(Frame parent, List<String> friends, OnGroupCreatedListener listener) {
        super(parent, "Tạo nhóm trò chuyện mới", true);
        this.availableFriends = (friends != null && !friends.isEmpty()) ? friends : getDefaultFriends();
        this.listener = listener;

        initComponents();
    }

    private List<String> getDefaultFriends() {
        List<String> list = new ArrayList<>();
        list.add("Trần Thị Mai (@tran_mai)");
        list.add("Lê Hoàng Nam (@nam_le)");
        list.add("Phạm Minh Tuấn (@tuan_pm)");
        list.add("Võ Thị Hồng (@hong_vo)");
        list.add("Đặng Quốc Huy (@huy_dq)");
        list.add("Nguyễn Bích Phương (@phuong_nb)");
        return list;
    }

    private void initComponents() {
        setSize(500, 620);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        UserUITheme.applyGlobalTheme();
        getContentPane().setBackground(UserUITheme.COLOR_BACKGROUND);

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UserUITheme.COLOR_SIDEBAR);
        headerPanel.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel lblTitle = new JLabel("Tạo nhóm trò chuyện mới");
        lblTitle.setFont(UserUITheme.FONT_TITLE_MEDIUM);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Thêm bạn bè vào cuộc trò chuyện chung");
        lblSub.setFont(UserUITheme.FONT_SMALL);
        lblSub.setForeground(new Color(185, 205, 230));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);
        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        headerPanel.add(titleBox, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Body Form
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(new EmptyBorder(16, 22, 10, 22));

        // 2.1. Tên nhóm
        JLabel lblGroupName = new JLabel("Tên nhóm chat *");
        lblGroupName.setFont(UserUITheme.FONT_BODY_BOLD);
        lblGroupName.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        lblGroupName.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblGroupName);
        bodyPanel.add(Box.createVerticalStrut(6));

        txtGroupName = UserUITheme.createTextField("Nhập tên nhóm (ví dụ: Nhóm Đồ án Java)...");
        txtGroupName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtGroupName.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(txtGroupName);
        bodyPanel.add(Box.createVerticalStrut(14));

        // 2.2. Chọn thành viên
        JLabel lblMembers = new JLabel("Chọn thành viên (tối thiểu 1 bạn bè) *");
        lblMembers.setFont(UserUITheme.FONT_BODY_BOLD);
        lblMembers.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
        lblMembers.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblMembers);
        bodyPanel.add(Box.createVerticalStrut(6));

        // Thanh tìm kiếm nhanh bạn bè
        txtSearchFriend = UserUITheme.createTextField("Tìm bạn bè trong danh bạ...");
        txtSearchFriend.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtSearchFriend.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtSearchFriend.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterFriends(); }
            @Override public void removeUpdate(DocumentEvent e) { filterFriends(); }
            @Override public void changedUpdate(DocumentEvent e) { filterFriends(); }
        });
        bodyPanel.add(txtSearchFriend);
        bodyPanel.add(Box.createVerticalStrut(6));

        // Thanh công cụ chọn nhanh + Đếm
        JPanel toolRow = new JPanel(new BorderLayout());
        toolRow.setOpaque(false);
        toolRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        toolRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSelectedCount = new JLabel("Đã chọn: 0 người");
        lblSelectedCount.setFont(UserUITheme.FONT_SMALL_BOLD);
        lblSelectedCount.setForeground(UserUITheme.COLOR_PRIMARY);
        toolRow.add(lblSelectedCount, BorderLayout.WEST);

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        quickActions.setOpaque(false);

        JButton btnAll = new JButton("Chọn tất cả");
        styleMiniLink(btnAll);
        btnAll.addActionListener(e -> setAllSelected(true));

        JButton btnNone = new JButton("Bỏ chọn");
        styleMiniLink(btnNone);
        btnNone.addActionListener(e -> setAllSelected(false));

        quickActions.add(btnAll);
        quickActions.add(new JLabel("•"));
        quickActions.add(btnNone);
        toolRow.add(quickActions, BorderLayout.EAST);

        bodyPanel.add(toolRow);
        bodyPanel.add(Box.createVerticalStrut(6));

        // Danh sách bạn bè
        JPanel membersListPanel = new JPanel();
        membersListPanel.setLayout(new BoxLayout(membersListPanel, BoxLayout.Y_AXIS));
        membersListPanel.setBackground(UserUITheme.COLOR_CARD_BACKGROUND);

        for (String friend : availableFriends) {
            FriendCheckItem item = new FriendCheckItem(friend);
            checkItems.add(item);
            membersListPanel.add(item);
        }

        JScrollPane scrollPane = new JScrollPane(membersListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UserUITheme.COLOR_BORDER));
        scrollPane.setPreferredSize(new Dimension(450, 160));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        bodyPanel.add(scrollPane);
        bodyPanel.add(Box.createVerticalStrut(12));

        // 2.3. Tùy chọn bảo mật mã hóa
        chkEncrypted = new JCheckBox("Bật mã hóa đầu cuối (End-to-End Encryption)", true);
        chkEncrypted.setFont(UserUITheme.FONT_BODY_BOLD);
        chkEncrypted.setForeground(UserUITheme.COLOR_PRIMARY);
        chkEncrypted.setOpaque(false);
        chkEncrypted.setFocusPainted(false);
        chkEncrypted.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(chkEncrypted);
        bodyPanel.add(Box.createVerticalStrut(3));

        JLabel lblEncNote = new JLabel("Bảo vệ nội dung tin nhắn giữa các thành viên trong nhóm");
        lblEncNote.setFont(UserUITheme.FONT_SMALL);
        lblEncNote.setForeground(UserUITheme.COLOR_TEXT_SECONDARY);
        lblEncNote.setBorder(new EmptyBorder(0, 24, 0, 0));
        lblEncNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblEncNote);
        bodyPanel.add(Box.createVerticalStrut(8));

        // Thông báo lỗi
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

        btnCreate = UserUITheme.createPrimaryButton("Tạo nhóm ngay");
        btnCreate.addActionListener(e -> handleCreateGroup());

        footer.add(btnCancel);
        footer.add(btnCreate);
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
        for (FriendCheckItem item : checkItems) {
            if (item.isVisible()) {
                item.setSelected(selected);
            }
        }
        updateCount();
    }

    private void filterFriends() {
        String kw = txtSearchFriend.getText().trim().toLowerCase();
        if (kw.equals("tìm bạn bè trong danh bạ...")) kw = "";

        for (FriendCheckItem item : checkItems) {
            boolean visible = kw.isEmpty() || item.friendName.toLowerCase().contains(kw);
            item.setVisible(visible);
        }
    }

    private void updateCount() {
        int count = 0;
        for (FriendCheckItem item : checkItems) {
            if (item.isSelected()) count++;
        }
        lblSelectedCount.setText("Đã chọn: " + count + " người");
        lblError.setText(" ");
    }

    private void handleCreateGroup() {
        String name = txtGroupName.getText().trim();
        String placeholder = "Nhập tên nhóm (ví dụ: Nhóm Đồ án Java)...";
        if (name.isEmpty() || name.equals(placeholder)) {
            lblError.setText("⚠️ Vui lòng nhập tên nhóm chat!");
            txtGroupName.requestFocus();
            return;
        }

        selectedMembers.clear();
        for (FriendCheckItem item : checkItems) {
            if (item.isSelected()) {
                selectedMembers.add(item.friendName);
            }
        }

        if (selectedMembers.isEmpty()) {
            lblError.setText("⚠️ Vui lòng chọn ít nhất 1 thành viên tham gia!");
            return;
        }

        groupCreated = true;
        createdGroupName = name;
        isEncrypted = chkEncrypted.isSelected();

        if (listener != null) {
            listener.onGroupCreated(createdGroupName, selectedMembers, isEncrypted);
        }

        JOptionPane.showMessageDialog(
                this,
                "🎉 Nhóm \"" + createdGroupName + "\" đã được tạo thành công với " + selectedMembers.size() + " thành viên!",
                "Tạo nhóm thành công",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }

    public boolean isGroupCreated() { return groupCreated; }
    public String getCreatedGroupName() { return createdGroupName; }
    public List<String> getSelectedMembers() { return selectedMembers; }
    public boolean isEncrypted() { return isEncrypted; }

    /**
     * Dòng hiển thị bạn bè với Checkbox + Avatar + Tên
     */
    private class FriendCheckItem extends JPanel {
        private final String friendName;
        private final JCheckBox checkBox;

        public FriendCheckItem(String friendName) {
            this.friendName = friendName;
            setLayout(new BorderLayout(8, 0));
            setBackground(UserUITheme.COLOR_CARD_BACKGROUND);
            setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(245, 247, 250)),
                    new EmptyBorder(6, 10, 6, 10)
            ));

            checkBox = new JCheckBox();
            checkBox.setOpaque(false);
            checkBox.setFocusPainted(false);
            checkBox.addActionListener(e -> updateCount());
            add(checkBox, BorderLayout.WEST);

            JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            info.setOpaque(false);

            AvatarComponent avt = new AvatarComponent(friendName, 28, false, null);
            info.add(avt);

            JLabel lbl = new JLabel(friendName);
            lbl.setFont(UserUITheme.FONT_BODY);
            lbl.setForeground(UserUITheme.COLOR_TEXT_PRIMARY);
            info.add(lbl);

            add(info, BorderLayout.CENTER);

            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    checkBox.setSelected(!checkBox.isSelected());
                    updateCount();
                }
            });
        }

        public boolean isSelected() { return checkBox.isSelected(); }
        public void setSelected(boolean sel) { checkBox.setSelected(sel); }
    }
}

package user;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Tập hợp các lớp đối tượng dữ liệu (Data Models) cho phân hệ Người dùng
 */
public class ChatDataModels {

    /**
     * Mô hình một cuộc trò chuyện (Cá nhân hoặc Nhóm)
     */
    public static class Conversation {
        private String id;
        private String title;
        private String subtitle;
        private String lastMessage;
        private String lastMessageTime;
        private int unreadCount;
        private boolean isGroup;
        private boolean isOnline;
        private boolean isEncrypted;
        private Color avatarColor;
        private String email;
        private String address;
        private List<ChatMessage> messages = new ArrayList<>();
        private List<String> members = new ArrayList<>();
        private List<GroupMember> groupMembers = new ArrayList<>();

        public Conversation(String id, String title, String subtitle, String lastMessage,
                            String lastMessageTime, int unreadCount, boolean isGroup,
                            boolean isOnline, boolean isEncrypted) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.lastMessage = lastMessage;
            this.lastMessageTime = lastMessageTime;
            this.unreadCount = unreadCount;
            this.isGroup = isGroup;
            this.isOnline = isOnline;
            this.isEncrypted = isEncrypted;
            this.avatarColor = AvatarComponent.generateColor(title);
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getSubtitle() { return subtitle; }
        public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

        public String getLastMessage() { return lastMessage; }
        public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

        public String getLastMessageTime() { return lastMessageTime; }
        public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }

        public int getUnreadCount() { return unreadCount; }
        public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

        public boolean isGroup() { return isGroup; }
        public void setGroup(boolean group) { isGroup = group; }

        public boolean isOnline() { return isOnline; }
        public void setOnline(boolean online) { isOnline = online; }

        public boolean isEncrypted() { return isEncrypted; }
        public void setEncrypted(boolean encrypted) { isEncrypted = encrypted; }

        public Color getAvatarColor() { return avatarColor; }
        public void setAvatarColor(Color avatarColor) { this.avatarColor = avatarColor; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public List<ChatMessage> getMessages() { return messages; }
        public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

        public List<String> getMembers() { return members; }
        public void setMembers(List<String> members) {
            this.members = members;
            this.groupMembers.clear();
        }

        public List<GroupMember> getGroupMembers() {
            if (groupMembers.isEmpty() && !members.isEmpty()) {
                for (String m : members) {
                    boolean admin = m.toLowerCase().contains("admin") || m.toLowerCase().contains("trưởng nhóm");
                    String cleanName = m.replaceAll("\\(.*?\\)", "").trim();
                    String userTag = cleanName.toLowerCase().replace(" ", "_");
                    groupMembers.add(new GroupMember(userTag, cleanName, admin));
                }
            }
            return groupMembers;
        }

        public void setGroupMembers(List<GroupMember> groupMembers) {
            this.groupMembers = groupMembers;
            this.members.clear();
            for (GroupMember gm : groupMembers) {
                this.members.add(gm.getDisplayName() + (gm.isAdmin() ? " (Admin)" : ""));
            }
            this.subtitle = groupMembers.size() + " thành viên";
        }

        public void addGroupMember(GroupMember gm) {
            getGroupMembers().add(gm);
            this.members.add(gm.getDisplayName() + (gm.isAdmin() ? " (Admin)" : ""));
            this.subtitle = this.groupMembers.size() + " thành viên";
        }

        public boolean removeGroupMember(String usernameOrDisplayName) {
            GroupMember toRemove = null;
            for (GroupMember gm : getGroupMembers()) {
                if (gm.getUsername().equalsIgnoreCase(usernameOrDisplayName) ||
                        gm.getDisplayName().equalsIgnoreCase(usernameOrDisplayName)) {
                    toRemove = gm;
                    break;
                }
            }
            if (toRemove != null) {
                groupMembers.remove(toRemove);
                final String targetDisplay = toRemove.getDisplayName();
                members.removeIf(s -> s.contains(targetDisplay));
                this.subtitle = this.groupMembers.size() + " thành viên";
                return true;
            }
            return false;
        }

        public void setMemberAdmin(String usernameOrDisplayName, boolean admin) {
            for (GroupMember gm : getGroupMembers()) {
                if (gm.getUsername().equalsIgnoreCase(usernameOrDisplayName) ||
                        gm.getDisplayName().equalsIgnoreCase(usernameOrDisplayName)) {
                    gm.setAdmin(admin);
                    break;
                }
            }
            members.clear();
            for (GroupMember gm : groupMembers) {
                members.add(gm.getDisplayName() + (gm.isAdmin() ? " (Admin)" : ""));
            }
        }

        public boolean isUserAdmin(String usernameOrDisplayName) {
            for (GroupMember gm : getGroupMembers()) {
                if (gm.getUsername().equalsIgnoreCase(usernameOrDisplayName) ||
                        gm.getDisplayName().equalsIgnoreCase(usernameOrDisplayName)) {
                    return gm.isAdmin();
                }
            }
            return false;
        }
    }

    /**
     * Mô hình một tin nhắn trong cuộc trò chuyện
     */
    public static class ChatMessage {
        private String senderName;
        private String content;
        private String timestamp;
        private boolean isSelf;

        public ChatMessage(String senderName, String content, String timestamp, boolean isSelf) {
            this.senderName = senderName;
            this.content = content;
            this.timestamp = timestamp;
            this.isSelf = isSelf;
        }

        public String getSenderName() { return senderName; }
        public String getContent() { return content; }
        public String getTimestamp() { return timestamp; }
        public boolean isSelf() { return isSelf; }
    }

    /**
     * Mô hình một người bạn trong danh sách bạn bè
     */
    public static class Friend {
        private String username;
        private String fullName;
        private String email;
        private boolean isOnline;
        private String statusText;
        private Color avatarColor;

        public Friend(String username, String fullName, String email, boolean isOnline, String statusText) {
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.isOnline = isOnline;
            this.statusText = statusText;
            this.avatarColor = AvatarComponent.generateColor(fullName);
        }

        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public boolean isOnline() { return isOnline; }
        public void setOnline(boolean online) { isOnline = online; }
        public String getStatusText() { return statusText; }
        public void setStatusText(String statusText) { this.statusText = statusText; }
        public Color getAvatarColor() { return avatarColor; }
    }

    /**
     * Mô hình yêu cầu kết bạn
     */
    public static class FriendRequest {
        private String id;
        private String username;
        private String fullName;
        private String timeAgo;
        private String note;
        private Color avatarColor;

        public FriendRequest(String id, String username, String fullName, String timeAgo, String note) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.timeAgo = timeAgo;
            this.note = note;
            this.avatarColor = AvatarComponent.generateColor(fullName);
        }

        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getTimeAgo() { return timeAgo; }
        public String getNote() { return note; }
        public Color getAvatarColor() { return avatarColor; }
    }

    /**
     * Mô hình người dùng trong tìm kiếm hệ thống
     */
    public static class SearchUser {
        private String username;
        private String fullName;
        private String email;
        private String location;
        private boolean isFriend;
        private boolean isPending;
        private boolean isOnline;
        private Color avatarColor;

        public SearchUser(String username, String fullName, String email, String location,
                          boolean isFriend, boolean isPending, boolean isOnline) {
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.location = location;
            this.isFriend = isFriend;
            this.isPending = isPending;
            this.isOnline = isOnline;
            this.avatarColor = AvatarComponent.generateColor(fullName);
        }

        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getLocation() { return location; }
        public boolean isFriend() { return isFriend; }
        public void setFriend(boolean friend) { isFriend = friend; }
        public boolean isPending() { return isPending; }
        public void setPending(boolean pending) { isPending = pending; }
        public boolean isOnline() { return isOnline; }
        public Color getAvatarColor() { return avatarColor; }
    }

    /**
     * Mô hình thành viên trong nhóm chat
     */
    public static class GroupMember {
        private String username;
        private String displayName;
        private boolean isAdmin;
        private Color avatarColor;
        private boolean isOnline;

        public GroupMember(String username, String displayName, boolean isAdmin) {
            this(username, displayName, isAdmin, true);
        }

        public GroupMember(String username, String displayName, boolean isAdmin, boolean isOnline) {
            this.username = username;
            this.displayName = displayName;
            this.isAdmin = isAdmin;
            this.isOnline = isOnline;
            this.avatarColor = AvatarComponent.generateColor(displayName);
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public boolean isAdmin() { return isAdmin; }
        public void setAdmin(boolean admin) { isAdmin = admin; }

        public Color getAvatarColor() { return avatarColor; }
        public void setAvatarColor(Color avatarColor) { this.avatarColor = avatarColor; }

        public boolean isOnline() { return isOnline; }
        public void setOnline(boolean online) { isOnline = online; }

        public String getRoleBadgeText() {
            return isAdmin ? "Admin" : "Member";
        }
    }

    /**
     * Mô hình báo cáo vi phạm / spam
     */
    public static class SpamReport {
        private String targetId;
        private String targetName;
        private String reporter;
        private String reason;
        private String description;
        private boolean blocked;
        private String reportTime;
        private int attachedEvidenceCount;

        public SpamReport(String targetId, String targetName, String reporter,
                          String reason, String description, boolean blocked,
                          String reportTime, int attachedEvidenceCount) {
            this.targetId = targetId;
            this.targetName = targetName;
            this.reporter = reporter;
            this.reason = reason;
            this.description = description;
            this.blocked = blocked;
            this.reportTime = reportTime;
            this.attachedEvidenceCount = attachedEvidenceCount;
        }

        public String getTargetId() { return targetId; }
        public String getTargetName() { return targetName; }
        public String getReporter() { return reporter; }
        public String getReason() { return reason; }
        public String getDescription() { return description; }
        public boolean isBlocked() { return blocked; }
        public String getReportTime() { return reportTime; }
        public int getAttachedEvidenceCount() { return attachedEvidenceCount; }
    }

    /**
     * Mô hình kết quả tìm kiếm tin nhắn toàn cục (Global Message Search)
     */
    public static class GlobalSearchResult {
        private Conversation conversation;
        private ChatMessage message;
        private int messageIndex;
        private String matchSnippet;

        public GlobalSearchResult(Conversation conversation, ChatMessage message, int messageIndex, String matchSnippet) {
            this.conversation = conversation;
            this.message = message;
            this.messageIndex = messageIndex;
            this.matchSnippet = matchSnippet;
        }

        public Conversation getConversation() { return conversation; }
        public ChatMessage getMessage() { return message; }
        public int getMessageIndex() { return messageIndex; }
        public String getMatchSnippet() { return matchSnippet; }
    }
}

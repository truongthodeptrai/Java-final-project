# ĐỒ ÁN CUỐI KỲ: HỆ THỐNG CHAT (CHATCONNECT)
> **Môn học:** Lập trình ứng dụng Java  
> **Khoa:** Công nghệ Thông tin - BM. Công nghệ Phần mềm  
> **Trường:** Đại học Khoa học Tự nhiên, ĐHQG-HCM  
> **Giai đoạn:** Phiên bản 1 (20% - Thiết kế Giao diện & Cơ sở dữ liệu)

---

## 📖 1. Giới thiệu Đồ án
Dự án **ChatConnect** là hệ thống ứng dụng trò chuyện trực tuyến (Chat Desktop Application) được xây dựng bằng ngôn ngữ **Java** và thư viện đồ họa **Java Swing / AWT** theo phong cách giao diện hiện đại (Modern Flat & Rounded UI), tối ưu trải nghiệm người dùng và đáp ứng chuẩn kiến trúc phần mềm.

Hệ thống được thiết kế hoàn chỉnh gồm **2 phân hệ độc lập**:
1. **Phân hệ dành cho Người quản trị (Admin Subsystem)**
2. **Phân hệ dành cho Người sử dụng (User Subsystem)**

---

## 🌟 2. Danh sách Chức năng chi tiết (Theo Đề bài)

### 🔹 A. Phân hệ dành cho Người quản trị (Admin)
1. **Quản lý danh sách người dùng:**
   - Xem thông tin đầy đủ: Tên đăng nhập, họ tên, địa chỉ, ngày sinh, giới tính, email.
   - Lọc danh sách theo tên / tên đăng nhập / trạng thái tài khoản.
   - Sắp xếp danh sách theo họ tên hoặc ngày tạo tài khoản.
   - Thao tác: Thêm người dùng mới, cập nhật thông tin, xóa người dùng.
   - Khóa / mở khóa tài khoản người dùng.
   - Cập nhật / reset mật khẩu cho người dùng.
   - Xem lịch sử đăng nhập chi tiết của từng tài khoản.
   - Xem danh sách bạn bè của người dùng.
2. **Xem danh sách đăng nhập theo thời gian:**
   - Hiển thị danh sách lịch sử đăng nhập theo trình tự thời gian (thời gian, username, họ tên).
3. **Quản lý danh sách nhóm chat:**
   - Sắp xếp theo tên nhóm hoặc ngày tạo nhóm; lọc nhóm chat theo tên.
   - Xem danh sách thành viên chi tiết của từng nhóm chat.
   - Xem danh sách quản trị viên (Admin) của nhóm chat.
4. **Quản lý danh sách báo cáo spam / vi phạm:**
   - Sắp xếp báo cáo theo thời gian hoặc tên đăng nhập bị báo cáo.
   - Lọc báo cáo theo khoảng thời gian hoặc theo tên đăng nhập.
   - Thực hiện khóa tài khoản người dùng vi phạm trực tiếp từ báo cáo.
5. **Danh sách người dùng đăng ký mới:**
   - Chọn khoảng thời gian (Từ ngày - Đến ngày) để hiển thị danh sách người dùng đăng ký mới.
   - Sắp xếp và lọc theo tên / ngày tạo.
6. **Biểu đồ thống kê người dùng đăng ký mới:**
   - Chọn năm thống kê, vẽ biểu đồ dạng cột (Bar Chart) với trục hoành là tháng (1 - 12) và trục tung là số lượng người đăng ký mới.
7. **Thống kê người dùng và số lượng bạn bè:**
   - Bảng thống kê hiển thị: 1 cột số lượng bạn bè trực tiếp, 1 cột tính luôn bạn của bạn bè.
   - Lọc danh sách theo số lượng bạn trực tiếp (bằng, nhỏ hơn, lớn hơn 1 số cụ thể).
8. **Thống kê người dùng hoạt động:**
   - Chọn khoảng thời gian, xem số liệu hoạt động: số lần mở app, chat với bao nhiêu người, tham gia bao nhiêu nhóm.
   - Lọc theo số lượng hoạt động (bằng, nhỏ hơn, lớn hơn 1 số nhập vào).
9. **Biểu đồ người dùng hoạt động theo năm:**
   - Chọn năm, vẽ biểu đồ cột thể hiện số lượng người có mở ứng dụng qua từng tháng.

---

### 🔹 B. Phân hệ dành cho Người sử dụng (User)
1. **Đăng ký tài khoản (`RegisterDialog`):**
   - Đầy đủ thông tin: Tên đăng nhập, Mật khẩu, Xác nhận mật khẩu, Họ và tên, Email, Ngày sinh (ComboBox Ngày/Tháng/Năm), Giới tính (Nam/Nữ/Khác), Địa chỉ.
2. **Cập nhật thông tin tài khoản (`ProfileDialog`):**
   - Xem Username cố định; cập nhật Họ tên, Email, Ngày sinh, Địa chỉ, Giới tính, Avatar.
3. **Khởi tạo lại mật khẩu (`ForgotPasswordDialog`):**
   - Nhập Username hoặc Email; hệ thống phát sinh mật khẩu ngẫu nhiên và gửi tới email.
4. **Cập nhật mật khẩu (`ChangePasswordDialog`):**
   - Nhập Mật khẩu hiện tại, Mật khẩu mới, Xác nhận mật khẩu; hướng dẫn tiêu chuẩn mật khẩu an toàn.
5. **Đăng nhập (`LoginFrame`):**
   - Bố cục 2 cột chuyên nghiệp: Hero Brand Banner + Form đăng nhập (Username, Password, Remember Me, Show/Hide Password), liên kết Đăng ký & Quên mật khẩu.
6. **Danh sách bạn bè (`FriendsTabPanel`):**
   - Hiển thị danh bạ bạn bè, lọc theo tên, chấm tròn online xanh lá / offline xám kèm thời gian hoạt động.
   - Nút "+ Thêm bạn" chuyển nhanh sang tìm kiếm để kết bạn.
   - Nút "Hủy bạn" và nút "Chặn" (Block - hủy kết bạn và chặn vĩnh viễn không cho gửi lại lời mời).
7. **Danh sách yêu cầu kết bạn (`RequestsTabPanel`):**
   - Hiển thị lời mời kết bạn kèm ô nhập lọc theo tên (`txtFilterRequests`).
   - Hai nút thao tác nhanh: "Đồng ý" (xanh lá) và "Từ chối" (xám).
8. **Danh sách bạn bè đang online (`OnlineTabPanel`):**
   - Lọc theo tên, hiển thị avatar có chấm trực tuyến, nút "Nhắn tin" ngay và nút "+ Nhóm" tạo group nhanh.
9. **Tìm người dùng (`SearchTabPanel`):**
   - Tìm theo Username hoặc họ tên; không hiển thị người đã block.
   - Kết quả tìm kiếm cung cấp đủ nút: "Nhắn tin" (chat trực tiếp), "+ Nhóm" (tạo nhóm cùng người đó), và "Kết bạn" / "Đã gửi mời".
10. **Báo cáo vi phạm / Spam (`SpamReportDialog`):**
    - Kích hoạt khi có người nhắn tin làm phiền.
    - Chọn 5 phân loại lý do (Spam, Lừa đảo, Quấy rối, Đồi trụy, Khác), nhập mô tả chi tiết, tùy chọn đính kèm bằng chứng tin nhắn và tùy chọn chặn người dùng.
11. **Khung trò chuyện (`ChatAreaPanel`, `ChatMessageBubble`, `ChatSearchBar`, `GlobalSearchDialog`, `LlmAssistDialog`):**
    - Xem lịch sử tin nhắn dạng bong bóng hiện đại, phân biệt tin gửi/nhận rõ ràng.
    - Xóa toàn bộ lịch sử trò chuyện (nút "Xóa chat" kèm xác nhận).
    - Xóa một hoặc vài dòng tin nhắn (chuột phải > "Xóa tin nhắn này" hoặc "Chọn & Xóa nhiều tin nhắn" với danh sách CheckBox).
    - Tìm kiếm chuỗi trong chat 1 người (`ChatSearchBar`, `Ctrl+F`), phím Lên/Xuống duyệt qua các kết quả khớp, tự động highlight viền cam và cuộn tới vị trí tin nhắn.
    - Tìm kiếm tin nhắn toàn cục trên tất cả cuộc trò chuyện (`GlobalSearchDialog`, `Ctrl+Shift+F`), hiển thị ngữ cảnh tin nhắn, nhấp vào để chuyển thẳng đến đoạn chat.
    - Tích hợp Trợ lý AI (`LlmAssistDialog`): Gợi ý phản hồi nhanh theo tình huống và Viết lại/Tinh chỉnh phong cách tin nhắn (Lịch sự, Thân thiện, Ngắn gọn,...).
12. **Nhóm chat (`CreateGroupDialog`, `GroupManagementDialog`, `AddGroupMemberDialog`):**
    - Tạo nhóm chat: Đặt tên nhóm, chọn danh sách bạn bè tham gia (bắt buộc tối thiểu 1 người).
    - Đổi tên nhóm chat có lưu và phát thông báo hệ thống.
    - Thêm thành viên mới vào nhóm chat.
    - Gán quyền Admin / Hạ quyền thành viên.
    - Xóa thành viên khỏi nhóm: Chỉ Admin nhóm mới có quyền xóa (nút xóa bị vô hiệu hóa kèm tooltip đối với Member thường).
    - Trò chuyện nhóm có hiển thị tên người gửi trong bong bóng chat.
    - Tính năng **Mã hóa nhóm (End-to-End Encryption - E2EE)** tham khảo Facebook Messenger, hiển thị huy hiệu ổ khóa xanh bảo mật.

---

## 📁 3. Cấu trúc thư mục dự án

```text
Java-final-project/
├── .gitignore               # Cấu hình loại trừ file nhị phân (bin/, out/, *.class)
├── README.md                # Tài liệu mô tả đồ án và hướng dẫn chạy
├── script/                  # Thư mục chứa script tạo Cơ sở dữ liệu (Database)
│   └── chat_app.sql         # Script SQL tạo bảng và nạp dữ liệu mẫu
├── screenshots/             # 18 hình ảnh chụp giao diện thực tế phục vụ Báo cáo
│   ├── login_frame.png
│   ├── register_dialog.png
│   ├── forgot_password_dialog.png
│   ├── profile_dialog.png
│   ├── change_password_dialog.png
│   ├── user_mainframe_chats.png
│   ├── user_mainframe_friends.png
│   ├── user_mainframe_online.png
│   ├── user_mainframe_requests.png
│   ├── user_mainframe_search.png
│   ├── chat_search_active.png
│   ├── global_search_dialog.png
│   ├── llm_assist_quick_tab.png
│   ├── llm_assist_rewrite_tab.png
│   ├── create_group_dialog.png
│   ├── group_management_dialog.png
│   ├── add_group_member_dialog.png
│   └── spam_report_dialog.png
├── bin/                     # Thư mục chứa file bytecode (.class) sau khi biên dịch
└── src/                     # Thư mục mã nguồn chính (Source code)
    ├── admin/               # Mã nguồn Phân hệ Quản trị viên (Admin)
    │   ├── ActivityChartPanel.java
    │   ├── ActivityPanel.java
    │   ├── DashboardPanel.java
    │   ├── FriendStatisticsPanel.java
    │   ├── GroupPanel.java
    │   ├── LoginHistoryPanel.java
    │   ├── Main.java         # Điểm khởi chạy Admin
    │   ├── MainFrame.java
    │   ├── NewUserPanel.java
    │   ├── RegistrationChartPanel.java
    │   ├── Sidebar.java
    │   ├── SpamReportPanel.java
    │   └── UserPanel.java
    └── user/                # Mã nguồn Phân hệ Người sử dụng (User)
        ├── AddGroupMemberDialog.java
        ├── AvatarComponent.java
        ├── ChangePasswordDialog.java
        ├── ChatAreaPanel.java
        ├── ChatDataModels.java
        ├── ChatDetailsPanel.java
        ├── ChatHeaderPanel.java
        ├── ChatIcons.java
        ├── ChatInputArea.java
        ├── ChatMessageBubble.java
        ├── ChatSearchBar.java
        ├── ChatsTabPanel.java
        ├── CreateGroupDialog.java
        ├── ForgotPasswordDialog.java
        ├── FriendsTabPanel.java
        ├── GlobalSearchDialog.java
        ├── GroupManagementDialog.java
        ├── LlmAssistDialog.java
        ├── LoginFrame.java       # Điểm khởi chạy Màn hình Đăng nhập
        ├── OnlineTabPanel.java
        ├── ProfileDialog.java
        ├── RegisterDialog.java
        ├── RequestsTabPanel.java
        ├── SearchTabPanel.java
        ├── SpamReportDialog.java
        ├── TestScreens.java      # Bộ kiểm thử tự động chụp màn hình
        ├── UserMainFrame.java    # Điểm khởi chạy Giao diện Chat 3 cột
        ├── UserSidebar.java
        └── UserUITheme.java
```

---

## 🛠️ 4. Hướng dẫn Biên dịch từ Terminal (Compile)

Dự án sử dụng Java thuần (Java SE 17+ hoặc Java 21+), không phụ thuộc bất kỳ thư viện bên ngoài nào.

Mở Terminal (Command Prompt hoặc PowerShell trên Windows, hoặc Terminal trên macOS/Linux) tại thư mục `Java-final-project`:

```bash
cd "D:\Work\Project\Lập trình ứng dụng Java\Java-final-project"

# Tạo thư mục bin nếu chưa có
mkdir bin

# Biên dịch toàn bộ 2 phân hệ Admin và User với mã hóa UTF-8
javac -encoding UTF-8 -d bin src/admin/*.java src/user/*.java
```

*(Trên Windows Command Prompt `cmd`, bạn có thể dùng dấu `\` thay cho `/`: `javac -encoding UTF-8 -d bin src\admin\*.java src\user\*.java`).*

---

## 🚀 5. Hướng dẫn Khởi chạy từ Terminal (Run)

Sau khi biên dịch xong, chạy các lệnh sau từ Terminal tại thư mục `Java-final-project`:

### 🔹 1. Chạy Phân hệ Quản trị viên (Admin)
```bash
java -Dfile.encoding=UTF-8 -cp bin admin.Main
```
> Lệnh này sẽ mở cửa sổ làm việc **Admin Dashboard** với thanh Sidebar chuyển đổi giữa các chức năng: Quản lý người dùng, Xem lịch sử đăng nhập, Quản lý nhóm chat, Báo cáo spam, và Biểu đồ thống kê.

---

### 🔹 2. Chạy Phân hệ Người dùng (User)

#### Cách A: Bắt đầu từ Màn hình Đăng nhập (`LoginFrame`)
```bash
java -Dfile.encoding=UTF-8 -cp bin user.LoginFrame
```
> Cửa sổ Đăng nhập sẽ hiện lên. Bạn có thể:
> - Nhấn **Đăng ký tài khoản** để mở form đăng ký mới.
> - Nhấn **Quên mật khẩu** để mở form cấp lại mật khẩu ngẫu nhiên qua email.
> - Nhập username bất kỳ (ví dụ: `nguyen_van_a`) và mật khẩu rồi bấm **Đăng nhập** để vào Khung chat chính.

#### Cách B: Mở trực tiếp Giao diện Chat chính 3 cột (`UserMainFrame`)
```bash
java -Dfile.encoding=UTF-8 -cp bin user.UserMainFrame
```
> Lệnh này mở ngay lập tức không gian làm việc chính 3 cột của người dùng (Sidebar danh bạ, Khung chat tin nhắn, và Cột thông tin chi tiết hội thoại).

---

### 🔹 3. Chạy Bộ kiểm thử tự động & Xuất ảnh chụp giao diện (`TestScreens`)
```bash
java -Dfile.encoding=UTF-8 -cp bin user.TestScreens
```
> Chương trình sẽ tự động mở lần lượt tất cả 18 màn hình và hộp thoại, chụp ảnh chất lượng cao và tự động lưu vào thư mục `screenshots/` để đưa vào Báo cáo đồ án.

---

## 💻 6. Khởi chạy trong IDE (IntelliJ IDEA / Eclipse / VS Code)

Nếu bạn sử dụng IDE:
1. Mở thư mục `Java-final-project` trong IDE.
2. Thiết lập thư mục `src` là **Sources Root**.
3. Đảm bảo cấu hình Encoding của dự án là **UTF-8**.
4. Mở trực tiếp các file Java chứa hàm `main` sau và nhấn nút **Run** (hoặc `Shift + F10` / `Ctrl + F5`):
   - Mở Admin: `src/admin/Main.java`
   - Mở Đăng nhập: `src/user/LoginFrame.java`
   - Mở Giao diện Chat: `src/user/UserMainFrame.java`
   - Chạy kiểm thử tự động: `src/user/TestScreens.java`
# 🖥️ Tech Blog API - Hệ Thống Quản Lý Blog Công Nghệ

Dự án **Tech Blog API** là một nền tảng backend hiện đại, được xây dựng bằng **Spring Boot** (Java 21). Hệ thống cung cấp giải pháp toàn diện cho việc quản lý nội dung số, phân quyền người dùng và tương tác cộng đồng trong một blog công nghệ.

---

## 🚀 Công Nghệ Sử Dụng (Tech Stack)

Dự án được xây dựng dựa trên các công nghệ tiên tiến nhất của hệ sinh thái Java/Spring:

- **Ngôn ngữ:** Java 21 (LTS)
- **Framework:** Spring Boot 4.0.3 (hoặc 3.x)
- **Cơ sở dữ liệu:**
  - **MySQL** (Mặc định cho môi trường phát triển)
  - **PostgreSQL** (Hỗ trợ mở rộng)
- **ORM:** Spring Data JPA (Hibernate)
- **Bảo mật:** Spring Security (Xác thực & Phân quyền chi tiết)
- **Giao diện:** Thymeleaf (Template Engine)
- **Tiện ích:**
  - **Lombok:** Giảm thiểu mã boilerplate (Getter, Setter, Constructor).
  - **Spring Validation:** Kiểm soát dữ liệu đầu vào.
  - **Thymeleaf Extras Spring Security 6:** Tích hợp bảo mật vào giao diện.
- **Quản lý dự án:** Maven

---

## ✨ Các Tính Năng Chính

Hệ thống được thiết kế với 3 phân quyền chính: **Admin**, **Author** (Tác giả), và **User** (Người dùng).

### 👨‍💼 Quản Trị Viên (Admin)

- **Bảng điều khiển (Dashboard):** Xem thống kê tổng quan về bài viết, người dùng, và tương tác.
- **Quản lý Bài viết:** Duyệt bài, xóa hoặc chỉnh sửa nội dung từ tất cả các tác giả.
- **Quản lý Danh mục & Thẻ:** Tổ chức cấu trúc blog qua các Category và Tag.
- **Quản lý Tài khoản:** Khóa/mở tài khoản, cấp quyền cho Tác giả (Author).
- **Kiểm duyệt Bình luận:** Quản lý và xóa các bình luận không phù hợp.

### ✍️ Tác Giả (Author)

- **Thống kê Cá nhân:** Theo dõi hiệu suất bài viết (Lượt xem, Lượt thích).
- **Soạn thảo Nội dung:** Tạo bài viết mới, quản lý bản nháp, theo dõi trạng thái bài viết (Draft -> Pending -> Published).
- **Tương tác:** Quản lý và phản hồi bình luận trên các bài viết của chính mình.

### 👤 Người Dùng (User)

- **Đọc & Tìm kiếm:** Tìm kiếm bài viết mạnh mẽ theo từ khóa, danh mục hoặc hashtag.
- **Tương tác Cộng đồng:** Bình luận, Thích bài viết (Like), và Thích bình luận.
- **Tiện ích Cá nhân:** Lưu bài viết (Bookmark) để đọc sau.
- **Hồ sơ:** Cập nhật thông tin cá nhân, thay đổi mật khẩu một cách an toàn.

---

## 🛠️ Cài Đặt & Chạy Dự Án

### 1. Yêu Cầu Hệ Thống

Đảm bảo máy tính của bạn đã cài đặt các công cụ sau:

- **JDK 21** trở lên.
- **Maven 3.6+**.
- **MySQL 8.0+** (hoặc PostgreSQL).

### 2. Cấu Hình Cơ Sở Dữ Liệu

Tạo một schema mới trong MySQL:

```sql
CREATE DATABASE tech_blog_db;
```

Mở file `src/main/resources/application.yaml` và cập nhật thông tin kết nối:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tech_blog_db
    username: YOUR_USERNAME (ví dụ: root)
    password: YOUR_PASSWORD
```

### 3. Khởi Chạy Ứng Dụng

Sử dụng terminal tại thư mục gốc của dự án:

```powershell
mvn spring-boot:run
```

Sau khi khởi động thành công, hệ thống sẽ chạy tại: **`http://localhost:8888`**

---

## 📁 Cấu Trúc Mã Nguồn

Dự án tuân thủ kiến trúc phân lớp (Multi-layered Architecture) chuẩn Spring Boot:

- `com.mtks04.tech_blog_api.config`: Cấu hình Security, Static resources, và App settings.
- `com.mtks04.tech_blog_api.controller`: Các đầu cuối xử lý Request (API Endpoints).
- `com.mtks04.tech_blog_api.entity`: Định nghĩa cấu trúc dữ liệu mapping với Database.
- `com.mtks04.tech_blog_api.repository`: Tầng truy vấn dữ liệu (JPA Repositories).
- `com.mtks04.tech_blog_api.service`: Tầng xử lý logic nghiệp vụ.
- `com.mtks04.tech_blog_api.dto`: Các đối tượng vận chuyển dữ liệu (Data Transfer Objects).

---

## 🛡️ Bảo Mật & Phân Quyền

Dự án sử dụng **Spring Security** với các phân quyền mặc định:

- `ROLE_ADMIN`: Quyền quản lý toàn cục.
- `ROLE_AUTHOR`: Quyền sáng tạo nội dung và quản lý bài viết cá nhân.
- `ROLE_USER`: Quyền đọc, tương tác cơ bản.

Hệ thống cũng hỗ trợ **Data Seeder** để tự động khởi tạo dữ liệu mẫu khi chạy lần đầu, giúp việc phát triển và thử nghiệm trở nên dễ dàng hơn.

---

## ✍️ Tác Giả & Bản Quyền

Dự án được phát triển và duy trì bởi đội ngũ **MT-KS-04**. Chúng tôi hy vọng nền tảng này sẽ mang lại giá trị thiết thực cho cộng đồng học tập và chia sẻ kiến thức công nghệ.

- **Phát triển bởi:** Team MT-KS-04
- **Mục đích:** Học tập & Phát triển nền tảng Blog chuyên nghiệp.
- **Giấy phép:** [Apache License 2.0](LICENSE)

---

_© 2026 **MT-KS-04**. Chúc bạn có những trải nghiệm tuyệt vời với Tech Blog!_

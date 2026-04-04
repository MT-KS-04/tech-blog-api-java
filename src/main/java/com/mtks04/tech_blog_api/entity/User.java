package com.mtks04.tech_blog_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    public enum Role {
        ADMIN,
        EDITOR,
        USER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Size(min = 3, max = 50, message = "username phai lon hon 3 ky tu va khong qua 50 ky tu")
    @NotBlank(message = "username khong duoc rong")
    private String username;

    @Column(unique = true, nullable = false)
    @Email(message = "Sai dinh dang email")
    @NotBlank(message = "Email khong duoc rong")
    private String email;

    @Column(nullable = false)
    @Size(min = 6, max = 100, message = "Password phai lon hon 6 ky tu")
    @NotBlank(message = "Password khong duoc rong")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "full_name", length = 120)
    private String fullName;

    @Column(length = 255)
    private String bio;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Dùng {@link Boolean} (không phải boolean) để các bản ghi cũ có cột NULL trong DB không gây lỗi khi đọc.
     * Giá trị mặc định được áp sau khi load ({@link #applyNotificationDefaults()}) và khi lưu mới.
     */
    @Column(name = "notify_comment_reply")
    @Builder.Default
    private Boolean notifyCommentReply = true;

    @Column(name = "notify_weekly_newsletter")
    @Builder.Default
    private Boolean notifyWeeklyNewsletter = true;

    @Column(name = "notify_author_posts")
    @Builder.Default
    private Boolean notifyAuthorPosts = false;

    @PostLoad
    private void applyNotificationDefaults() {
        if (notifyCommentReply == null) {
            notifyCommentReply = true;
        }
        if (notifyWeeklyNewsletter == null) {
            notifyWeeklyNewsletter = true;
        }
        if (notifyAuthorPosts == null) {
            notifyAuthorPosts = false;
        }
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        applyNotificationDefaults();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
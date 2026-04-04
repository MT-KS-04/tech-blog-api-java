package com.mtks04.tech_blog_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bài viết mà comment thuộc về (ManyToOne -> posts)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Người dùng để lại bình luận (ManyToOne -> users)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Noi dung binh luan khong duoc de trong")
    private String content;

    // Tự tham chiếu để hỗ trợ trả lời bình luận (nested reply)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private long likesCount;

    @Transient
    private boolean likedByCurrentUser;

    @Transient
    private boolean canDelete;

    @Transient
    @Builder.Default
    private java.util.List<Comment> replies = new java.util.ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}

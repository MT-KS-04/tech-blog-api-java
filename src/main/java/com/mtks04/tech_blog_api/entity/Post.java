package com.mtks04.tech_blog_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    public enum Status {
        DRAFT,
        PENDING,
        PUBLISHED,
        ARCHIVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Tieu de bai viet khong duoc de trong")
    private String title;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Slug khong duoc de trong")
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Noi dung bai viet khong duoc de trong")
    private String content;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    // Tác giả bài viết (ManyToOne -> users)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    // Danh mục bài viết (ManyToOne -> categories)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Điểm đánh giá sản phẩm (chỉ dùng cho bài Review)
    @Column(precision = 2, scale = 1)
    @DecimalMin(value = "1.0", message = "Rating toi thieu la 1.0")
    @DecimalMax(value = "5.0", message = "Rating toi da la 5.0")
    private BigDecimal rating;

    // Trạng thái bài viết: DRAFT, PUBLISHED, ARCHIVED
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    // Các thẻ (Tags) – mối quan hệ N-N thông qua bảng post_tags
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<PostLike> likes = new java.util.ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

package com.mtks04.tech_blog_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bài viết mà ảnh thuộc về (ManyToOne -> posts)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "image_url", nullable = false)
    @NotBlank(message = "URL anh khong duoc de trong")
    private String imageUrl;

    // Văn bản thay thế cho ảnh, tốt cho SEO hình ảnh
    @Column(name = "alt_text")
    private String altText;
}

package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // Tìm tất cả bài viết theo trạng thái (dùng cho trang Duyệt bài)
    Page<Post> findByStatus(Post.Status status, Pageable pageable);

    // Tìm bài viết theo tác giả
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    // Tìm bài viết theo danh mục
    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);
}

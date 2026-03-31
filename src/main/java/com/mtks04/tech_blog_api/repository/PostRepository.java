package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    // Dashboard: 5 bài viết mới nhất
    List<Post> findTop5ByOrderByCreatedAtDesc();

    // Dashboard: 5 bài viết có lượt xem cao nhất
    List<Post> findTop5ByOrderByViewCountDesc();

    // Dashboard: Tổng lượt xem toàn hệ thống
    @Query("SELECT COALESCE(SUM(p.viewCount), 0) FROM Post p")
    Long sumViewCount();

    // Tìm kiếm bài viết dành cho Admin (kèm phân trang)
    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:status IS NULL OR p.status = :status)")
    Page<Post> findAdminPosts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") Post.Status status,
            Pageable pageable
    );
}

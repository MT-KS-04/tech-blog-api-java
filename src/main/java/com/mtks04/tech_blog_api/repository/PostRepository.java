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
    long countByAuthorId(Long authorId);

    @Query("SELECT COALESCE(SUM(p.viewCount), 0) FROM Post p WHERE p.author.id = :authorId")
    Long sumViewCountByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(COALESCE(p.summary, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchByAuthorIdAndKeyword(
            @Param("authorId") Long authorId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

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
<<<<<<< HEAD

    @Query("SELECT p FROM Post p JOIN PostLike pl ON p.id = pl.post.id WHERE pl.user.id = :userId AND p.status = 'PUBLISHED' ORDER BY pl.createdAt DESC")
    List<Post> findLikedPostsByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p JOIN PostBookmark pb ON p.id = pb.post.id WHERE pb.user.id = :userId AND p.status = 'PUBLISHED' ORDER BY pb.createdAt DESC")
    List<Post> findBookmarkedPostsByUserId(@Param("userId") Long userId);
=======
    // Tìm kiếm công khai – chỉ bài PUBLISHED, theo keyword trong title/summary
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(COALESCE(p.summary, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchPublished(@Param("keyword") String keyword, Pageable pageable);
>>>>>>> origin/develop
}

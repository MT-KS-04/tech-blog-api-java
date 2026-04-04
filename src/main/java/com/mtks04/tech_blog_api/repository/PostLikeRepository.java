package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.entity.PostLike;
import com.mtks04.tech_blog_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    /**
     * Kiểm tra xem người dùng đã thích bài viết này chưa.
     */
    boolean existsByPostAndUser(Post post, User user);
    
    /**
     * Tìm lượt thích của người dùng cho một bài viết cụ thể.
     */
    Optional<PostLike> findByPostAndUser(Post post, User user);
    
    /**
     * Đếm tổng lượt thích cho một bài viết.
     */
    long countByPostId(Long postId);

    /**
     * Đếm tổng lượt thích trên tất cả bài viết của một tác giả.
     */
    long countByPostAuthorId(Long authorId);

    /**
     * Đếm tổng số bài viết mà một người dùng đã thích.
     */
    long countByUserId(Long userId);
}

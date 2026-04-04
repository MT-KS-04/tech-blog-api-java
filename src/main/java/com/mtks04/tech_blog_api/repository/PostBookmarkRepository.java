package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.entity.PostBookmark;
import com.mtks04.tech_blog_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {
    
    /**
     * Kiểm tra xem người dùng đã lưu bài viết này chưa.
     */
    boolean existsByPostAndUser(Post post, User user);
    
    /**
     * Tìm dấu trang của người dùng cho một bài viết cụ thể.
     */
    Optional<PostBookmark> findByPostAndUser(Post post, User user);
    
    /**
     * Đếm tổng lượt lưu cho một người dùng.
     */
    long countByUserId(Long userId);
}

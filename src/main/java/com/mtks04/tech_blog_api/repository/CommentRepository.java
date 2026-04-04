package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Lấy tất cả comment gốc (không phải reply) của một bài viết
    List<Comment> findByPostIdAndParentIsNull(Long postId);

    // Lấy tất cả reply của một comment cha
    List<Comment> findByParentId(Long parentId);

    // Lấy tất cả comment của một bài viết (dùng cho Admin quản lý)
    Page<Comment> findByPostId(Long postId, Pageable pageable);

    // Đếm số comment của một bài viết
    long countByPostId(Long postId);

    // Đếm tổng comment trên tất cả bài viết của một tác giả
    long countByPostAuthorId(Long authorId);

    long countByUser_Id(Long userId);
}

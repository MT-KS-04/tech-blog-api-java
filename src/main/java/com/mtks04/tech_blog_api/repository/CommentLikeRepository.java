package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.Comment;
import com.mtks04.tech_blog_api.entity.CommentLike;
import com.mtks04.tech_blog_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // Kiem tra xem 1 user da like 1 comment chua
    boolean existsByCommentAndUser(Comment comment, User user);

    // Tim record like cu the
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);

    // Dem tong so like cua 1 comment
    long countByCommentId(Long commentId);
    
    // Xoá tất cả like của một bình luận (Dùng trước khi delete comment)
    void deleteByCommentId(Long commentId);
}

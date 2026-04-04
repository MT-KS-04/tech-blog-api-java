package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    long countByCommentId(Long commentId);

    boolean existsByComment_IdAndUser_Id(Long commentId, Long userId);

    Optional<CommentLike> findByComment_IdAndUser_Id(Long commentId, Long userId);
}

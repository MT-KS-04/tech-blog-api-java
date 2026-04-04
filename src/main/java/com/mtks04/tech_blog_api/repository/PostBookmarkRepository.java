package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.PostBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {

    long countByUser_Id(Long userId);

    Page<PostBookmark> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);

    Optional<PostBookmark> findByPost_IdAndUser_Id(Long postId, Long userId);
}

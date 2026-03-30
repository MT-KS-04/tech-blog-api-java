package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // Lấy toàn bộ ảnh của một bài viết
    List<PostImage> findByPostId(Long postId);

    // Xóa toàn bộ ảnh của một bài viết (dùng khi xóa post)
    void deleteByPostId(Long postId);
}

package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final PostRepository postRepository;

    /**
     * Tìm kiếm và lọc bài viết dành cho Admin
     */
    public Page<Post> searchPosts(String keyword, Long categoryId, Post.Status status, Pageable pageable) {
        // Chuyển rỗng thành null để query dễ hơn
        String searchKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        return postRepository.findAdminPosts(searchKeyword, categoryId, status, pageable);
    }

    /**
     * Cập nhật trạng thái bài viết (DRAFT, PUBLISHED, ARCHIVED)
     */
    @Transactional
    public void updateStatus(Long id, Post.Status status) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));
        post.setStatus(status);
        postRepository.save(post);
    }

    /**
     * Xóa vĩnh viễn bài viết
     */
    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Không thể xóa: Không tìm thấy bài viết ID " + id);
        }
        postRepository.deleteById(id);
    }

    /**
     * Duyệt bài viết (Chuyển sang PUBLISHED)
     */
    @Transactional
    public void approvePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết ID " + id));
        post.setStatus(Post.Status.PUBLISHED);
        postRepository.save(post);
    }

    /**
     * Từ chối bài viết (Chuyển về DRAFT)
     */
    @Transactional
    public void rejectPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết ID " + id));
        post.setStatus(Post.Status.DRAFT);
        postRepository.save(post);
    }
}

package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.entity.PostBookmark;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.PostBookmarkRepository;
import com.mtks04.tech_blog_api.repository.PostRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostBookmarkService {

    private final PostBookmarkRepository postBookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void toggleBookmark(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        if (post.getStatus() != Post.Status.PUBLISHED) {
            throw new RuntimeException("Chỉ có thể lưu bài đã xuất bản");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Optional<PostBookmark> existing = postBookmarkRepository.findByPost_IdAndUser_Id(postId, user.getId());
        if (existing.isPresent()) {
            postBookmarkRepository.delete(existing.get());
        } else {
            postBookmarkRepository.save(PostBookmark.builder()
                    .post(post)
                    .user(user)
                    .build());
        }
    }

    public boolean isBookmarked(Long postId, Long userId) {
        if (userId == null) {
            return false;
        }
        return postBookmarkRepository.existsByPost_IdAndUser_Id(postId, userId);
    }
}

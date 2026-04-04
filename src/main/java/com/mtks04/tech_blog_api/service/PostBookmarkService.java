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

    /**
     * Logic Bookmark/Un-bookmark bài viết.
     */
    @Transactional
    public boolean toggleBookmark(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết ID: " + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + userId));

        Optional<PostBookmark> existingBookmark = postBookmarkRepository.findByPostAndUser(post, user);

        if (existingBookmark.isPresent()) {
            postBookmarkRepository.delete(existingBookmark.get());
            return false; // Removed
        } else {
            PostBookmark bookmark = PostBookmark.builder()
                    .post(post)
                    .user(user)
                    .build();
            postBookmarkRepository.save(bookmark);
            return true; // Added
        }
    }
}

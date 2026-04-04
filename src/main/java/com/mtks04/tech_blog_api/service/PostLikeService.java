package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.entity.PostLike;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.PostLikeRepository;
import com.mtks04.tech_blog_api.repository.PostRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * Logic Like/Unlike bài viết.
     * Nếu đã like -> Thao tác xóa (Unlike).
     * Nếu chưa like -> Thao tác thêm (Like).
     */
    @Transactional
    public void toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết ID: " + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + userId));

        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            // Unlike
            postLikeRepository.delete(existingLike.get());
        } else {
            // Like
            PostLike newLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(newLike);
        }
    }

    @Transactional
    public void toggleLikeByUsername(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        toggleLike(postId, user.getId());
    }

    public boolean isLikedByUser(Long postId, Long userId) {
        if (userId == null) {
            return false;
        }
        return postLikeRepository.existsByPost_IdAndUser_Id(postId, userId);
    }
}

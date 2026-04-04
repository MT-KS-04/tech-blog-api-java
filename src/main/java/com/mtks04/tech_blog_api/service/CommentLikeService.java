package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.entity.Comment;
import com.mtks04.tech_blog_api.entity.CommentLike;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.CommentLikeRepository;
import com.mtks04.tech_blog_api.repository.CommentRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void toggle(Long commentId, Long postId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));
        if (!comment.getPost().getId().equals(postId)) {
            throw new RuntimeException("Bình luận không thuộc bài viết này");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Optional<CommentLike> existing = commentLikeRepository.findByComment_IdAndUser_Id(commentId, user.getId());
        if (existing.isPresent()) {
            commentLikeRepository.delete(existing.get());
        } else {
            commentLikeRepository.save(CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build());
        }
    }
}

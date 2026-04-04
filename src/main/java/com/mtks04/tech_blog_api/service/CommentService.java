package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.dto.CommentThreadItem;
import com.mtks04.tech_blog_api.entity.Comment;
import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.CommentLikeRepository;
import com.mtks04.tech_blog_api.repository.CommentRepository;
import com.mtks04.tech_blog_api.repository.PostRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommentThreadItem> buildThread(Long postId, Long currentUserId) {
        List<Comment> roots = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId);
        return roots.stream()
                .map(c -> toItem(c, currentUserId))
                .toList();
    }

    private CommentThreadItem toItem(Comment comment, Long currentUserId) {
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
        List<CommentThreadItem> replyItems = replies.stream()
                .map(r -> toItem(r, currentUserId))
                .toList();

        long likeCount = commentLikeRepository.countByCommentId(comment.getId());
        boolean liked = currentUserId != null
                && commentLikeRepository.existsByComment_IdAndUser_Id(comment.getId(), currentUserId);

        User author = comment.getUser();
        String display = displayName(author);

        return new CommentThreadItem(
                comment.getId(),
                display,
                comment.getContent(),
                comment.getCreatedAt(),
                likeCount,
                liked,
                replyItems
        );
    }

    private static String displayName(User user) {
        if (user == null) {
            return "Ẩn danh";
        }
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName().trim();
        }
        return user.getUsername();
    }

    @Transactional
    public void addComment(Long postId, String username, String content, Long parentId) {
        if (content == null || content.isBlank()) {
            throw new RuntimeException("Nội dung bình luận không được để trống");
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        if (post.getStatus() != Post.Status.PUBLISHED) {
            throw new RuntimeException("Không thể bình luận bài viết này");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Comment parent = null;
        if (parentId != null) {
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận gốc"));
            if (!parent.getPost().getId().equals(postId)) {
                throw new RuntimeException("Bình luận không thuộc bài viết này");
            }
        }

        Comment built = Comment.builder()
                .post(post)
                .user(user)
                .content(content.trim())
                .parent(parent)
                .build();
        commentRepository.save(built);
    }
}

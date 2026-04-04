package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.PostLikeRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import com.mtks04.tech_blog_api.entity.Comment;
import com.mtks04.tech_blog_api.entity.CommentLike;
import com.mtks04.tech_blog_api.repository.CommentRepository;
import com.mtks04.tech_blog_api.repository.CommentLikeRepository;
import com.mtks04.tech_blog_api.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final com.mtks04.tech_blog_api.repository.PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeService postLikeService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final com.mtks04.tech_blog_api.repository.PostBookmarkRepository postBookmarkRepository;
    private final com.mtks04.tech_blog_api.service.PostBookmarkService postBookmarkService;

    @GetMapping("/post/{slug}")
    public String viewPostDetail(@PathVariable String slug, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Post> postOptional = postRepository.findBySlug(slug);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            // Tăng viewCount
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);

            model.addAttribute("post", post);
            
            // Xử lý thông tin Like
            long likesCount = postLikeRepository.countByPostId(post.getId());
            model.addAttribute("likesCount", likesCount);
            
            // Xử lý tổng số lượng bình luận chính xác (bao gồm cả phản hồi)
            long totalComments = commentRepository.countByPostId(post.getId());
            model.addAttribute("totalComments", totalComments);
            
            boolean isLiked = false;
            boolean isBookmarked = false;
            if (userDetails != null) {
                User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
                if (user != null) {
                    isLiked = postLikeRepository.existsByPostAndUser(post, user);
                    isBookmarked = postBookmarkRepository.existsByPostAndUser(post, user);
                }
            }
            model.addAttribute("isLiked", isLiked);
            model.addAttribute("isBookmarked", isBookmarked);
            
            // Xử lý thông tin Comment
            java.util.List<Comment> comments = commentRepository.findByPostIdAndParentIsNull(post.getId());
            User currentUserForComment = null;
            if (userDetails != null) {
                currentUserForComment = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            }
            final User finalCurrentUser = currentUserForComment;

            for (Comment c : comments) {
                c.setLikesCount(commentLikeRepository.countByCommentId(c.getId()));
                if (finalCurrentUser != null) {
                    c.setLikedByCurrentUser(commentLikeRepository.existsByCommentAndUser(c, finalCurrentUser));
                    c.setCanDelete(c.getUser().getId().equals(finalCurrentUser.getId()) || finalCurrentUser.getRole() == User.Role.ADMIN);
                }
                // Load replies for each parent comment
                java.util.List<Comment> replies = commentRepository.findByParentId(c.getId());
                for (Comment r : replies) {
                    r.setLikesCount(commentLikeRepository.countByCommentId(r.getId()));
                    if (finalCurrentUser != null) {
                        r.setLikedByCurrentUser(commentLikeRepository.existsByCommentAndUser(r, finalCurrentUser));
                        r.setCanDelete(r.getUser().getId().equals(finalCurrentUser.getId()) || finalCurrentUser.getRole() == User.Role.ADMIN);
                    }
                }
                c.setReplies(replies);
            }
            model.addAttribute("comments", comments);
            
            // Bài viết liên quan
            model.addAttribute("relatedPosts", postRepository.findTop5ByOrderByCreatedAtDesc().subList(0, Math.min(3, postRepository.findTop5ByOrderByCreatedAtDesc().size())));

            return "post-detail";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/api/posts/{postId}/like")
    @ResponseBody
    public ResponseEntity<?> toggleLikeApi(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui lòng đăng nhập để yêu thích chức năng này."));
        }
        
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Tài khoản không tồn tại."));
        }
        
        postLikeService.toggleLike(postId, user.getId());
        
        long count = postLikeRepository.countByPostId(postId);
        Post post = postRepository.findById(postId).orElse(null);
        boolean isLiked = false;
        if (post != null) {
            isLiked = postLikeRepository.existsByPostAndUser(post, user);
        }
        
        
        return ResponseEntity.ok(Map.of("liked", isLiked, "count", count));
    }

    @PostMapping("/api/posts/{postId}/bookmark")
    @ResponseBody
    public ResponseEntity<?> toggleBookmarkApi(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui lòng đăng nhập để lưu bài viết."));
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Tài khoản không tồn tại."));
        }

        boolean saved = postBookmarkService.toggleBookmark(postId, user.getId());
        return ResponseEntity.ok(Map.of("bookmarked", saved));
    }

    @PostMapping("/api/posts/{postId}/comment")
    @ResponseBody
    public ResponseEntity<?> addCommentApi(@PathVariable Long postId, @RequestBody Map<String, String> payload, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui lòng đăng nhập để bình luận."));
        }

        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nội dung bình luận không được để trống."));
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Tài khoản không tồn tại."));
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Bài viết không tồn tại."));
        }

        // Hỗ trợ reply: kiểm tra parentId
        Comment parent = null;
        String parentIdStr = payload.get("parentId");
        if (parentIdStr != null && !parentIdStr.isBlank()) {
            try {
                Long parentId = Long.parseLong(parentIdStr);
                parent = commentRepository.findById(parentId).orElse(null);
            } catch (NumberFormatException ignored) {}
        }

        Comment comment = Comment.builder()
                .content(content.trim())
                .post(post)
                .user(user)
                .parent(parent)
                .build();
        
        Comment savedComment = commentRepository.save(comment);

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDate = savedComment.getCreatedAt() != null ? savedComment.getCreatedAt().format(formatter) : java.time.LocalDateTime.now().format(formatter);

        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("id", savedComment.getId());
        resp.put("content", savedComment.getContent());
        resp.put("username", user.getUsername());
        resp.put("createdAt", formattedDate);
        resp.put("likesCount", 0);
        resp.put("isLikedByCurrentUser", false);
        resp.put("canDelete", true);
        resp.put("parentId", parent != null ? parent.getId() : null);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/api/comments/{commentId}/like")
    @ResponseBody
    public ResponseEntity<?> toggleCommentLikeApi(@PathVariable Long commentId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui lòng đăng nhập để yêu thích bình luận."));
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Tài khoản không tồn tại."));
        }

        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Bình luận không tồn tại."));
        }

        boolean isLiked = false;
        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndUser(comment, user);
        if (existingLike.isPresent()) {
            commentLikeRepository.delete(existingLike.get());
        } else {
            CommentLike commentLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(commentLike);
            isLiked = true;
        }

        long count = commentLikeRepository.countByCommentId(commentId);
        return ResponseEntity.ok(Map.of("liked", isLiked, "count", count));
    }

    @DeleteMapping("/api/comments/{commentId}")
    @ResponseBody
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> deleteCommentApi(@PathVariable Long commentId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui lòng đăng nhập để xóa bình luận."));
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        Comment comment = commentRepository.findById(commentId).orElse(null);
        
        if (user == null || comment == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy dữ liệu hợp lệ."));
        }

        // Kiểm tra quyền: Chỉ tác giả hoặc ADMIN mới được xóa
        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Bạn không có quyền xóa bình luận này."));
        }

        // Nếu là comment gốc, xóa các likes của replies và xóa các replies trước
        if (comment.getParent() == null) {
            java.util.List<Comment> replies = commentRepository.findByParentId(commentId);
            for (Comment r : replies) {
                commentLikeRepository.deleteByCommentId(r.getId());
            }
            commentRepository.deleteByParentId(commentId);
        }

        // Xóa likes của chính comment đó
        commentLikeRepository.deleteByCommentId(commentId);
        
        // Xóa chính comment
        commentRepository.delete(comment);

        return ResponseEntity.ok(Map.of("message", "Xóa thành công"));
    }
}

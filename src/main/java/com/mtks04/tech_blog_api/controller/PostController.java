package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.dto.CommentThreadItem;
import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.CommentRepository;
import com.mtks04.tech_blog_api.repository.PostLikeRepository;
import com.mtks04.tech_blog_api.repository.PostRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import com.mtks04.tech_blog_api.service.CommentLikeService;
import com.mtks04.tech_blog_api.service.CommentService;
import com.mtks04.tech_blog_api.service.PostBookmarkService;
import com.mtks04.tech_blog_api.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final PostBookmarkService postBookmarkService;
    private final PostLikeService postLikeService;
    private final PostLikeRepository postLikeRepository;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @GetMapping("/post/{slug}")
    public String viewPost(@PathVariable String slug, Model model,
                           @AuthenticationPrincipal UserDetails principal) {
        Optional<Post> optionalPost = postRepository.findBySlug(slug);

        if (optionalPost.isEmpty()) {
            return "error/404";
        }

        Post post = optionalPost.get();

        if (post.getStatus() != Post.Status.PUBLISHED) {
            return "error/404";
        }

        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        String htmlContent = convertTextToHtml(post.getContent());

        model.addAttribute("post", post);
        model.addAttribute("htmlContent", htmlContent);

        long likeCount = postLikeRepository.countByPostId(post.getId());
        long commentCount = commentRepository.countByPostId(post.getId());
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("commentCount", commentCount);

        Long currentUserId = null;
        if (principal != null) {
            Optional<User> uOpt = userRepository.findByUsername(principal.getUsername());
            if (uOpt.isPresent()) {
                User u = uOpt.get();
                currentUserId = u.getId();
                model.addAttribute("postBookmarked", postBookmarkService.isBookmarked(post.getId(), u.getId()));
                model.addAttribute("postLiked", postLikeService.isLikedByUser(post.getId(), u.getId()));
            } else {
                model.addAttribute("postBookmarked", false);
                model.addAttribute("postLiked", false);
            }
        } else {
            model.addAttribute("postBookmarked", false);
            model.addAttribute("postLiked", false);
        }

        List<CommentThreadItem> comments = commentService.buildThread(post.getId(), currentUserId);
        model.addAttribute("comments", comments);

        enrichAuthorBio(model, post);

        return "post-detail";
    }

    /** Hiển thị bio tác giả nếu có (tránh null trên template). */
    private void enrichAuthorBio(Model model, Post post) {
        if (post.getAuthor() != null && post.getAuthor().getBio() != null && !post.getAuthor().getBio().isBlank()) {
            model.addAttribute("authorBioDisplay", post.getAuthor().getBio().trim());
        } else {
            model.addAttribute("authorBioDisplay", "Tác giả TechPulse");
        }
    }

    @PostMapping("/post/{slug}/bookmark")
    public String toggleBookmark(@PathVariable String slug,
                                 @AuthenticationPrincipal UserDetails principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        Post post = postRepository.findBySlug(slug).orElseThrow();
        if (post.getStatus() != Post.Status.PUBLISHED) {
            return "error/404";
        }
        try {
            postBookmarkService.toggleBookmark(post.getId(), principal.getUsername());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("postActionError", e.getMessage());
        }
        return "redirect:/post/" + slug + "#post-interactions";
    }

    @PostMapping("/post/{slug}/like")
    public String togglePostLike(@PathVariable String slug,
                                 @AuthenticationPrincipal UserDetails principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        Post post = postRepository.findBySlug(slug).orElseThrow();
        if (post.getStatus() != Post.Status.PUBLISHED) {
            return "error/404";
        }
        try {
            postLikeService.toggleLikeByUsername(post.getId(), principal.getUsername());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("postActionError", e.getMessage());
        }
        return "redirect:/post/" + slug + "#post-interactions";
    }

    @PostMapping("/post/{slug}/comment")
    public String addComment(@PathVariable String slug,
                             @AuthenticationPrincipal UserDetails principal,
                             @RequestParam String content,
                             @RequestParam(required = false) Long parentId,
                             RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        Post post = postRepository.findBySlug(slug).orElseThrow();
        if (post.getStatus() != Post.Status.PUBLISHED) {
            return "error/404";
        }
        try {
            commentService.addComment(post.getId(), principal.getUsername(), content, parentId);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("postActionError", e.getMessage());
        }
        return "redirect:/post/" + slug + "#comments";
    }

    @PostMapping("/post/{slug}/comment/{commentId}/like")
    public String toggleCommentLike(@PathVariable String slug,
                                    @PathVariable Long commentId,
                                    @AuthenticationPrincipal UserDetails principal,
                                    RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        Post post = postRepository.findBySlug(slug).orElseThrow();
        if (post.getStatus() != Post.Status.PUBLISHED) {
            return "error/404";
        }
        try {
            commentLikeService.toggle(commentId, post.getId(), principal.getUsername());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("postActionError", e.getMessage());
        }
        return "redirect:/post/" + slug + "#comment-" + commentId;
    }

    private String convertTextToHtml(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String escaped = text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");

        String[] paragraphs = escaped.split("(\\r?\\n){2,}");

        StringBuilder sb = new StringBuilder();
        for (String para : paragraphs) {
            String trimmed = para.trim();
            if (!trimmed.isEmpty()) {
                String withBr = trimmed.replace("\r\n", "<br>").replace("\n", "<br>");
                sb.append("<p>").append(withBr).append("</p>\n");
            }
        }

        return sb.toString();
    }
}

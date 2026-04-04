package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.repository.PostRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import com.mtks04.tech_blog_api.service.PostBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final PostBookmarkService postBookmarkService;
    private final UserRepository userRepository;

    @GetMapping("/post/{slug}")
    public String viewPost(@PathVariable String slug, Model model,
                           @AuthenticationPrincipal UserDetails principal) {
        Optional<Post> optionalPost = postRepository.findBySlug(slug);

        if (optionalPost.isEmpty()) {
            return "error/404";
        }

        Post post = optionalPost.get();

        // Chỉ cho xem bài đã PUBLISHED
        if (post.getStatus() != Post.Status.PUBLISHED) {
            return "error/404";
        }

        // Tăng lượt xem
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        // Chuyển đổi nội dung text thành HTML (giữ xuống dòng)
        String htmlContent = convertTextToHtml(post.getContent());

        model.addAttribute("post", post);
        model.addAttribute("htmlContent", htmlContent);

        if (principal != null) {
            userRepository.findByUsername(principal.getUsername()).ifPresent(u ->
                    model.addAttribute("postBookmarked", postBookmarkService.isBookmarked(post.getId(), u.getId())));
        } else {
            model.addAttribute("postBookmarked", false);
        }
        return "post-detail";
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
        return "redirect:/post/" + slug;
    }

    /**
     * Chuyển đổi nội dung plain text thành HTML:
     * - Escape HTML đặc biệt
     * - Đoạn trống (2 dòng trống) → thẻ &lt;p&gt;
     * - Xuống dòng đơn → &lt;br&gt;
     */
    private String convertTextToHtml(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        // Escape HTML để tránh XSS
        String escaped = text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");

        // Tách theo đoạn (2+ dòng trống)
        String[] paragraphs = escaped.split("(\\r?\\n){2,}");

        StringBuilder sb = new StringBuilder();
        for (String para : paragraphs) {
            String trimmed = para.trim();
            if (!trimmed.isEmpty()) {
                // Xuống dòng đơn → <br>
                String withBr = trimmed.replace("\r\n", "<br>").replace("\n", "<br>");
                sb.append("<p>").append(withBr).append("</p>\n");
            }
        }

        return sb.toString();
    }
}

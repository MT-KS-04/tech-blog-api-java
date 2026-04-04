package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;

    @GetMapping("/post/{slug}")
    public String viewPost(@PathVariable String slug, Model model) {
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
        return "post-detail";
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

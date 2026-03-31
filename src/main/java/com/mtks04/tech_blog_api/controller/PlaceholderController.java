package com.mtks04.tech_blog_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller này dùng để giữ chỗ cho các tính năng chưa xây dựng xong.
 * Giúp tránh hiện thị lỗi thô sơ trong quá trình dev và làm đẹp giao diện.
 */
@Controller
public class PlaceholderController {

    // --- Admin Placeholders ---

    @GetMapping({ "/admin/posts", "/admin/posts/**" })
    public String adminPosts() {
        return "error/404";
    }

    @GetMapping({ "/admin/comments", "/admin/comments/**" })
    public String adminComments() {
        return "error/404";
    }

    // --- Public Placeholders ---

    @GetMapping({ "/blog", "/blog/**" })
    public String publicBlog() {
        return "error/404";
    }

    @GetMapping({ "/post/**" })
    public String publicPostDetail() {
        return "error/404";
    }

    @GetMapping({ "/author/**" })
    public String publicAuthorProfile() {
        return "error/404";
    }

    @GetMapping({ "/user/profile", "/user/profile/**" })
    public String userProfile() {
        return "error/404";
    }

    @GetMapping({ "/search", "/search/**" })
    public String searchPlaceholder() {
        return "error/404";
    }
}

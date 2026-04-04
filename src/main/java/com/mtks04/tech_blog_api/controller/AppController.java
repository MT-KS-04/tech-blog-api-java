package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.config.AppProperties;
import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.repository.PostRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class AppController {
    private final AppProperties appProperties;
    private final PostRepository postRepository;

    public AppController(AppProperties appProperties, PostRepository postRepository) {
        this.appProperties = appProperties;
        this.postRepository = postRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Lấy 2 bài viết featured (mới nhất đã PUBLISHED)
        List<Post> featuredPosts = postRepository.findByStatus(
            Post.Status.PUBLISHED,
            PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        // Lấy 6 bài viết latest (tiếp theo, sau 2 featured)
        List<Post> latestPosts = postRepository.findByStatus(
            Post.Status.PUBLISHED,
            PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        model.addAttribute("featuredPosts", featuredPosts);
        model.addAttribute("latestPosts", latestPosts);

        return "index";
    }
}

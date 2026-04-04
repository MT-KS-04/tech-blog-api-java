package com.mtks04.tech_blog_api.controller;

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
    private final PostRepository postRepository;

    public AppController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Bài viết nổi bật: top 2 bài PUBLISHED có lượt xem cao nhất
        List<Post> featuredPosts = postRepository.findByStatus(
                Post.Status.PUBLISHED,
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "viewCount"))
        ).getContent();

        // Bài viết mới nhất: top 6 bài PUBLISHED mới nhất
        List<Post> latestPosts = postRepository.findByStatus(
                Post.Status.PUBLISHED,
                PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        model.addAttribute("featuredPosts", featuredPosts);
        model.addAttribute("latestPosts", latestPosts);

        return "index";
    }
}

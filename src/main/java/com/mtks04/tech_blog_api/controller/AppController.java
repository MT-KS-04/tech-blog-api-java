package com.mtks04.tech_blog_api.controller;

<<<<<<< HEAD
import com.mtks04.tech_blog_api.config.AppProperties;
=======
>>>>>>> origin/develop
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
<<<<<<< HEAD
    private final AppProperties appProperties;
    private final PostRepository postRepository;

    public AppController(AppProperties appProperties, PostRepository postRepository) {
        this.appProperties = appProperties;
=======
    private final PostRepository postRepository;

    public AppController(PostRepository postRepository) {
>>>>>>> origin/develop
        this.postRepository = postRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
<<<<<<< HEAD
        // Lấy 2 bài viết featured (mới nhất đã PUBLISHED)
        List<Post> featuredPosts = postRepository.findByStatus(
            Post.Status.PUBLISHED,
            PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        // Lấy 6 bài viết latest (tiếp theo, sau 2 featured)
        List<Post> latestPosts = postRepository.findByStatus(
            Post.Status.PUBLISHED,
            PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt"))
=======
        // Bài viết nổi bật: top 2 bài PUBLISHED có lượt xem cao nhất
        List<Post> featuredPosts = postRepository.findByStatus(
                Post.Status.PUBLISHED,
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "viewCount"))
        ).getContent();

        // Bài viết mới nhất: top 6 bài PUBLISHED mới nhất
        List<Post> latestPosts = postRepository.findByStatus(
                Post.Status.PUBLISHED,
                PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt"))
>>>>>>> origin/develop
        ).getContent();

        model.addAttribute("featuredPosts", featuredPosts);
        model.addAttribute("latestPosts", latestPosts);

        return "index";
    }
}

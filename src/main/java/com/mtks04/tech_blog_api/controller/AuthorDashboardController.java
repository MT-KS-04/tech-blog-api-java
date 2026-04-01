package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.repository.CategoryRepository;
import com.mtks04.tech_blog_api.repository.PostRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import com.mtks04.tech_blog_api.service.AuthorPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/author")
@RequiredArgsConstructor
public class AuthorDashboardController {

    private final AuthorPostService authorPostService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        String username = authentication.getName();
        
        Page<Post> postsPage = authorPostService.getPostsByAuthor(username, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        
        model.addAttribute("postsPage", postsPage);
        model.addAttribute("posts", postsPage.getContent());
        
        // Optional stats (simplified for UI)
        long totalPosts = postsPage.getTotalElements();
        model.addAttribute("totalPosts", totalPosts);
        
        return "author/dashboard";
    }

    @GetMapping("/create-post")
    public String createPostForm(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "author/create-post";
    }

    @PostMapping("/create-post")
    public String createPost(@RequestParam String title,
                             @RequestParam String summary,
                             @RequestParam String content,
                             @RequestParam(required = false) String coverUrl,
                             @RequestParam Long categoryId,
                             @RequestParam String action,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            Post.Status status = action.equals("publish") ? Post.Status.PUBLISHED : Post.Status.DRAFT;
            authorPostService.createPost(username, title, summary, content, coverUrl, categoryId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Đã lưu bài viết thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/author/dashboard";
    }

    @GetMapping("/edit-post/{id}")
    public String editPostForm(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        Post post = authorPostService.getPostByIdForAuthor(id, username);
        model.addAttribute("post", post);
        model.addAttribute("categories", categoryRepository.findAll());
        return "author/edit-post";
    }

    @PostMapping("/edit-post/{id}")
    public String updatePost(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String summary,
                             @RequestParam String content,
                             @RequestParam(required = false) String coverUrl,
                             @RequestParam Long categoryId,
                             @RequestParam String action,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            Post.Status status = action.equals("publish") ? Post.Status.PUBLISHED : Post.Status.DRAFT;
            authorPostService.updatePost(id, username, title, summary, content, coverUrl, categoryId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật bài viết thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/author/dashboard";
    }

    @PostMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            authorPostService.deletePost(id, username);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xoá bài viết.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/author/dashboard";
    }
}
package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.Category;
import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.repository.CategoryRepository;
import com.mtks04.tech_blog_api.service.AdminPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {

    private final AdminPostService adminPostService;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String listPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Post.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = adminPostService.searchPosts(keyword, categoryId, status, pageable);

        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("postPage", postPage);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", Post.Status.values());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("activePage", "posts");
        model.addAttribute("currentUser", userDetails != null ? userDetails.getUsername() : null);

        return "admin/posts/list";
    }

    @PostMapping("/update-status")
    public String updateStatus(
            @RequestParam Long postId,
            @RequestParam Post.Status status,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminPostService.updateStatus(postId, status);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái bài viết thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/posts";
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminPostService.deletePost(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa vĩnh viễn bài viết khỏi hệ thống!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/posts";
    }
}

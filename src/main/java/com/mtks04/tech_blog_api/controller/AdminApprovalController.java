package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.Post;
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

@Controller
@RequestMapping("/admin/approvals")
@RequiredArgsConstructor
public class AdminApprovalController {

    private final AdminPostService adminPostService;

    /**
     * Danh sách bài chờ duyệt
     */
    @GetMapping
    public String listPendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Lọc trực tiếp các bài có trạng thái PENDING
        Page<Post> postPage = adminPostService.searchPosts(null, null, Post.Status.PENDING, pageable);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("postPage", postPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("activePage", "approvals");
        model.addAttribute("currentUser", userDetails != null ? userDetails.getUsername() : null);

        return "admin/approvals/test_list";
    }

    /**
     * Đồng ý duyệt bài (Chuyển sang PUBLISHED)
     */
    @PostMapping("/approve/{id}")
    public String approvePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminPostService.approvePost(id);
            redirectAttributes.addFlashAttribute("success", "Bài viết đã được duyệt và xuất bản!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/approvals";
    }

    /**
     * Từ chối bài viết (Chuyển về DRAFT)
     */
    @PostMapping("/reject/{id}")
    public String rejectPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminPostService.rejectPost(id);
            redirectAttributes.addFlashAttribute("success", "Bài viết đã bị từ chối và chuyển về bản nháp.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/approvals";
    }
}

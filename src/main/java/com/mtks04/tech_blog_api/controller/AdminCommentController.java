package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.Comment;
import com.mtks04.tech_blog_api.service.AdminCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @GetMapping
    public String listComments(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<Comment> comments = adminCommentService.getAllComments(keyword);
        model.addAttribute("comments", comments);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage", "comments");
        return "admin/comments/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminCommentService.deleteComment(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa bình luận thành công (bao gồm cả các phản hồi)!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa bình luận: " + e.getMessage());
        }
        return "redirect:/admin/comments";
    }
}

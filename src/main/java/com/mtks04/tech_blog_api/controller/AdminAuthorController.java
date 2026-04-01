package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.dto.AuthorStatsDto;
import com.mtks04.tech_blog_api.service.AdminAuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/authors")
@RequiredArgsConstructor
public class AdminAuthorController {

    private final AdminAuthorService adminAuthorService;

    @GetMapping
    public String listAuthors(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        List<AuthorStatsDto> authors = adminAuthorService.getAuthorsWithStats();
        
        model.addAttribute("authors", authors);
        model.addAttribute("activePage", "authors");
        model.addAttribute("currentUser", userDetails != null ? userDetails.getUsername() : null);
        
        return "admin/authors/list";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleAuthorStatus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminAuthorService.toggleAuthorStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái tài khoản tác giả!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/authors";
    }
}

package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public String listUsers(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        
        Page<User> userPage = adminUserService.getAllUsers(keyword, page, size);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        
        return "admin/users/list";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminUserService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "Da cap nhat trang thai nguoi dung.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/update-role")
    public String updateRole(@RequestParam("userId") Long userId, 
                             @RequestParam("role") User.Role role, 
                             RedirectAttributes redirectAttributes) {
        try {
            adminUserService.updateUserRole(userId, role);
            redirectAttributes.addFlashAttribute("success", "Da cap nhat quyen han nguoi dung.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminUserService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Da xoa vinh vien nguoi dung.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}

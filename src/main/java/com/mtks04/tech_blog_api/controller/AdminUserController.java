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

    /**
     * Hiển thị danh sách người dùng kèm phân trang và tìm kiếm
     */
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
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("pageSize", 10);
        model.addAttribute("keyword", keyword);
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("activePage", "users");
        
        return "admin/users/list";
    }

    /**
     * Thay đổi trạng thái Khóa/Mở khóa tài khoản
     */
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminUserService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "Da cap nhat trang thai nguoi dung thanh cong!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Loi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * Cập nhật quyền hạn cho tài khoản
     */
    @PostMapping("/update-role")
    public String updateRole(
            @RequestParam Long userId, 
            @RequestParam String role, 
            RedirectAttributes redirectAttributes) {
        try {
            adminUserService.updateRole(userId, role);
            redirectAttributes.addFlashAttribute("success", "Da cap nhat quyen han thanh cong!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Loi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * Xóa vĩnh viễn tài khoản
     */
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminUserService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Da xoa nguoi dung vinh vien khoi he thong!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Loi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}

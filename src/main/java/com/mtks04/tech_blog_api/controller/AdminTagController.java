package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.Tag;
import com.mtks04.tech_blog_api.service.AdminTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final AdminTagService adminTagService;

    @GetMapping
    public String listTags(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<Tag> tags = adminTagService.getAllTags(keyword);
        model.addAttribute("tags", tags);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage", "tags");
        return "admin/tags/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("tag", new Tag());
        model.addAttribute("activePage", "tags");
        return "admin/tags/form";
    }

    @PostMapping("/save")
    public String saveTag(@Valid @ModelAttribute("tag") Tag tag,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "tags");
            return "admin/tags/form";
        }
        try {
            adminTagService.saveTag(tag);
            redirectAttributes.addFlashAttribute("success", "Đã lưu thẻ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Tag tag = adminTagService.getTagById(id);
            model.addAttribute("tag", tag);
            model.addAttribute("activePage", "tags");
            return "admin/tags/form";
        } catch (Exception e) {
            return "redirect:/admin/tags";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteTag(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminTagService.deleteTag(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa thẻ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/tags";
    }
}

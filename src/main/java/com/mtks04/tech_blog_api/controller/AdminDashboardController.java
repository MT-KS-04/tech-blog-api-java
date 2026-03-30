package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping
    public String dashboard(Model model) {
        // Stats Cards
        model.addAttribute("totalPosts",    dashboardService.countTotalPosts());
        model.addAttribute("totalUsers",    dashboardService.countTotalUsers());
        model.addAttribute("totalComments", dashboardService.countTotalComments());
        model.addAttribute("totalViews",    dashboardService.getTotalViews());

        // Post Lists
        model.addAttribute("recentPosts",   dashboardService.getRecentPosts());
        model.addAttribute("topPosts",      dashboardService.getTopViewedPosts());

        model.addAttribute("activePage", "dashboard");

        return "admin/dashboard";
    }
}

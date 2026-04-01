package com.mtks04.tech_blog_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/author")
public class AuthorDashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "author/dashboard";
    }
}
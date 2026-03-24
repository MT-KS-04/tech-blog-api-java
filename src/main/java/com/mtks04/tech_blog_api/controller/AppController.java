package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.config.AppProperties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import  org.springframework.web.bind.annotation.RequestMapping;

import java.lang.management.ManagementFactory;
import java.time.Instant;

@Controller
@RequestMapping("/")
public class AppController {
    private final AppProperties appProperties;

    public AppController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Hệ thống đang hoạt động tốt!");
        model.addAttribute("status", "ok");
        model.addAttribute("serviceName", "tech-blog-api");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("environment", appProperties.getEnvironment());
        model.addAttribute("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0);
        model.addAttribute("server", "Spring Boot MVC");
        model.addAttribute("docs", "https://docs.tech-blog-api.mk-ts-04.com");
        model.addAttribute("timestamp", Instant.now().toString());

        return "index";
    }
}

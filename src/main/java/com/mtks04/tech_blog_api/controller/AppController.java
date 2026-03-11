package com.mtks04.tech_blog_api.controller;

/**
 * Custom Modules
 */
import com.mtks04.tech_blog_api.config.AppProperties;

/**
 * Spring Modules
 */
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import  org.springframework.web.bind.annotation.RequestMapping;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController()
@RequestMapping("/")
public class AppController {
    private final AppProperties appProperties;

    public AppController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/")
    Map<String, Object> home() {
        Map<String, Object> res = new LinkedHashMap<>();

        res.put("message", "API is live");
        res.put("status", "ok");
        res.put("serviceName", "tect-blog-api");
        res.put("version", "1.0.0");
        res.put("environment", appProperties.getEnvironment());
        // Tính uptime (giây)
        res.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0);
        res.put("server", "Spring Boot + Java");
        res.put("docs", "https://docs.tech-blog-api.mk-ts-04.com");
        res.put("timestamp", Instant.now().toString());

        return  res;
    }
}

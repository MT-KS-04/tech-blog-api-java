package com.mtks04.tech_blog_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import  org.springframework.web.bind.annotation.RequestMapping;

import  java.util.Map;

@RestController()
@RequestMapping("/")
public class RootController {
    @GetMapping()
    public Map<String, String> root() {
        return Map.of(
                "code", "oke",
                "message", "Server is live"
        );
    }
}

package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final PostRepository postRepository;

    @GetMapping("/search")
    public String search(@RequestParam(name = "q", required = false, defaultValue = "") String query,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "12") int size,
                          Model model) {

        model.addAttribute("query", query);

        if (query.isBlank()) {
            model.addAttribute("results", null);
            return "search";
        }

        var results = postRepository.searchPublished(
                query.trim(),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        model.addAttribute("results", results);
        return "search";
    }
}

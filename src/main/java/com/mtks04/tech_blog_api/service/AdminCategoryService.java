package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.entity.Category;
import com.mtks04.tech_blog_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories(String keyword) {
        if (StringUtils.hasText(keyword)) {
            return categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                    .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }
        return categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public void saveCategory(Category category) {
        if (!StringUtils.hasText(category.getSlug())) {
            category.setSlug(toSlug(category.getName()));
        }
        
        // Ensure slug is unique if creating new
        if (category.getId() == null && categoryRepository.existsBySlug(category.getSlug())) {
            category.setSlug(category.getSlug() + "-" + System.currentTimeMillis());
        }
        
        categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private String toSlug(String input) {
        if (input == null || input.isEmpty()) return "";
        String slug = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        slug = pattern.matcher(slug).replaceAll("");
        slug = slug.toLowerCase();
        slug = slug.replaceAll("đ", "d");
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");
        slug = slug.replaceAll("\\s+", "-");
        slug = slug.replaceAll("-+", "-");
        return slug;
    }
}

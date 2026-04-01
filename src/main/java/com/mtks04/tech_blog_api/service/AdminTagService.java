package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.entity.Tag;
import com.mtks04.tech_blog_api.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AdminTagService {

    private final TagRepository tagRepository;

    public List<Tag> getAllTags(String keyword) {
        if (StringUtils.hasText(keyword)) {
            return tagRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                    .filter(t -> t.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }
        return tagRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Tag getTagById(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new RuntimeException("Tag not found"));
    }

    public void saveTag(Tag tag) {
        if (!StringUtils.hasText(tag.getSlug())) {
            tag.setSlug(toSlug(tag.getName()));
        }
        
        if (tag.getId() == null && tagRepository.existsBySlug(tag.getSlug())) {
            tag.setSlug(tag.getSlug() + "-" + System.currentTimeMillis());
        }
        
        tagRepository.save(tag);
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
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

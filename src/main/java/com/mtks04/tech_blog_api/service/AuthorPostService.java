package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.entity.Category;
import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.CategoryRepository;
import com.mtks04.tech_blog_api.repository.PostRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthorPostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public String generateSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH).replaceAll("-{2,}", "-").replaceAll("^-|-$", "");

        // Make slug unique
        String originalSlug = slug;
        int count = 1;
        while (postRepository.existsBySlug(slug)) {
            slug = originalSlug + "-" + count;
            count++;
        }
        return slug;
    }

    public Page<Post> getPostsByAuthor(String username, Pageable pageable) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        return postRepository.findByAuthorId(author.getId(), pageable);
    }

    public Page<Post> searchPostsByAuthor(String username, String keyword, Pageable pageable) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        if (!StringUtils.hasText(keyword)) {
            return postRepository.findByAuthorId(author.getId(), pageable);
        }
        return postRepository.searchByAuthorIdAndKeyword(author.getId(), keyword.trim(), pageable);
    }

    @Transactional
    public Post createPost(String username, String title, String summary, String content, String thumbnailUrl, Long categoryId, Post.Status status) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Post post = Post.builder()
                .title(title)
                .slug(generateSlug(title))
                .summary(summary)
                .content(content)
                .thumbnailUrl(thumbnailUrl)
                .category(category)
                .author(author)
                .status(status)
                .viewCount(0)
                .build();

        return postRepository.save(post);
    }

    public Post getPostByIdForAuthor(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
                
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("You do not have permission to view this post");
        }
        return post;
    }

    @Transactional
    public Post updatePost(Long id, String username, String title, String summary, String content, String thumbnailUrl, Long categoryId, Post.Status status) {
        Post post = getPostByIdForAuthor(id, username);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        post.setTitle(title);
        // Only regenerate slug if title changed (optional, but good practice to check or just let it stay old slug to prevent broken links)
        // Let's keep the slug stable or just update it if needed. For now, keep it stable.
        post.setSummary(summary);
        post.setContent(content);
        post.setThumbnailUrl(thumbnailUrl);
        post.setCategory(category);
        post.setStatus(status);

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post post = getPostByIdForAuthor(id, username);
        postRepository.delete(post);
    }
}

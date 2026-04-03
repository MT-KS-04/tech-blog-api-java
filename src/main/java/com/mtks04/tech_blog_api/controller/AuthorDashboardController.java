package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.repository.CategoryRepository;
import com.mtks04.tech_blog_api.service.AuthorPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/author")
@RequiredArgsConstructor
public class AuthorDashboardController {

    private final AuthorPostService authorPostService;
    private final CategoryRepository categoryRepository;
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        String username = authentication.getName();
        
        Page<Post> postsPage = authorPostService.getPostsByAuthor(username, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        
        model.addAttribute("postsPage", postsPage);
        model.addAttribute("posts", postsPage.getContent());
        
        // Optional stats (simplified for UI)
        long totalPosts = postsPage.getTotalElements();
        model.addAttribute("totalPosts", totalPosts);
        
        return "author/dashboard";
    }

    @GetMapping("/create-post")
    public String createPostForm(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "author/create-post";
    }

    @PostMapping("/create-post")
    public String createPost(@RequestParam String title,
                             @RequestParam String summary,
                             @RequestParam String content,
                             @RequestParam(required = false) String coverUrl,
                             @RequestParam(required = false) MultipartFile coverImage,
                             @RequestParam Long categoryId,
                             @RequestParam String action,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            Post.Status status = action.equals("publish") ? Post.Status.PENDING : Post.Status.DRAFT;
            String thumbnailUrl = resolveThumbnailUrl(coverUrl, coverImage);
            authorPostService.createPost(username, title, summary, content, thumbnailUrl, categoryId, status);
            
            String successMsg = status == Post.Status.PENDING 
                ? "Bài viết đã được gửi duyệt. Vui lòng chờ Admin phê duyệt!" 
                : "Đã lưu bản nháp thành công!";
            redirectAttributes.addFlashAttribute("successMessage", successMsg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/author/dashboard";
    }

    @GetMapping("/edit-post/{id}")
    public String editPostForm(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        Post post = authorPostService.getPostByIdForAuthor(id, username);
        model.addAttribute("post", post);
        model.addAttribute("categories", categoryRepository.findAll());
        return "author/edit-post";
    }

    @PostMapping("/edit-post/{id}")
    public String updatePost(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String summary,
                             @RequestParam String content,
                             @RequestParam(required = false) String coverUrl,
                             @RequestParam(required = false) MultipartFile coverImage,
                             @RequestParam Long categoryId,
                             @RequestParam String action,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            Post.Status status = action.equals("publish") ? Post.Status.PENDING : Post.Status.DRAFT;
            String thumbnailUrl = resolveThumbnailUrl(coverUrl, coverImage);
            authorPostService.updatePost(id, username, title, summary, content, thumbnailUrl, categoryId, status);
            
            String successMsg = status == Post.Status.PENDING 
                ? "Bài viết đã được cập nhật và gửi lại cho Admin duyệt!" 
                : "Đã cập nhật bản nháp thành công!";
            redirectAttributes.addFlashAttribute("successMessage", successMsg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/author/dashboard";
    }

    @PostMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            authorPostService.deletePost(id, username);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xoá bài viết.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/author/dashboard";
    }

    private String resolveThumbnailUrl(String coverUrl, MultipartFile coverImage) throws IOException {
        if (coverImage != null && !coverImage.isEmpty()) {
            return storeUploadedImage(coverImage);
        }
        if (!StringUtils.hasText(coverUrl)) {
            return null;
        }
        String normalized = coverUrl.trim();
        if (normalized.startsWith("http://") || normalized.startsWith("https://") || normalized.startsWith("/")) {
            return normalized;
        }
        return "/" + normalized;
    }

    private String storeUploadedImage(MultipartFile coverImage) throws IOException {
        String originalName = coverImage.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalName);
        if (extension == null || !ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Ảnh bìa chỉ hỗ trợ định dạng JPG, JPEG, PNG, GIF, WEBP.");
        }

        Path uploadDirectory = Paths.get("uploads");
        Files.createDirectories(uploadDirectory);

        String filename = UUID.randomUUID() + "." + extension.toLowerCase();
        Path target = uploadDirectory.resolve(filename);
        Files.copy(coverImage.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + filename;
    }
}
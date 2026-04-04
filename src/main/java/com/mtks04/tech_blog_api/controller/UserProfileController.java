package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;
import java.util.List;
import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.repository.PostLikeRepository;
import com.mtks04.tech_blog_api.repository.PostBookmarkRepository;
import com.mtks04.tech_blog_api.repository.PostRepository;
import com.mtks04.tech_blog_api.repository.CommentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostBookmarkRepository postBookmarkRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showProfile(Model model, Principal principal, @RequestParam(value = "tab", defaultValue = "info") String tab) {
        if (principal == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("fullName", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("about", user.getBio() != null ? user.getBio() : "");

            if (user.getCreatedAt() != null) {
                String joinDate = "Thành viên từ tháng " + user.getCreatedAt().getMonthValue() + " năm " + user.getCreatedAt().getYear();
                model.addAttribute("joinDate", joinDate);
            } else {
                model.addAttribute("joinDate", "Thành viên mới");
            }

            // Fetch Real Data
            long savedCount = postBookmarkRepository.countByUserId(user.getId());
            long likedCount = postLikeRepository.countByPostAuthorId(user.getId()); // Wait, this is for author. 
            // Actually I need count of posts LIKED BY the user.
            // Let's check PostLikeRepository again.
            
            // For now, let's just fetch the lists and use their size for the counts if needed, 
            // but Better to add countByUserId to repositories.
            List<Post> posts = null;
            if ("liked".equals(tab)) {
                posts = postRepository.findLikedPostsByUserId(user.getId());
            } else if ("saved".equals(tab)) {
                posts = postRepository.findBookmarkedPostsByUserId(user.getId());
            }
            
            model.addAttribute("posts", posts);
            model.addAttribute("activeTab", tab);
            model.addAttribute("savedPosts", savedCount);
            model.addAttribute("likedPosts", postLikeRepository.countByUserId(user.getId()));
            model.addAttribute("comments", commentRepository.countByPostAuthorId(user.getId())); // Or count by user?
        }

        return "user/profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "about", required = false) String about,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài khoản.");
            return "redirect:/profile";
        }

        User user = userOpt.get();

        // Kiểm tra email mới có bị trùng với người dùng khác không
        if (email != null && !email.isBlank() && !email.equalsIgnoreCase(user.getEmail())) {
            boolean emailTaken = userRepository.existsByEmail(email);
            if (emailTaken) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email này đã được sử dụng bởi tài khoản khác.");
                return "redirect:/profile";
            }
            user.setEmail(email.trim());
        }

        // Cập nhật bio / giới thiệu
        if (about != null) {
            user.setBio(about.trim());
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/profile";

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu hiện tại không chính xác.");
            return "redirect:/profile?tab=settings";
        }

        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            return "redirect:/profile?tab=settings";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        return "redirect:/profile?tab=settings";
    }

    @PostMapping("/update-notifications")
    public String updateNotifications(
            @RequestParam(value = "notifCommentReply", defaultValue = "false") boolean notifCommentReply,
            @RequestParam(value = "notifWeeklyNewsletter", defaultValue = "false") boolean notifWeeklyNewsletter,
            @RequestParam(value = "notifNewPost", defaultValue = "false") boolean notifNewPost,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/profile";

        user.setNotifCommentReply(notifCommentReply);
        user.setNotifWeeklyNewsletter(notifWeeklyNewsletter);
        user.setNotifNewPost(notifNewPost);
        
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tùy chọn thông báo thành công!");
        return "redirect:/profile?tab=settings";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(Principal principal, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            user.setActive(false);
            userRepository.save(user);
            
            // Logout user
            new SecurityContextLogoutHandler().logout(request, null, null);
        }

        return "redirect:/login?logout";
    }
}

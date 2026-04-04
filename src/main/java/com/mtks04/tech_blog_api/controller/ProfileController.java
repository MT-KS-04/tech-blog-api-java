package com.mtks04.tech_blog_api.controller;

import com.mtks04.tech_blog_api.dto.PasswordChangeFormDto;
import com.mtks04.tech_blog_api.dto.ProfileFormDto;
import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    private void addSidebarStats(Model model, User user) {
        Long id = user.getId();
        model.addAttribute("profileUser", user);
        model.addAttribute("displayName", profileService.displayName(user));
        model.addAttribute("memberSinceLabel", profileService.memberSinceLabel(user));
        model.addAttribute("avatarInitials", profileService.avatarInitials(user));
        model.addAttribute("countSaved", profileService.countSaved(id));
        model.addAttribute("countLiked", profileService.countLiked(id));
        model.addAttribute("countComments", profileService.countComments(id));
    }

    @GetMapping
    public String profileTab(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = profileService.requireUser(principal.getUsername());
        addSidebarStats(model, user);
        model.addAttribute("activeTab", "profile");
        model.addAttribute("profileForm", profileService.toFormDto(user));
        return "profile";
    }

    @PostMapping
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                @Valid @ModelAttribute("profileForm") ProfileFormDto form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        User user = profileService.requireUser(principal.getUsername());
        addSidebarStats(model, user);
        model.addAttribute("activeTab", "profile");

        if (bindingResult.hasErrors()) {
            return "profile";
        }

        try {
            profileService.updateProfile(principal.getUsername(), form);
        } catch (RuntimeException e) {
            model.addAttribute("profileError", e.getMessage());
            return "profile";
        }

        redirectAttributes.addFlashAttribute("profileSuccess", "Đã lưu thay đổi thành công.");
        return "redirect:/profile";
    }

    @GetMapping("/saved")
    public String savedTab(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = profileService.requireUser(principal.getUsername());
        addSidebarStats(model, user);
        model.addAttribute("activeTab", "saved");
        List<Post> savedPosts = profileService.listSavedPosts(user.getId(), 0, 50);
        model.addAttribute("savedPosts", savedPosts);
        return "profile-saved";
    }

    @GetMapping("/liked")
    public String likedTab(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = profileService.requireUser(principal.getUsername());
        addSidebarStats(model, user);
        model.addAttribute("activeTab", "liked");
        List<Post> likedPosts = profileService.listLikedPosts(user.getId(), 0, 50);
        model.addAttribute("likedPosts", likedPosts);
        return "profile-liked";
    }

    @GetMapping("/settings")
    public String settingsTab(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = profileService.requireUser(principal.getUsername());
        addSidebarStats(model, user);
        model.addAttribute("activeTab", "settings");
        model.addAttribute("passwordChangeForm", emptyPasswordForm());
        return "profile-settings";
    }

    @PostMapping("/settings/password")
    public String changePassword(@AuthenticationPrincipal UserDetails principal,
                                 @Valid @ModelAttribute("passwordChangeForm") PasswordChangeFormDto form,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        User user = profileService.requireUser(principal.getUsername());
        addSidebarStats(model, user);
        model.addAttribute("activeTab", "settings");

        if (bindingResult.hasErrors()) {
            return "profile-settings";
        }

        try {
            profileService.changePassword(principal.getUsername(), form);
        } catch (RuntimeException e) {
            model.addAttribute("settingsPasswordError", e.getMessage());
            return "profile-settings";
        }

        redirectAttributes.addFlashAttribute("settingsPasswordSuccess", "Đã cập nhật mật khẩu thành công.");
        return "redirect:/profile/settings";
    }

    @PostMapping("/settings/notifications")
    public String saveNotifications(@AuthenticationPrincipal UserDetails principal,
                                    @RequestParam(required = false) Boolean notifyCommentReply,
                                    @RequestParam(required = false) Boolean notifyWeeklyNewsletter,
                                    @RequestParam(required = false) Boolean notifyAuthorPosts,
                                    RedirectAttributes redirectAttributes) {
        profileService.updateNotificationSettings(
                principal.getUsername(),
                Boolean.TRUE.equals(notifyCommentReply),
                Boolean.TRUE.equals(notifyWeeklyNewsletter),
                Boolean.TRUE.equals(notifyAuthorPosts));
        redirectAttributes.addFlashAttribute("settingsNotifySuccess", "Đã lưu tùy chọn thông báo.");
        return "redirect:/profile/settings";
    }

    @PostMapping("/settings/account/delete")
    public String deleteAccount(@AuthenticationPrincipal UserDetails principal,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes redirectAttributes) {
        try {
            profileService.deactivateAccount(principal.getUsername());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("settingsDeleteError", e.getMessage());
            return "redirect:/profile/settings";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, auth);
        return "redirect:/login?closed=1";
    }

    private static PasswordChangeFormDto emptyPasswordForm() {
        return PasswordChangeFormDto.builder()
                .currentPassword("")
                .newPassword("")
                .confirmPassword("")
                .build();
    }
}

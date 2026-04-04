package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.dto.PasswordChangeFormDto;
import com.mtks04.tech_blog_api.dto.ProfileFormDto;
import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.entity.PostBookmark;
import com.mtks04.tech_blog_api.entity.PostLike;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.CommentRepository;
import com.mtks04.tech_blog_api.repository.PostBookmarkRepository;
import com.mtks04.tech_blog_api.repository.PostLikeRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final String[] VI_MONTH_PREFIX = {
            "tháng 1", "tháng 2", "tháng 3", "tháng 4", "tháng 5", "tháng 6",
            "tháng 7", "tháng 8", "tháng 9", "tháng 10", "tháng 11", "tháng 12"
    };

    private final UserRepository userRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    public User requireUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    public String displayName(User user) {
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName().trim();
        }
        return user.getUsername();
    }

    public String memberSinceLabel(User user) {
        LocalDateTime created = user.getCreatedAt();
        if (created == null) {
            return "";
        }
        String monthPart = VI_MONTH_PREFIX[created.getMonthValue() - 1];
        return "Thành viên từ " + monthPart + " năm " + created.getYear();
    }

    public String avatarInitials(User user) {
        String name = displayName(user);
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return "?";
        }
        String[] parts = trimmed.split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
        return trimmed.substring(0, Math.min(2, trimmed.length())).toUpperCase();
    }

    public long countSaved(Long userId) {
        return postBookmarkRepository.countByUser_Id(userId);
    }

    public long countLiked(Long userId) {
        return postLikeRepository.countByUser_Id(userId);
    }

    public long countComments(Long userId) {
        return commentRepository.countByUser_Id(userId);
    }

    public ProfileFormDto toFormDto(User user) {
        return ProfileFormDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .bio(user.getBio() != null ? user.getBio() : "")
                .build();
    }

    @Transactional
    public void updateProfile(String username, ProfileFormDto form) {
        User user = requireUser(username);
        if (!form.getEmail().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(form.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác");
        }
        user.setFullName(form.getFullName() != null && !form.getFullName().isBlank()
                ? form.getFullName().trim()
                : null);
        user.setEmail(form.getEmail().trim());
        user.setBio(form.getBio() != null && !form.getBio().isBlank() ? form.getBio().trim() : null);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<Post> listSavedPosts(Long userId, int page, int size) {
        Page<PostBookmark> bookmarks = postBookmarkRepository.findByUser_IdOrderByCreatedAtDesc(
                userId, PageRequest.of(page, size));
        return bookmarks.getContent().stream()
                .map(PostBookmark::getPost)
                .filter(p -> p.getStatus() == Post.Status.PUBLISHED)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Post> listLikedPosts(Long userId, int page, int size) {
        Page<PostLike> likes = postLikeRepository.findByUser_IdOrderByCreatedAtDesc(
                userId, PageRequest.of(page, size));
        return likes.getContent().stream()
                .map(PostLike::getPost)
                .filter(p -> p.getStatus() == Post.Status.PUBLISHED)
                .collect(Collectors.toList());
    }

    @Transactional
    public void changePassword(String username, PasswordChangeFormDto form) {
        User user = requireUser(username);
        if (!passwordEncoder.matches(form.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận không khớp");
        }
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void updateNotificationSettings(String username,
                                           boolean notifyCommentReply,
                                           boolean notifyWeeklyNewsletter,
                                           boolean notifyAuthorPosts) {
        User user = requireUser(username);
        user.setNotifyCommentReply(notifyCommentReply);
        user.setNotifyWeeklyNewsletter(notifyWeeklyNewsletter);
        user.setNotifyAuthorPosts(notifyAuthorPosts);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateAccount(String username) {
        User user = requireUser(username);
        user.setActive(false);
        userRepository.save(user);
    }
}

package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    /**
     * Lấy danh sách người dùng kèm phân trang và tìm kiếm
     */
    public Page<User> getAllUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (keyword != null && !keyword.isEmpty()) {
            return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }

    /**
     * Khóa hoặc mở khóa tài khoản người dùng
     */
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi dung id: " + userId));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    /**
     * Cập nhật quyền hạn (Role) cho người dùng
     */
    @Transactional
    public void updateUserRole(Long userId, User.Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi dung id: " + userId));
        user.setRole(newRole);
        userRepository.save(user);
    }

    /**
     * Xóa vĩnh viễn người dùng (Hard Delete)
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Khong tim thay nguoi dung de xoa id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}

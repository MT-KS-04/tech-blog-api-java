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
     * Lấy danh sách người dùng có phân trang và tìm kiếm (theo username/email)
     */
    public Page<User> getAllUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (keyword != null && !keyword.isEmpty()) {
            return userRepository.findByUsernameContainingOrEmailContaining(keyword, keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }

    /**
     * Đảo ngược trạng thái hoạt động của người dùng (Bật/Tắt)
     */
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi dung id: " + userId));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    /**
     * Cập nhật quyền hạn cho người dùng
     */
    @Transactional
    public void updateRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi dung id: " + userId));
        
        try {
            User.Role newRole = User.Role.valueOf(roleName.toUpperCase());
            user.setRole(newRole);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Quyen han khong hop le: " + roleName);
        }
    }

    /**
     * Xóa vĩnh viễn người dùng khỏi cơ sở dữ liệu
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Khong tim thay nguoi dung id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}

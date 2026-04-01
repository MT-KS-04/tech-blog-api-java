package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.dto.AuthorStatsDto;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAuthorService {

    private final UserRepository userRepository;

    /**
     * Lấy danh sách tác giả (ADMIN/EDITOR) cùng các số liệu thống kê.
     * Sắp xếp theo số lượng bài viết giảm dần.
     */
    public List<AuthorStatsDto> getAuthorsWithStats() {
        return userRepository.getAuthorsWithStats();
    }

    /**
     * Thay đổi trạng thái hoạt động của tác giả (Khóa/Mở khóa).
     */
    @Transactional
    public void toggleAuthorStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả ID: " + userId));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
}

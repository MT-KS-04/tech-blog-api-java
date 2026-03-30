package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Tìm kiếm người dùng theo username hoặc email hỗ trợ phân trang
    Page<User> findByUsernameContainingOrEmailContaining(String username, String email, Pageable pageable);
}

package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Page<User> findByUsernameContainingOrEmailContaining(String username, String email, Pageable pageable);
}

package com.mtks04.tech_blog_api.repository;

import com.mtks04.tech_blog_api.dto.AuthorStatsDto;
import com.mtks04.tech_blog_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT new com.mtks04.tech_blog_api.dto.AuthorStatsDto(" +
           "u.id, u.username, u.email, u.bio, " +
           "COUNT(DISTINCT p.id), " +
           "COALESCE(SUM(DISTINCT p.viewCount), 0), " +
           "(SELECT COUNT(c) FROM Comment c WHERE c.post.author = u), " +
           "u.isActive) " +
           "FROM User u LEFT JOIN Post p ON p.author = u " +
           "WHERE u.role IN (com.mtks04.tech_blog_api.entity.User.Role.EDITOR) " +
           "GROUP BY u.id, u.username, u.email, u.bio, u.isActive " +
           "ORDER BY COUNT(DISTINCT p.id) DESC")
    List<AuthorStatsDto> getAuthorsWithStats();

    // Tìm kiếm người dùng theo username hoặc email (có phân trang và không phân biệt hoa thường)
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);
}

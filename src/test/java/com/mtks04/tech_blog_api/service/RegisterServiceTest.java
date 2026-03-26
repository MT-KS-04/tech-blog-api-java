package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.dto.RegisterRequest;
import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RegisterServiceTest {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        registerService.register(request);

        User savedUser = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals("testuser"))
                .findFirst()
                .orElse(null);

        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        assertEquals(User.Role.USER, savedUser.getRole());
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        User existingUser = User.builder()
                .username("existing")
                .email("existing@example.com")
                .password("password")
                .build();
        userRepository.save(existingUser);

        RegisterRequest request = RegisterRequest.builder()
                .username("existing")
                .email("new@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        Exception exception = assertThrows(RuntimeException.class, () -> registerService.register(request));
        assertEquals("Username đã tồn tại", exception.getMessage());
    }

    @Test
    void register_MismatchedPassword_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .confirmPassword("different")
                .build();

        Exception exception = assertThrows(RuntimeException.class, () -> registerService.register(request));
        assertEquals("Mật khẩu xác nhận không khớp", exception.getMessage());
    }
}

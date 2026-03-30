package com.mtks04.tech_blog_api.config;

import com.mtks04.tech_blog_api.entity.User;
import com.mtks04.tech_blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem đã có tài khoản ADMIN nào trong hệ thống chưa
        long adminCount = userRepository.findAll().stream()
                .filter(user -> user.getRole() == User.Role.ADMIN)
                .count();

        if (adminCount == 0) {
            log.info("Khong tim thay tai khoan ADMIN. Dang khoi tao tai khoan mac dinh...");
            
            User admin = User.builder()
                    .username("admin")
                    .email("admin@techblog.com")
                    .password(passwordEncoder.encode("admin@123"))
                    .role(User.Role.ADMIN)
                    .isActive(true)
                    .build();
            
            userRepository.save(admin);
            
            log.info("--------------------------------------------------");
            log.info("TAI KHOAN ADMIN MAC DINH DA DUOC TAO:");
            log.info("Username: admin");
            log.info("Password: admin@123");
            log.info("Role: ROLE_ADMIN");
            log.info("--------------------------------------------------");
        } else {
            log.info("He thong da co tai khoan ADMIN. Bo qua buoc khoi tao.");
        }
    }
}

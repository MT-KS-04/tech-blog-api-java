package com.mtks04.tech_blog_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                    // Cho phép các trang công khai và asset
                    .requestMatchers("/", "/login", "/register", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                    // Chỉ ADMIN mới được vào trang quản trị
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    // Mọi yêu cầu khác đều phải đăng nhập
                    .anyRequest().authenticated()
                )
                .formLogin(form -> form 
                    .loginPage("/login")
                    // Đăng nhập thành công sẽ chuyển sang trang chủ
                    .defaultSuccessUrl("/", true)
                    .permitAll()
                )
                .logout(logout -> logout
                    .logoutSuccessUrl("/")
                    .permitAll()
                );
        return httpSecurity.build();

    }
}
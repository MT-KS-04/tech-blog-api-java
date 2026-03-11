package com.mtks04.tech_blog_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    public enum Role {
        ADMIN,
        USER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Size(min = 3, max = 50, message = "username phai lon hon 3 ky tu va khong qua 50 ky tu")
    @NotBlank(message = "username khong duoc rong")
    private String username;

    @Column(unique = true)
    @Email(message = "Sai dinh dang email")
    @NotBlank(message = "Email khong duoc rong")
    private String email;

    @Size(min = 6, max = 50, message = "Password phai lon hon 6 ky tu va khong qua 50 ky tu")
    @NotBlank(message = "Password khong duoc rong")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private boolean isActive = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
package com.mtks04.tech_blog_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Ten danh muc khong duoc de trong")
    private String name;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Slug khong duoc de trong")
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;
}

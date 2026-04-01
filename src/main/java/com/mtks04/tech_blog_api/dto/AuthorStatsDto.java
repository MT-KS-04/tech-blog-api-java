package com.mtks04.tech_blog_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorStatsDto {
    private Long userId;
    private String username;
    private String email;
    private String bio;
    private long postCount;
    private long totalLikes;
    private long commentCount;
    private boolean active;
}

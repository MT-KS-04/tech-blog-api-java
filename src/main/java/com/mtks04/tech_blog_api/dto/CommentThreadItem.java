package com.mtks04.tech_blog_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CommentThreadItem {

    private final Long id;
    private final String authorDisplayName;
    private final String content;
    private final LocalDateTime createdAt;
    private final long likeCount;
    private final boolean likedByCurrentUser;
    private final List<CommentThreadItem> replies;
}

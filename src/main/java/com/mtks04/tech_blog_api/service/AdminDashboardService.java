package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.entity.Post;
import com.mtks04.tech_blog_api.repository.CommentRepository;
import com.mtks04.tech_blog_api.repository.PostRepository;
import com.mtks04.tech_blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final PostRepository    postRepository;
    private final UserRepository    userRepository;
    private final CommentRepository commentRepository;

    /** Tổng số bài viết */
    public long countTotalPosts() {
        return postRepository.count();
    }

    /** Tổng số người dùng */
    public long countTotalUsers() {
        return userRepository.count();
    }

    /** Tổng số bình luận */
    public long countTotalComments() {
        return commentRepository.count();
    }

    /** Tổng lượt xem toàn bộ bài viết */
    public long getTotalViews() {
        Long total = postRepository.sumViewCount();
        return total != null ? total : 0L;
    }

    /** 5 bài viết được tạo gần nhất */
    public List<Post> getRecentPosts() {
        return postRepository.findTop5ByOrderByCreatedAtDesc();
    }

    /** 5 bài viết có lượt xem cao nhất */
    public List<Post> getTopViewedPosts() {
        return postRepository.findTop5ByOrderByViewCountDesc();
    }
}

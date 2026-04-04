package com.mtks04.tech_blog_api.service;

import com.mtks04.tech_blog_api.entity.Comment;
import com.mtks04.tech_blog_api.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getAllComments(String keyword) {
        List<Comment> all = commentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        if (StringUtils.hasText(keyword)) {
            String lowerKeyword = keyword.toLowerCase();
            return all.stream()
                    .filter(c -> c.getContent() != null && c.getContent().toLowerCase().contains(lowerKeyword))
                    .toList();
        }
        return all;
    }

    @Transactional
    public void deleteComment(Long id) {
        // Tự động xóa luân hồi (recursive) các bình luận con để tránh lỗi ForeignKey
        deleteCommentRecursive(id);
    }
    
    private void deleteCommentRecursive(Long id) {
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(id);
        if (replies != null && !replies.isEmpty()) {
            for (Comment reply : replies) {
                deleteCommentRecursive(reply.getId());
            }
        }
        commentRepository.deleteById(id);
    }
}

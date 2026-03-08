package com.example.TaskManager.domain.comment.dto;

import com.example.TaskManager.domain.comment.Comment;

import java.time.LocalDateTime;

public record CommentResponseDto(
        String id,
        String message,
        String userId,
        String userName,
        LocalDateTime createdAt
) {
    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getMessage(),
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getCreatedAt()
        );
    }
}
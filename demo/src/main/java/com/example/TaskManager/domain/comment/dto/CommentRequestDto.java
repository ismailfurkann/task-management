package com.example.TaskManager.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto(
        @NotBlank(message = "Message cannot be blank")
        String message
) {}
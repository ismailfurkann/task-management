package com.example.TaskManager.domain.subtask.dto;

import com.example.TaskManager.domain.subtask.Subtask;

import java.time.LocalDateTime;

public record SubtaskResponseDto(
        String id,
        String title,
        boolean completed,
        LocalDateTime createdAt
) {
    public static SubtaskResponseDto from(Subtask subtask) {
        return new SubtaskResponseDto(
                subtask.getId(),
                subtask.getTitle(),
                subtask.isCompleted(),
                subtask.getCreatedAt()
        );
    }
}
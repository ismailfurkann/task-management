package com.example.TaskManager.domain.activity.dto;

import com.example.TaskManager.domain.activity.ActivityAction;
import com.example.TaskManager.domain.activity.ActivityLog;

import java.time.LocalDateTime;

public record ActivityLogResponseDto(
        String id,
        ActivityAction action,
        String taskTitle,
        String userId,
        String userName,
        LocalDateTime createdAt
) {
    public static ActivityLogResponseDto from(ActivityLog log) {
        return new ActivityLogResponseDto(
                log.getId(),
                log.getAction(),
                log.getTaskTitle(),
                log.getUser().getId(),
                log.getUser().getName(),
                log.getCreatedAt()
        );
    }
}
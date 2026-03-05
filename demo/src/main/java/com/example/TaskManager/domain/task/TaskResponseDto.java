package com.example.TaskManager.domain.task;

import com.example.TaskManager.domain.task.Priority;
import com.example.TaskManager.domain.task.Task;
import com.example.TaskManager.domain.task.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponseDto(

        String id,
        String title,
        String description,
        TaskStatus status,
        Priority priority,
        LocalDate dueDate,
        String projectId,
        String projectName,
        String assignedUserId,
        String assignedUserName,
        LocalDateTime createdAt
) {
    public static TaskResponseDto from(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getAssignedUser() != null ? task.getAssignedUser().getId() : null,
                task.getAssignedUser() != null ? task.getAssignedUser().getName() : null,
                task.getCreatedAt()
        );
    }
}

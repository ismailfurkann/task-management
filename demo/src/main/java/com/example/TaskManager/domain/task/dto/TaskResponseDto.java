package com.example.TaskManager.domain.task.dto;

import com.example.TaskManager.domain.comment.dto.CommentResponseDto;
import com.example.TaskManager.domain.label.dto.LabelResponseDto;
import com.example.TaskManager.domain.subtask.dto.SubtaskResponseDto;
import com.example.TaskManager.domain.task.Priority;
import com.example.TaskManager.domain.task.Task;
import com.example.TaskManager.domain.task.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        List<CommentResponseDto> comments,
        List<SubtaskResponseDto> subtasks,
        List<LabelResponseDto> labels,
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
                task.getComments().stream().map(CommentResponseDto::from).toList(),
                task.getSubtasks().stream().map(SubtaskResponseDto::from).toList(),
                task.getLabels().stream().map(LabelResponseDto::from).toList(),
                task.getCreatedAt()
        );
    }
}
package com.example.TaskManager.domain.project.dto;

import com.example.TaskManager.domain.project.Project;

import java.time.LocalDateTime;

public record ProjectResponseDto(

        String id,
        String name,
        String description,
        String ownerName,
        int taskCount,
        LocalDateTime createdAt
) {
    public static ProjectResponseDto from(Project project) {
        return new ProjectResponseDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getName(),
                project.getTasks().size(),
                project.getCreatedAt()
        );
    }
}

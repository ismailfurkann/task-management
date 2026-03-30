package com.example.TaskManager.domain.project.dto;

import com.example.TaskManager.domain.project.Project;

import java.time.LocalDateTime;

public record ProjectResponseDto(
        String id,
        String name,
        String description,
        String ownerId,
        String ownerName,
        int taskCount,
        int memberCount,
        LocalDateTime createdAt
) {
    public static ProjectResponseDto from(Project project) {
        return new ProjectResponseDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getOwner().getName(),
                project.getTasks() != null ? project.getTasks().size() : 0,
                project.getMembers() != null ? project.getMembers().size() : 0,
                project.getCreatedAt()
        );
    }
}
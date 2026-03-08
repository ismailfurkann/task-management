package com.example.TaskManager.domain.project;

import com.example.TaskManager.domain.project.dto.ProjectRequestDto;
import com.example.TaskManager.domain.project.dto.ProjectResponseDto;

import java.util.List;

public interface ProjectService {

    ProjectResponseDto createProject(String ownerId, ProjectRequestDto dto);

    ProjectResponseDto getProjectById(String id, String currentUserId);

    List<ProjectResponseDto> getProjectsByOwner(String ownerId);

    ProjectResponseDto updateProject(String id, String currentUserId, ProjectRequestDto dto);

    void deleteProject(String id, String currentUserId);
}
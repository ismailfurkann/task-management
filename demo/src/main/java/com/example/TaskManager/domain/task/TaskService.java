package com.example.TaskManager.domain.task;

import com.example.TaskManager.domain.task.dto.TaskRequestDto;
import com.example.TaskManager.domain.task.dto.TaskResponseDto;

import java.util.List;

public interface TaskService {

    TaskResponseDto createTask(String projectId, String currentUserId, TaskRequestDto dto);

    TaskResponseDto getTaskById(String id, String currentUserId);

    List<TaskResponseDto> getTasksByProject(String projectId, String currentUserId);

    List<TaskResponseDto> getTasksByAssignedUser(String userId);

    TaskResponseDto updateTask(String id, String currentUserId, TaskRequestDto dto);

    TaskResponseDto updateTaskStatus(String id, String currentUserId, TaskStatus status);

    void deleteTask(String id, String currentUserId);
}
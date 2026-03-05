package com.example.TaskManager.domain.task;

import com.example.TaskManager.domain.task.TaskRequestDto;
import com.example.TaskManager.domain.task.TaskResponseDto;

import java.util.List;

public interface TaskService {

    TaskResponseDto createTask(String projectId, TaskRequestDto dto);

    TaskResponseDto getTaskById(String id);

    List<TaskResponseDto> getTasksByProject(String projectId);

    List<TaskResponseDto> getTasksByAssignedUser(String userId);

    TaskResponseDto updateTask(String id, TaskRequestDto dto);

    TaskResponseDto updateTaskStatus(String id, TaskStatus status);

    void deleteTask(String id);
}
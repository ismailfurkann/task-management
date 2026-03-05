package com.example.TaskManager.domain.task;

import com.example.TaskManager.domain.project.Project;
import com.example.TaskManager.domain.project.ProjectRepository;
import com.example.TaskManager.domain.task.TaskRequestDto;
import com.example.TaskManager.domain.task.TaskResponseDto;
import com.example.TaskManager.domain.user.User;
import com.example.TaskManager.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public TaskResponseDto createTask(String projectId, TaskRequestDto dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());
        task.setPriority(dto.priority());
        task.setDueDate(dto.dueDate());
        task.setProject(project);

        // assignedUser opsiyonel — null olabilir
        if (dto.assignedUserId() != null) {
            User assignedUser = userRepository.findById(dto.assignedUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.assignedUserId()));
            task.setAssignedUser(assignedUser);
        }

        Task saved = taskRepository.save(task);
        return TaskResponseDto.from(saved);
    }

    @Override
    public TaskResponseDto getTaskById(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        return TaskResponseDto.from(task);
    }

    @Override
    public List<TaskResponseDto> getTasksByProject(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Project not found with id: " + projectId);
        }

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(TaskResponseDto::from)
                .toList();
    }

    @Override
    public List<TaskResponseDto> getTasksByAssignedUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        return taskRepository.findByAssignedUserId(userId)
                .stream()
                .map(TaskResponseDto::from)
                .toList();
    }

    @Override
    public TaskResponseDto updateTask(String id, TaskRequestDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());
        task.setPriority(dto.priority());
        task.setDueDate(dto.dueDate());

        if (dto.assignedUserId() != null) {
            User assignedUser = userRepository.findById(dto.assignedUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.assignedUserId()));
            task.setAssignedUser(assignedUser);
        } else {
            task.setAssignedUser(null); // atamayı kaldır
        }

        Task updated = taskRepository.save(task);
        return TaskResponseDto.from(updated);
    }

    @Override
    public TaskResponseDto updateTaskStatus(String id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setStatus(status);

        Task updated = taskRepository.save(task);
        return TaskResponseDto.from(updated);
    }

    @Override
    public void deleteTask(String id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}
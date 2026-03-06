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

    private Project getProjectAndVerifyOwner(String projectId, String currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Access denied");
        }

        return project;
    }

    private Task getTaskAndVerifyOwner(String taskId, String currentUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getProject().getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Access denied");
        }

        return task;
    }

    @Override
    public TaskResponseDto createTask(String projectId, String currentUserId, TaskRequestDto dto) {
        Project project = getProjectAndVerifyOwner(projectId, currentUserId);

        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());
        task.setPriority(dto.priority());
        task.setDueDate(dto.dueDate());
        task.setProject(project);

        if (dto.assignedUserId() != null) {
            User assignedUser = userRepository.findById(dto.assignedUserId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            task.setAssignedUser(assignedUser);
        }

        return TaskResponseDto.from(taskRepository.save(task));
    }

    @Override
    public TaskResponseDto getTaskById(String id, String currentUserId) {
        Task task = getTaskAndVerifyOwner(id, currentUserId);
        return TaskResponseDto.from(task);
    }

    @Override
    public List<TaskResponseDto> getTasksByProject(String projectId, String currentUserId) {
        getProjectAndVerifyOwner(projectId, currentUserId);

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(TaskResponseDto::from)
                .toList();
    }

    @Override
    public List<TaskResponseDto> getTasksByAssignedUser(String userId) {
        return taskRepository.findByAssignedUserId(userId)
                .stream()
                .map(TaskResponseDto::from)
                .toList();
    }

    @Override
    public TaskResponseDto updateTask(String id, String currentUserId, TaskRequestDto dto) {
        Task task = getTaskAndVerifyOwner(id, currentUserId);

        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());
        task.setPriority(dto.priority());
        task.setDueDate(dto.dueDate());

        if (dto.assignedUserId() != null) {
            User assignedUser = userRepository.findById(dto.assignedUserId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            task.setAssignedUser(assignedUser);
        } else {
            task.setAssignedUser(null);
        }

        return TaskResponseDto.from(taskRepository.save(task));
    }

    @Override
    public TaskResponseDto updateTaskStatus(String id, String currentUserId, TaskStatus status) {
        Task task = getTaskAndVerifyOwner(id, currentUserId);
        task.setStatus(status);
        return TaskResponseDto.from(taskRepository.save(task));
    }

    @Override
    public void deleteTask(String id, String currentUserId) {
        getTaskAndVerifyOwner(id, currentUserId);
        taskRepository.deleteById(id);
    }
}
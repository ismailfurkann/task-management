package com.example.TaskManager.domain.task;

import com.example.TaskManager.domain.activity.ActivityAction;
import com.example.TaskManager.domain.activity.ActivityLogService;
import com.example.TaskManager.domain.project.Project;
import com.example.TaskManager.domain.project.ProjectRepository;
import com.example.TaskManager.domain.task.dto.TaskRequestDto;
import com.example.TaskManager.domain.task.dto.TaskResponseDto;
import com.example.TaskManager.domain.user.User;
import com.example.TaskManager.domain.user.UserRepository;
import com.example.TaskManager.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final SecurityUtils securityUtils;

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

        Task saved = taskRepository.save(task);
        User currentUser = securityUtils.getCurrentUser();

        activityLogService.log(saved, currentUser, ActivityAction.CREATED_TASK);
        if (dto.assignedUserId() != null) {
            activityLogService.log(saved, currentUser, ActivityAction.ASSIGNED_USER);
        }

        return TaskResponseDto.from(saved);
    }

    @Override
    public TaskResponseDto getTaskById(String id, String currentUserId) {
        return TaskResponseDto.from(getTaskAndVerifyOwner(id, currentUserId));
    }

    @Override
    public List<TaskResponseDto> getTasksByProject(String projectId, String currentUserId) {
        getProjectAndVerifyOwner(projectId, currentUserId);
        return taskRepository.findByProjectId(projectId)
                .stream().map(TaskResponseDto::from).toList();
    }

    @Override
    public List<TaskResponseDto> getTasksByAssignedUser(String userId) {
        return taskRepository.findByAssignedUserId(userId)
                .stream().map(TaskResponseDto::from).toList();
    }

    @Override
    public TaskResponseDto updateTask(String id, String currentUserId, TaskRequestDto dto) {
        Task task = getTaskAndVerifyOwner(id, currentUserId);

        boolean statusChanged = !task.getStatus().equals(dto.status());
        boolean priorityChanged = !task.getPriority().equals(dto.priority());
        boolean assigneeChanged = dto.assignedUserId() != null &&
                (task.getAssignedUser() == null || !task.getAssignedUser().getId().equals(dto.assignedUserId()));

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

        Task updated = taskRepository.save(task);
        User currentUser = securityUtils.getCurrentUser();

        activityLogService.log(updated, currentUser, ActivityAction.UPDATED_TASK);
        if (statusChanged) activityLogService.log(updated, currentUser, ActivityAction.CHANGED_STATUS);
        if (priorityChanged) activityLogService.log(updated, currentUser, ActivityAction.CHANGED_PRIORITY);
        if (assigneeChanged) activityLogService.log(updated, currentUser, ActivityAction.ASSIGNED_USER);

        return TaskResponseDto.from(updated);
    }

    @Override
    public TaskResponseDto updateTaskStatus(String id, String currentUserId, TaskStatus status) {
        Task task = getTaskAndVerifyOwner(id, currentUserId);
        task.setStatus(status);
        Task updated = taskRepository.save(task);
        activityLogService.log(updated, securityUtils.getCurrentUser(), ActivityAction.CHANGED_STATUS);
        return TaskResponseDto.from(updated);
    }

    @Override
    public void deleteTask(String id, String currentUserId) {
        getTaskAndVerifyOwner(id, currentUserId);
        taskRepository.deleteById(id);
    }
}
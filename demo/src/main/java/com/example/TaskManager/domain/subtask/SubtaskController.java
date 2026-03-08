package com.example.TaskManager.domain.subtask;

import com.example.TaskManager.domain.activity.ActivityAction;
import com.example.TaskManager.domain.activity.ActivityLogService;
import com.example.TaskManager.domain.subtask.dto.SubtaskRequestDto;
import com.example.TaskManager.domain.subtask.dto.SubtaskResponseDto;
import com.example.TaskManager.domain.task.Task;
import com.example.TaskManager.domain.task.TaskRepository;
import com.example.TaskManager.domain.user.User;
import com.example.TaskManager.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/subtasks")
@RequiredArgsConstructor
public class SubtaskController {

    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;
    private final ActivityLogService activityLogService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<SubtaskResponseDto> addSubtask(
            @PathVariable String taskId,
            @Valid @RequestBody SubtaskRequestDto dto) {

        User currentUser = securityUtils.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Subtask subtask = new Subtask();
        subtask.setTitle(dto.title());
        subtask.setTask(task);

        Subtask saved = subtaskRepository.save(subtask);
        activityLogService.log(task, currentUser, ActivityAction.ADDED_SUBTASK);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubtaskResponseDto.from(saved));
    }

    @GetMapping
    public ResponseEntity<List<SubtaskResponseDto>> getSubtasks(@PathVariable String taskId) {
        return ResponseEntity.ok(
                subtaskRepository.findByTaskId(taskId)
                        .stream()
                        .map(SubtaskResponseDto::from)
                        .toList()
        );
    }

    @PatchMapping("/{subtaskId}/toggle")
    public ResponseEntity<SubtaskResponseDto> toggleSubtask(
            @PathVariable String taskId,
            @PathVariable String subtaskId) {

        User currentUser = securityUtils.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Subtask subtask = subtaskRepository.findById(subtaskId)
                .orElseThrow(() -> new RuntimeException("Subtask not found"));

        subtask.setCompleted(!subtask.isCompleted());
        Subtask updated = subtaskRepository.save(subtask);

        if (updated.isCompleted()) {
            activityLogService.log(task, currentUser, ActivityAction.COMPLETED_SUBTASK);
        }

        return ResponseEntity.ok(SubtaskResponseDto.from(updated));
    }

    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<Void> deleteSubtask(
            @PathVariable String taskId,
            @PathVariable String subtaskId) {

        subtaskRepository.deleteById(subtaskId);
        return ResponseEntity.noContent().build();
    }
}
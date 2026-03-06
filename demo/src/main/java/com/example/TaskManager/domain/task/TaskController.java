package com.example.TaskManager.domain.task;

import com.example.TaskManager.domain.task.TaskRequestDto;
import com.example.TaskManager.domain.task.TaskResponseDto;
import com.example.TaskManager.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @RequestParam String projectId,
            @Valid @RequestBody TaskRequestDto dto) {
        String currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(projectId, currentUserId, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable String id) {
        String currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(taskService.getTaskById(id, currentUserId));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getTasksByProject(@RequestParam String projectId) {
        String currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, currentUserId));
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TaskResponseDto>> getMyAssignedTasks() {
        String currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(taskService.getTasksByAssignedUser(currentUserId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequestDto dto) {
        String currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(taskService.updateTask(id, currentUserId, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(
            @PathVariable String id,
            @RequestParam TaskStatus status) {
        String currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(taskService.updateTaskStatus(id, currentUserId, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        String currentUserId = securityUtils.getCurrentUserId();
        taskService.deleteTask(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
package com.example.TaskManager.domain.task;

import com.example.TaskManager.domain.task.TaskRequestDto;
import com.example.TaskManager.domain.task.TaskResponseDto;
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

    // Task oluştur — hangi proje? URL'den al
    // POST /api/tasks?projectId=abc123
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @RequestParam String projectId,
            @Valid @RequestBody TaskRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(projectId, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    // Projeye ait tüm tasklar
    // GET /api/tasks?projectId=abc123
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getTasksByProject(@RequestParam String projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    // Kullanıcıya atanmış tasklar
    // GET /api/tasks/assigned?userId=abc123
    @GetMapping("/assigned")
    public ResponseEntity<List<TaskResponseDto>> getTasksByAssignedUser(@RequestParam String userId) {
        return ResponseEntity.ok(taskService.getTasksByAssignedUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequestDto dto) {
        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }

    // Sadece status güncellemek için ayrı endpoint — çok kullanılır
    // PATCH /api/tasks/{id}/status?status=IN_PROGRESS
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(
            @PathVariable String id,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}

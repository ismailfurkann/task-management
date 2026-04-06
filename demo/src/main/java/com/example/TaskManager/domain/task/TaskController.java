package com.example.TaskManager.domain.task;

import com.example.TaskManager.domain.task.dto.TaskRequestDto;
import com.example.TaskManager.domain.task.dto.TaskResponseDto;
import com.example.TaskManager.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskRepository taskRepository;
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

    // Yaklaşan taskler — sahip olduğun + sana atanan, sonraki 7 gün
    @GetMapping("/upcoming")
    public ResponseEntity<List<TaskResponseDto>> getUpcomingTasks() {
        String currentUserId = securityUtils.getCurrentUserId();
        LocalDate today = LocalDate.now();
        LocalDate in7Days = today.plusDays(7);

        List<Task> owned = taskRepository.findUpcomingTasksByOwner(currentUserId, today, in7Days);
        List<Task> assigned = taskRepository.findUpcomingTasksByAssignee(currentUserId, today, in7Days);

        // Birleştir, duplicate'leri kaldır, tarihe göre sırala
        List<Task> all = new ArrayList<>(owned);
        assigned.stream()
                .filter(t -> owned.stream().noneMatch(o -> o.getId().equals(t.getId())))
                .forEach(all::add);

        all.sort(Comparator.comparing(Task::getDueDate));

        return ResponseEntity.ok(all.stream().map(TaskResponseDto::from).toList());
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
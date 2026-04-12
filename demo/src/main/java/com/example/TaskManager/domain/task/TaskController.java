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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(projectId, securityUtils.getCurrentUserId(), dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTaskById(id, securityUtils.getCurrentUserId()));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getTasksByProject(@RequestParam String projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, securityUtils.getCurrentUserId()));
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TaskResponseDto>> getMyAssignedTasks() {
        return ResponseEntity.ok(taskService.getTasksByAssignedUser(securityUtils.getCurrentUserId()));
    }

    // Yaklaşan taskler — sonraki 7 gün
    @GetMapping("/upcoming")
    public ResponseEntity<List<TaskResponseDto>> getUpcomingTasks() {
        String currentUserId = securityUtils.getCurrentUserId();
        LocalDate today = LocalDate.now();
        LocalDate in7Days = today.plusDays(7);

        List<Task> owned = taskRepository.findUpcomingTasksByOwner(currentUserId, today, in7Days);
        List<Task> assigned = taskRepository.findUpcomingTasksByAssignee(currentUserId, today, in7Days);

        List<Task> all = new ArrayList<>(owned);
        assigned.stream()
                .filter(t -> owned.stream().noneMatch(o -> o.getId().equals(t.getId())))
                .forEach(all::add);
        all.sort(Comparator.comparing(Task::getDueDate));

        return ResponseEntity.ok(all.stream().map(TaskResponseDto::from).toList());
    }

    // Arama — proje içinde
    @GetMapping("/search")
    public ResponseEntity<List<TaskResponseDto>> searchInProject(
            @RequestParam String projectId,
            @RequestParam String query) {
        return ResponseEntity.ok(
                taskRepository.searchInProject(projectId, query)
                        .stream().map(TaskResponseDto::from).toList()
        );
    }

    // Arama — tüm erişilebilir taskler
    @GetMapping("/search/all")
    public ResponseEntity<List<TaskResponseDto>> searchAll(@RequestParam String query) {
        return ResponseEntity.ok(
                taskRepository.searchAllAccessible(securityUtils.getCurrentUserId(), query)
                        .stream().map(TaskResponseDto::from).toList()
        );
    }

    // Filtreleme — durum, öncelik, assignee
    @GetMapping("/filter")
    public ResponseEntity<List<TaskResponseDto>> filterTasks(
            @RequestParam String projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String assignedUserId) {
        return ResponseEntity.ok(
                taskRepository.filterByProject(projectId, status, priority, assignedUserId)
                        .stream().map(TaskResponseDto::from).toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequestDto dto) {
        return ResponseEntity.ok(taskService.updateTask(id, securityUtils.getCurrentUserId(), dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(
            @PathVariable String id,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, securityUtils.getCurrentUserId(), status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id, securityUtils.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
package com.example.TaskManager.domain.label;

import com.example.TaskManager.domain.label.dto.LabelRequestDto;
import com.example.TaskManager.domain.label.dto.LabelResponseDto;
import com.example.TaskManager.domain.project.Project;
import com.example.TaskManager.domain.project.ProjectRepository;
import com.example.TaskManager.domain.task.Task;
import com.example.TaskManager.domain.task.TaskRepository;
import com.example.TaskManager.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LabelController {

    private final LabelRepository labelRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final SecurityUtils securityUtils;

    // Projeye label ekle
    @PostMapping("/projects/{projectId}/labels")
    public ResponseEntity<LabelResponseDto> createLabel(
            @PathVariable String projectId,
            @Valid @RequestBody LabelRequestDto dto) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Label label = new Label();
        label.setName(dto.name());
        label.setColor(dto.color());
        label.setProject(project);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LabelResponseDto.from(labelRepository.save(label)));
    }

    // Projenin tüm labelleri
    @GetMapping("/projects/{projectId}/labels")
    public ResponseEntity<List<LabelResponseDto>> getLabelsByProject(@PathVariable String projectId) {
        return ResponseEntity.ok(
                labelRepository.findByProjectId(projectId)
                        .stream()
                        .map(LabelResponseDto::from)
                        .toList()
        );
    }

    // Task'a label ekle
    @PostMapping("/tasks/{taskId}/labels/{labelId}")
    public ResponseEntity<Void> addLabelToTask(
            @PathVariable String taskId,
            @PathVariable String labelId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        task.getLabels().add(label);
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

    // Task'tan label kaldır
    @DeleteMapping("/tasks/{taskId}/labels/{labelId}")
    public ResponseEntity<Void> removeLabelFromTask(
            @PathVariable String taskId,
            @PathVariable String labelId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.getLabels().removeIf(l -> l.getId().equals(labelId));
        taskRepository.save(task);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/labels/{labelId}")
    public ResponseEntity<Void> deleteLabel(@PathVariable String labelId) {
        labelRepository.deleteById(labelId);
        return ResponseEntity.noContent().build();
    }
}
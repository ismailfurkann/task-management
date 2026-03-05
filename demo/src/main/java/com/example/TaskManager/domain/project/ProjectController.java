package com.example.TaskManager.domain.project;

import com.example.TaskManager.domain.project.ProjectRequestDto;
import com.example.TaskManager.domain.project.ProjectResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // Proje oluştur — owner kim? URL'den al
    // POST /api/projects?ownerId=abc123
    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @RequestParam String ownerId,
            @Valid @RequestBody ProjectRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(ownerId, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable String id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    // Kullanıcının tüm projeleri
    // GET /api/projects?ownerId=abc123
    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getProjectsByOwner(@RequestParam String ownerId) {
        return ResponseEntity.ok(projectService.getProjectsByOwner(ownerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable String id,
            @Valid @RequestBody ProjectRequestDto dto) {
        return ResponseEntity.ok(projectService.updateProject(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
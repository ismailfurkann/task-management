package com.example.TaskManager.domain.project;

import com.example.TaskManager.domain.project.ProjectRequestDto;
import com.example.TaskManager.domain.project.ProjectResponseDto;
import com.example.TaskManager.security.SecurityUtils;
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
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @Valid @RequestBody ProjectRequestDto dto) {
        String ownerId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(ownerId, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable String id) {
        String currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(projectService.getProjectById(id, currentUserId));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getMyProjects() {
        String ownerId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(projectService.getProjectsByOwner(ownerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable String id,
            @Valid @RequestBody ProjectRequestDto dto) {
        String currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(projectService.updateProject(id, currentUserId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        String currentUserId = securityUtils.getCurrentUserId();
        projectService.deleteProject(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
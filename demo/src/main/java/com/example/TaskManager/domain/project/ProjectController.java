package com.example.TaskManager.domain.project;

import com.example.TaskManager.domain.project.dto.ProjectRequestDto;
import com.example.TaskManager.domain.project.dto.ProjectResponseDto;
import com.example.TaskManager.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(@Valid @RequestBody ProjectRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(securityUtils.getCurrentUserId(), dto));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjectsForUser(securityUtils.getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable String id) {
        return ResponseEntity.ok(projectService.getProjectById(id, securityUtils.getCurrentUserId()));
    }

    // Arama — kullanıcının erişebildiği projeler içinde
    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponseDto>> searchProjects(@RequestParam String query) {
        String currentUserId = securityUtils.getCurrentUserId();

        List<Project> owned = projectRepository.searchByOwner(currentUserId, query);
        List<Project> memberOf = projectRepository.searchByMember(currentUserId, query);

        List<Project> all = new ArrayList<>(owned);
        memberOf.stream()
                .filter(p -> owned.stream().noneMatch(o -> o.getId().equals(p.getId())))
                .forEach(all::add);

        return ResponseEntity.ok(all.stream().map(ProjectResponseDto::from).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable String id,
            @Valid @RequestBody ProjectRequestDto dto) {
        return ResponseEntity.ok(projectService.updateProject(id, securityUtils.getCurrentUserId(), dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id, securityUtils.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
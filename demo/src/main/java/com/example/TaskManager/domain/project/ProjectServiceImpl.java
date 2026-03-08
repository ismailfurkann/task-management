package com.example.TaskManager.domain.project;

import com.example.TaskManager.domain.project.dto.ProjectRequestDto;
import com.example.TaskManager.domain.project.dto.ProjectResponseDto;
import com.example.TaskManager.domain.user.User;
import com.example.TaskManager.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public ProjectResponseDto createProject(String ownerId, ProjectRequestDto dto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (projectRepository.existsByNameAndOwnerId(dto.name(), ownerId)) {
            throw new RuntimeException("Project with this name already exists");
        }

        Project project = new Project();
        project.setName(dto.name());
        project.setDescription(dto.description());
        project.setOwner(owner);

        return ProjectResponseDto.from(projectRepository.save(project));
    }

    @Override
    public ProjectResponseDto getProjectById(String id, String currentUserId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Sadece proje sahibi görebilir
        if (!project.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Access denied");
        }

        return ProjectResponseDto.from(project);
    }

    @Override
    public List<ProjectResponseDto> getProjectsByOwner(String ownerId) {
        return projectRepository.findByOwnerId(ownerId)
                .stream()
                .map(ProjectResponseDto::from)
                .toList();
    }

    @Override
    public ProjectResponseDto updateProject(String id, String currentUserId, ProjectRequestDto dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Sadece proje sahibi güncelleyebilir
        if (!project.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Access denied");
        }

        project.setName(dto.name());
        project.setDescription(dto.description());

        return ProjectResponseDto.from(projectRepository.save(project));
    }

    @Override
    public void deleteProject(String id, String currentUserId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Sadece proje sahibi silebilir
        if (!project.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Access denied");
        }

        projectRepository.deleteById(id);
    }
}
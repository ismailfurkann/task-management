package com.example.TaskManager.domain.project;

import com.example.TaskManager.domain.project.ProjectRequestDto;
import com.example.TaskManager.domain.project.ProjectResponseDto;
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
                .orElseThrow(() -> new RuntimeException("User not found with id: " + ownerId));

        // Aynı kullanıcının aynı isimde projesi var mı?
        if (projectRepository.existsByNameAndOwnerId(dto.name(), ownerId)) {
            throw new RuntimeException("Project with this name already exists");
        }

        Project project = new Project();
        project.setName(dto.name());
        project.setDescription(dto.description());
        project.setOwner(owner);

        Project saved = projectRepository.save(project);
        return ProjectResponseDto.from(saved);
    }

    @Override
    public ProjectResponseDto getProjectById(String id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        return ProjectResponseDto.from(project);
    }

    @Override
    public List<ProjectResponseDto> getProjectsByOwner(String ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new RuntimeException("User not found with id: " + ownerId);
        }

        return projectRepository.findByOwnerId(ownerId)
                .stream()
                .map(ProjectResponseDto::from)
                .toList();
    }

    @Override
    public ProjectResponseDto updateProject(String id, ProjectRequestDto dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        project.setName(dto.name());
        project.setDescription(dto.description());

        Project updated = projectRepository.save(project);
        return ProjectResponseDto.from(updated);
    }

    @Override
    public void deleteProject(String id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }
}
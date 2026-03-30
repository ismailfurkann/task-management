package com.example.TaskManager.domain.project;

import com.example.TaskManager.domain.member.InviteStatus;
import com.example.TaskManager.domain.member.MemberRole;
import com.example.TaskManager.domain.member.ProjectMember;
import com.example.TaskManager.domain.member.ProjectMemberRepository;
import com.example.TaskManager.domain.project.dto.ProjectRequestDto;
import com.example.TaskManager.domain.project.dto.ProjectResponseDto;
import com.example.TaskManager.domain.user.User;
import com.example.TaskManager.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    @Override
    public ProjectResponseDto createProject(String ownerId, ProjectRequestDto dto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (projectRepository.existsByNameAndOwnerId(dto.name(), ownerId)) {
            throw new RuntimeException("Bu isimde bir projen zaten var");
        }

        Project project = new Project();
        project.setName(dto.name());
        project.setDescription(dto.description());
        project.setOwner(owner);

        Project saved = projectRepository.save(project);

        // Proje sahibini otomatik olarak OWNER rolüyle üye ekle
        ProjectMember ownerMember = new ProjectMember();
        ownerMember.setProject(saved);
        ownerMember.setUser(owner);
        ownerMember.setRole(MemberRole.OWNER);
        ownerMember.setStatus(InviteStatus.ACCEPTED);
        memberRepository.save(ownerMember);

        return ProjectResponseDto.from(saved);
    }

    @Override
    public ProjectResponseDto getProjectById(String id, String currentUserId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Erişim kontrolü: sahip veya ACCEPTED üye olmalı
        boolean isOwner = project.getOwner().getId().equals(currentUserId);
        boolean isMember = memberRepository.findByProjectIdAndUserId(id, currentUserId)
                .map(m -> m.getStatus() == InviteStatus.ACCEPTED)
                .orElse(false);

        if (!isOwner && !isMember) {
            throw new RuntimeException("Access denied");
        }

        return ProjectResponseDto.from(project);
    }

    @Override
    public List<ProjectResponseDto> getAllProjectsForUser(String userId) {
        // Sahip olduğu projeler
        List<Project> owned = projectRepository.findByOwnerId(userId);

        // Üye olduğu projeler (ACCEPTED, sahip olduğu hariç)
        List<Project> memberOf = projectRepository.findProjectsByMemberId(userId)
                .stream()
                .filter(p -> !p.getOwner().getId().equals(userId))
                .toList();

        List<Project> all = new ArrayList<>();
        all.addAll(owned);
        all.addAll(memberOf);

        return all.stream()
                .map(ProjectResponseDto::from)
                .toList();
    }

    @Override
    public ProjectResponseDto updateProject(String id, String currentUserId, ProjectRequestDto dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Sadece proje sahibi güncelleyebilir
        if (!project.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Access denied — sadece proje sahibi güncelleyebilir");
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
            throw new RuntimeException("Access denied — sadece proje sahibi silebilir");
        }

        projectRepository.deleteById(id);
    }
}
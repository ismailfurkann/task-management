package com.example.TaskManager.domain.member;

import com.example.TaskManager.domain.member.dto.InviteMemberDto;
import com.example.TaskManager.domain.member.dto.ProjectMemberResponseDto;
import com.example.TaskManager.domain.project.Project;
import com.example.TaskManager.domain.project.ProjectRepository;
import com.example.TaskManager.domain.user.User;
import com.example.TaskManager.domain.user.UserRepository;
import com.example.TaskManager.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    // Projenin tüm üyelerini getir
    @GetMapping
    public ResponseEntity<List<ProjectMemberResponseDto>> getMembers(@PathVariable String projectId) {
        return ResponseEntity.ok(
                memberRepository.findByProjectIdAndStatus(projectId, InviteStatus.ACCEPTED)
                        .stream()
                        .map(ProjectMemberResponseDto::from)
                        .toList()
        );
    }

    // Direkt ekle — kullanıcı sistemde kayıtlıysa anında ACCEPTED olarak eklenir
    @PostMapping("/direct")
    public ResponseEntity<ProjectMemberResponseDto> addDirectly(
            @PathVariable String projectId,
            @Valid @RequestBody InviteMemberDto dto) {

        String currentUserId = securityUtils.getCurrentUserId();
        Project project = getProjectAndVerifyOwner(projectId, currentUserId);

        User targetUser = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Bu email ile kayıtlı kullanıcı bulunamadı"));

        // Zaten üye mi?
        if (memberRepository.existsByProjectIdAndUserId(projectId, targetUser.getId())) {
            throw new RuntimeException("Kullanıcı zaten bu projenin üyesi");
        }

        // Proje sahibini tekrar eklemeye çalışıyorsa engelle
        if (targetUser.getId().equals(currentUserId)) {
            throw new RuntimeException("Kendinizi üye olarak ekleyemezsiniz");
        }

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(targetUser);
        member.setRole(MemberRole.MEMBER);
        member.setStatus(InviteStatus.ACCEPTED);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProjectMemberResponseDto.from(memberRepository.save(member)));
    }

    // Email ile davet — token üretilir, kullanıcı linke tıklar
    @PostMapping("/invite")
    public ResponseEntity<String> inviteByEmail(
            @PathVariable String projectId,
            @Valid @RequestBody InviteMemberDto dto) {

        String currentUserId = securityUtils.getCurrentUserId();
        Project project = getProjectAndVerifyOwner(projectId, currentUserId);

        User targetUser = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Bu email ile kayıtlı kullanıcı bulunamadı"));

        if (memberRepository.existsByProjectIdAndUserId(projectId, targetUser.getId())) {
            throw new RuntimeException("Kullanıcı zaten bu projenin üyesi");
        }

        String token = UUID.randomUUID().toString();

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(targetUser);
        member.setRole(MemberRole.MEMBER);
        member.setStatus(InviteStatus.PENDING);
        member.setInviteToken(token);

        memberRepository.save(member);

        // Frontend bu linki kullanıcıya gösterir veya email gönderir
        String inviteLink = "http://localhost:5173/invite/accept?token=" + token;

        return ResponseEntity.ok(inviteLink);
    }

    // Daveti kabul et (token ile)
    @PostMapping("/accept")
    public ResponseEntity<ProjectMemberResponseDto> acceptInvite(@RequestParam String token) {
        ProjectMember member = memberRepository.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Geçersiz veya süresi dolmuş davet"));

        if (member.getStatus() != InviteStatus.PENDING) {
            throw new RuntimeException("Bu davet artık geçerli değil");
        }

        member.setStatus(InviteStatus.ACCEPTED);
        member.setInviteToken(null); // Token tek kullanımlık

        return ResponseEntity.ok(ProjectMemberResponseDto.from(memberRepository.save(member)));
    }

    // Daveti reddet
    @PostMapping("/decline")
    public ResponseEntity<Void> declineInvite(@RequestParam String token) {
        ProjectMember member = memberRepository.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Geçersiz davet"));

        member.setStatus(InviteStatus.DECLINED);
        member.setInviteToken(null);
        memberRepository.save(member);

        return ResponseEntity.ok().build();
    }

    // Üyeyi projeden çıkar (sadece proje sahibi yapabilir)
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable String projectId,
            @PathVariable String memberId) {

        String currentUserId = securityUtils.getCurrentUserId();
        getProjectAndVerifyOwner(projectId, currentUserId);

        memberRepository.deleteById(memberId);
        return ResponseEntity.noContent().build();
    }

    private Project getProjectAndVerifyOwner(String projectId, String currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Access denied — sadece proje sahibi üye yönetebilir");
        }

        return project;
    }
}
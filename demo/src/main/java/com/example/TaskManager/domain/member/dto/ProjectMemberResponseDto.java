package com.example.TaskManager.domain.member.dto;

import com.example.TaskManager.domain.member.InviteStatus;
import com.example.TaskManager.domain.member.MemberRole;
import com.example.TaskManager.domain.member.ProjectMember;

public record ProjectMemberResponseDto(
        String id,
        String userId,
        String userName,
        String userEmail,
        MemberRole role,
        InviteStatus status
) {
    public static ProjectMemberResponseDto from(ProjectMember member) {
        return new ProjectMemberResponseDto(
                member.getId(),
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getRole(),
                member.getStatus()
        );
    }
}
package com.example.TaskManager.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteMemberDto(
        @NotBlank @Email
        String email
) {}
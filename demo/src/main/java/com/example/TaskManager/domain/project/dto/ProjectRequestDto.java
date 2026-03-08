package com.example.TaskManager.domain.project.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequestDto(

        @NotBlank(message = "Project name cannot be blank")
        String name,

        String description
) {}
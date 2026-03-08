package com.example.TaskManager.domain.subtask.dto;

import jakarta.validation.constraints.NotBlank;

public record SubtaskRequestDto(
        @NotBlank(message = "Title cannot be blank")
        String title
) {}
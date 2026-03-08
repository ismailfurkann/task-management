package com.example.TaskManager.domain.label.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LabelRequestDto(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "Color cannot be blank")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code")
        String color
) {}
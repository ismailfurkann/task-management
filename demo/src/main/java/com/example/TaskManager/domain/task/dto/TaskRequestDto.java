package com.example.TaskManager.domain.task.dto;

import com.example.TaskManager.domain.task.Priority;
import com.example.TaskManager.domain.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequestDto(

        @NotBlank(message = "Title cannot be blank")
        String title,

        String description,

        @NotNull(message = "Status cannot be null")
        TaskStatus status,

        @NotNull(message = "Priority cannot be null")
        Priority priority,

        LocalDate dueDate,

        // null olabilir — task atanmamış olabilir
        String assignedUserId
) {}


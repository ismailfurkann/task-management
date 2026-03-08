package com.example.TaskManager.domain.label.dto;

import com.example.TaskManager.domain.label.Label;

public record LabelResponseDto(
        String id,
        String name,
        String color
) {
    public static LabelResponseDto from(Label label) {
        return new LabelResponseDto(
                label.getId(),
                label.getName(),
                label.getColor()
        );
    }
}
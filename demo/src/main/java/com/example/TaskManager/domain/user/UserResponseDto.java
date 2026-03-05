package com.example.TaskManager.domain.user;



import java.time.LocalDateTime;

public record UserResponseDto(

        String id,
        String name,
        String email,
        LocalDateTime createdAt
) {
    // Entity → DTO dönüşümü burada — service'te new UserResponseDto(user) yazmak yeterli
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
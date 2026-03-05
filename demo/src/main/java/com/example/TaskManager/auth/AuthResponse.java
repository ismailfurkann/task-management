package com.example.TaskManager.auth;

public record AuthResponse(
        String token,
        String id,
        String name,
        String email
) {}
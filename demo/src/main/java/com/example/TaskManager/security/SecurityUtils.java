package com.example.TaskManager.security;

import com.example.TaskManager.domain.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public String getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
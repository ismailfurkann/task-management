package com.example.TaskManager.domain.user;

import com.example.TaskManager.domain.user.dto.UserRequestDto;
import com.example.TaskManager.domain.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto dto);

    UserResponseDto getUserById(String id);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUser(String id, UserRequestDto dto);

    void deleteUser(String id);
}
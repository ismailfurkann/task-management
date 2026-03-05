package com.example.TaskManager.domain.user;

import com.example.TaskManager.domain.user.UserRequestDto;
import com.example.TaskManager.domain.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto createUser(UserRequestDto dto) {
        // Email kontrolü
        if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email already in use: " + dto.email());
        }

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password()); // ileride BCrypt ile şifrelenecek

        User saved = userRepository.save(user);
        return UserResponseDto.from(saved);
    }

    @Override
    public UserResponseDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return UserResponseDto.from(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDto::from)
                .toList();
    }

    @Override
    public UserResponseDto updateUser(String id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());

        User updated = userRepository.save(user);
        return UserResponseDto.from(updated);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
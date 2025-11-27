package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UUID addUser(UserRequestDto dto);
    UUID updateUser(UUID uuid, UserRequestDto dto);
    UserResponseDto getUserById(UUID uuid);
    List<UserResponseDto> getAllUsers();
    void deleteUser(UUID uuid);
}

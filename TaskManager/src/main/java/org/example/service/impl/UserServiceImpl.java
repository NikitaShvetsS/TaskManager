package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.exception.UserNotFoundException;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.Role;
import org.example.util.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UUID addUser(UserRequestDto dto) {
        return repository.save(mapper.toEntity(dto)).getUuid();
    }

    @Override
    public UUID updateUser(UUID uuid, UserRequestDto dto) {

        User user = repository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", uuid)));
        mapper.updateUserFromDto(dto, user);

        return repository.save(user).getUuid();
    }

    @Override
    public UserResponseDto getUserById(UUID uuid) {
        return repository.findById(uuid).map(mapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", uuid)));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public void deleteUser(UUID uuid) {
        repository.deleteById(uuid);
    }
}

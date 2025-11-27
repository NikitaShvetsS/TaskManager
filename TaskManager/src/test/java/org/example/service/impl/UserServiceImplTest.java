package org.example.service.impl;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.exception.UserNotFoundException;
import org.example.repository.UserRepository;
import org.example.util.Role;
import org.example.util.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Test
    void addUser() {
        UserRequestDto dto = new UserRequestDto();
        dto.setUsername("TestUser");
        dto.setEmail("test@example.com");
        dto.setPassword("123");

        UUID expectedUuid = UUID.randomUUID();
        User userEntity = User.builder()
                .uuid(expectedUuid)
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.USER)
                .build();

        Mockito.when(userMapper.toEntity(dto)).thenReturn(userEntity);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userEntity);

        UUID result = userService.addUser(dto);

        assertNotNull(result);
        assertEquals(expectedUuid, result);

        Mockito.verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void updateUser_shouldUpdate() {
        UUID userId = UUID.randomUUID();
        UserRequestDto dto = new UserRequestDto();
        dto.setUsername("UpdatedUser");
        dto.setEmail("updated@example.com");
        dto.setPassword("newPassword");

        User existingUser = new User();
        existingUser.setUuid(userId);
        existingUser.setUsername("OldUser");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldPassword");
        existingUser.setRole(Role.USER);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID result = userService.updateUser(userId, dto);

        assertNotNull(result);
        assertEquals(userId, result);
        assertEquals("UpdatedUser", existingUser.getUsername());
        assertEquals("updated@example.com", existingUser.getEmail());
        assertEquals("newPassword", existingUser.getPassword());

        Mockito.verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        UserRequestDto dto = new UserRequestDto();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, dto));

        Mockito.verify(userRepository, never()).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void getUserById_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setUuid(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        Mockito.verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_whenUserNotFound() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        Mockito.verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setUuid(UUID.randomUUID());
        user1.setUsername("User1");

        User user2 = new User();
        user2.setUuid(UUID.randomUUID());
        user2.setUsername("User2");

        List<User> users = List.of(user1, user2);

        UserResponseDto dto1 = new UserResponseDto();
        dto1.setId(user1.getUuid());
        dto1.setUsername(user1.getUsername());

        UserResponseDto dto2 = new UserResponseDto();
        dto2.setId(user2.getUuid());
        dto2.setUsername(user2.getUsername());

        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.when(userMapper.toDto(user1)).thenReturn(dto1);
        Mockito.when(userMapper.toDto(user2)).thenReturn(dto2);

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1.getId(), result.get(0).getId());
        assertEquals(dto2.getId(), result.get(1).getId());

        Mockito.verify(userMapper, times(1)).toDto(user1);
        Mockito.verify(userMapper, times(1)).toDto(user2);
    }

    @Test
    void deleteUser() {
        UUID uuid = UUID.randomUUID();

        userService.deleteUser(uuid);

        Mockito.verify(userRepository, times(1)).deleteById(uuid);
    }
}
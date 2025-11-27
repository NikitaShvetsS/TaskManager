package org.example.service.impl;

import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test_user")
            .withPassword("test_pass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void addUser() {
        UserRequestDto dto = new UserRequestDto("test_user", "test@mail.com", "password");
        UUID id = userService.addUser(dto);
        User user = userRepository.findById(id).orElseThrow();
        assertEquals("test_user", user.getUsername());
        assertEquals("test@mail.com", user.getEmail());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void updateUser() {
        UserRequestDto dto = new UserRequestDto("old_user", "old@mail.com", "password");
        UUID id = userService.addUser(dto);

        UserRequestDto updatedDto = new UserRequestDto("new_user", "new@mail.com", "newpass1");
        userService.updateUser(id, updatedDto);

        User user = userRepository.findById(id).orElseThrow();
        assertEquals("new_user", user.getUsername());
        assertEquals("new@mail.com", user.getEmail());
        assertEquals("newpass1", user.getPassword());
    }

    @Test
    void getUserById() {
        UserRequestDto dto = new UserRequestDto("test_user", "test@mail.com", "password");
        UUID id = userService.addUser(dto);

        UserResponseDto response = userService.getUserById(id);

        assertEquals("test_user", response.getUsername());
        assertEquals("test@mail.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void getAllUsers() {
        userService.addUser(new UserRequestDto("user1", "user1@mail.com", "password"));
        userService.addUser(new UserRequestDto("user2", "user2@mail.com", "password"));

        List<UserResponseDto> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user1")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user2")));
    }

    @Test
    void deleteUser() {
        UserRequestDto dto = new UserRequestDto("test_user", "test@mail.com", "password");
        UUID id = userService.addUser(dto);

        userService.deleteUser(id);

        assertFalse(userRepository.findById(id).isPresent());
    }
}
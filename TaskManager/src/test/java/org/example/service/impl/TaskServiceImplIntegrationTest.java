package org.example.service.impl;


import org.example.dto.TaskRequestDto;
import org.example.dto.TaskResponseDto;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.service.TaskService;
import org.example.util.PriorityType;
import org.example.util.Role;
import org.example.util.StatusType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class TaskServiceImplIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }
    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;
    private final User testUser = new User();

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        userRepository.deleteAll();


        testUser.setEmail("user@test.com");
        testUser.setUsername("testUser");
        testUser.setPassword("pass");
        testUser.setRole(Role.USER);
        userRepository.save(testUser);

    }


    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void addTask() {
        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Test title");
        dto.setDescription("Test description");

        Long id = taskService.addTask(dto);

        assertThat(id).isNotNull();
        Task task = taskRepository.findById(id).orElseThrow();
        assertThat(task.getTitle()).isEqualTo("Test title");
        assertThat(task.getDescription()).isEqualTo("Test description");
    }

    @Test
    void updateTask() {
        Task task = new Task();
        task.setTitle("Old Task");
        task.setDescription("Old Description");
        task.setPriority(PriorityType.LOW);
        task.setStatus(StatusType.TODO);
        task.setUser(testUser);
        taskRepository.save(task);

        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Updated Task");
        dto.setDescription("Updated Description");
        dto.setPriority(PriorityType.HIGH);
        dto.setStatus(StatusType.IN_PROGRESS);
        dto.setDeadline(LocalDate.now());

        Long updatedId = taskService.updateTask(task.getId(), dto);
        Task updatedTask = taskRepository.findById(updatedId).orElseThrow();

        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals(PriorityType.HIGH, updatedTask.getPriority());
        assertEquals(StatusType.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    void getTaskById() {
        Task task = new Task();
        task.setTitle("Sample Task");
        task.setUser(testUser);
        taskRepository.save(task);

        TaskResponseDto dto = taskService.getTaskById(task.getId());

        assertEquals("Sample Task", dto.getTitle());
    }

    @Test
    void getAllTasks() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setUser(testUser);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setUser(testUser);

        taskRepository.saveAll(List.of(task1, task2));

        List<TaskResponseDto> tasks = taskService.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void deleteTask() {
        Task task = new Task();
        task.setTitle("To Delete");
        task.setUser(testUser);
        taskRepository.save(task);

        taskService.deleteTask(task.getId());
        assertFalse(taskRepository.existsById(task.getId()));
    }
}
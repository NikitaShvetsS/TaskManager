package org.example.service.impl;

import org.example.dto.TaskRequestDto;
import org.example.dto.TaskResponseDto;
import org.example.entity.Task;
import org.example.exception.TaskNotFoundException;
import org.example.repository.TaskRepository;
import org.example.util.TaskMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private TaskRepository taskRepository;

    @Test
    void addTask() {
        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Test task");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test task");

        Mockito.when(taskMapper.toEntity(dto)).thenReturn(task);
        Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);

        Long result = taskService.addTask(dto);

        assertNotNull(result);
        assertEquals(1L, result);

        Mockito.verify(taskRepository, times(1)).save(Mockito.any(Task.class));
    }

    @Test
    void updateTask_shouldUpdate() {
        Long taskId = 1L;
        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Updated task");

        Task existingTask = new Task();
        existingTask.setId(taskId);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Long result = taskService.updateTask(taskId, dto);

        assertNotNull(result);
        assertEquals(taskId, result);

        Mockito.verify(taskRepository, times(1)).findById(taskId);
        Mockito.verify(taskRepository, times(1)).save(existingTask);
    }
    @Test
    void updateTask_whenTaskNotFound() {
        Long taskId = 1L;
        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Updated task");

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTask(taskId, dto)
        );

        assertEquals("Task with id 1 not found", exception.getMessage());
        Mockito.verify(taskRepository, times(1)).findById(taskId);
        Mockito.verify(taskRepository, never()).save(Mockito.any());
    }

    @Test
    void getTaskById() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(taskId);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(taskMapper.toDto(task)).thenReturn(dto);

        TaskResponseDto result = taskService.getTaskById(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());

        Mockito.verify(taskRepository, times(1)).findById(taskId);
        Mockito.verify(taskMapper, times(1)).toDto(task);
    }

    @Test
    void getTaskById_whenTaskNotFound() {
        Long taskId = 1L;

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskById(taskId)
        );

        assertEquals("Task with id 1 not found", exception.getMessage());
        Mockito.verify(taskRepository, times(1)).findById(taskId);
        Mockito.verify(taskMapper, never()).toDto(Mockito.any());
    }

    @Test
    void getAllTasks() {
        Task task1 = new Task();
        task1.setId(1L);
        Task task2 = new Task();
        task2.setId(2L);

        TaskResponseDto dto1 = new TaskResponseDto();
        dto1.setId(1L);
        TaskResponseDto dto2 = new TaskResponseDto();
        dto2.setId(2L);

        Mockito.when(taskRepository.findAll()).thenReturn(List.of(task1, task2));
        Mockito.when(taskMapper.toDto(task1)).thenReturn(dto1);
        Mockito.when(taskMapper.toDto(task2)).thenReturn(dto2);

        List<TaskResponseDto> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void deleteTask() {
        Long taskId = 1L;

        taskService.deleteTask(taskId);

        Mockito.verify(taskRepository, times(1)).deleteById(taskId);
    }
}
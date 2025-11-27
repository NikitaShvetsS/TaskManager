package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.TaskRequestDto;
import org.example.dto.TaskResponseDto;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.exception.TaskNotFoundException;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.service.TaskService;
import org.example.util.TaskMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final UserRepository userRepository;

    @Override
    public Long addTask(TaskRequestDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is unauthenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(String.format("User with username %s not found", email)));

        Task task = mapper.toEntity(dto);

        task.setUser(user);

        return repository.save(task).getId();
    }

    public Long updateTask(Long id, TaskRequestDto dto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Not found id: " + id));

       mapper.updateTaskFromDto(dto, task);

        repository.save(task);
        return task.getId();
    }


    @Override
    public TaskResponseDto getTaskById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new TaskNotFoundException(String.format("Task with id %d not found", id)));
    }

    @Override
    public List<TaskResponseDto> getAllTasks() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public void deleteTask(Long id) {
        repository.deleteById(id);
    }
}

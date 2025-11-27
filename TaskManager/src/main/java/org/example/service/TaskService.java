package org.example.service;

import org.example.dto.TaskRequestDto;
import org.example.dto.TaskResponseDto;

import java.util.List;

public interface TaskService {

    Long addTask(TaskRequestDto dto);
    Long updateTask(Long id, TaskRequestDto dto);
    TaskResponseDto getTaskById(Long id);
    List<TaskResponseDto> getAllTasks();
    void deleteTask(Long id);

}

package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.TaskRequestDto;
import org.example.dto.TaskResponseDto;
import org.example.service.impl.TaskServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskServiceImpl taskService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addTask(@RequestBody TaskRequestDto dto){
        Long taskId = taskService.addTask(dto);
        log.info("adding a new task");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Task successfully created");
        response.put("taskId", taskId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateTask(@PathVariable("id") Long id, @ RequestBody TaskRequestDto dto){
        Long taskId = taskService.updateTask(id, dto);
        log.info("updating task");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Task successfully updated");
        response.put("taskId", taskId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public TaskResponseDto getTaskById(@PathVariable("id") Long id){
        log.info("getting task by id");
        return taskService.getTaskById(id);
    }
    @GetMapping("/all")
    public List<TaskResponseDto> getAllTasks(){
        log.info("getting all tasks");
        return taskService.getAllTasks();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTaskById(@PathVariable("id") Long id){
        taskService.deleteTask(id);
        log.info("deleting task");
        Map<String, Object> response = new HashMap<>();
        response.put("taskId", id);
        response.put("message", "Task successfully deleted");
        return ResponseEntity.ok(response);
    }

}

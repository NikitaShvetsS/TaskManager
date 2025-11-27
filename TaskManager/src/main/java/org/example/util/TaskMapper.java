package org.example.util;

import org.example.dto.TaskRequestDto;
import org.example.dto.TaskResponseDto;
import org.example.entity.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public abstract class TaskMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateTaskFromDto(TaskRequestDto dto, @MappingTarget Task task);

    public Task toEntity(TaskRequestDto dto) {
        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .priority(dto.getPriority())
                .deadline(dto.getDeadline())
                .build();
    }

    public TaskResponseDto toDto(Task task) {

        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .priority(task.getPriority())
                .status(task.getStatus())
                .userId(task.getUser().getUuid())
                .build();

    }
}

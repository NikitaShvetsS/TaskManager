package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.util.PriorityType;
import org.example.util.StatusType;

import java.time.LocalDate;

@Getter
@Setter
public class TaskRequestDto {

    private String title;
    private String description;
    private StatusType status;
    private PriorityType priority;
    private LocalDate deadline;

}

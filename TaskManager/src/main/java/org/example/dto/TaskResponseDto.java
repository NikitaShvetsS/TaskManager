package org.example.dto;

import lombok.*;
import org.example.util.PriorityType;
import org.example.util.StatusType;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TaskResponseDto {

    private Long id;
    private String title;
    private String description;
    private StatusType status;
    private PriorityType priority;
    private LocalDate deadline;
    private UUID userId;

}

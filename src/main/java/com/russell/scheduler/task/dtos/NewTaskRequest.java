package com.russell.scheduler.task.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewTaskRequest {
    private String name;
    private String description = ""; // defaults blank
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID projectId;
}

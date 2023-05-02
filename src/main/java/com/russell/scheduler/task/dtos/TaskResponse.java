package com.russell.scheduler.task.dtos;

import com.russell.scheduler.task.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TaskResponse {
    private UUID id;
    private String name;
    private String description = ""; // defaults blank
    private UUID projectId;
    private UUID assigneeId;
    private UUID assignerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createdDate;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.projectId = task.getProject().getId();
        this.assigneeId = task.getAssignee().getId();
        this.assignerId = task.getAssigner().getId();
        this.startDate = task.getStartDate();
        this.endDate = task.getEndDate();
        this.createdDate = task.getCreatedDate();
    }
}

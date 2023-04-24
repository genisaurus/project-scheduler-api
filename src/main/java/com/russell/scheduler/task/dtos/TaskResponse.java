package com.russell.scheduler.task.dtos;

import com.russell.scheduler.task.Task;
import com.russell.scheduler.project.dtos.ProjectResponse;
import com.russell.scheduler.resource.dtos.ResourceResponse;
import com.russell.scheduler.user.dtos.UserResponse;
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
    private ResourceResponse assignee;
    private UserResponse assigner;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectResponse project;
    private LocalDate createdDate;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.assignee = new ResourceResponse(task.getAssignee());
        this.assigner = new UserResponse(task.getAssigner());
        this.startDate = task.getStartDate();
        this.endDate = task.getEndDate();
        this.project = new ProjectResponse(task.getProject());
        this.createdDate = task.getCreatedDate();
    }
}
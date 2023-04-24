package com.russell.scheduler.dtos;

import com.russell.scheduler.entities.Project;
import com.russell.scheduler.entities.Resource;
import com.russell.scheduler.entities.Task;
import com.russell.scheduler.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TaskResponse {
    private UUID id;
    private String name;
    private String description = ""; // defaults blank
    private Resource assignee;
    private User assigner;
    private LocalDate startDate;
    private LocalDate endDate;
    private Project project;
    private LocalDate createdDate;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.assignee = task.getAssignee();
        this.assigner = task.getAssigner();
        this.startDate = task.getStartDate();
        this.endDate = task.getEndDate();
        this.project = task.getProject();
        this.createdDate = task.getCreatedDate();
    }
}

package com.russell.scheduler.dtos;

import com.russell.scheduler.entities.Project;
import com.russell.scheduler.entities.Resource;
import com.russell.scheduler.entities.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ProjectResponse {
    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Resource owner;
    private Set<Task> tasks;

    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.owner = project.getOwner();
        this.tasks = project.getTasks();
    }
}

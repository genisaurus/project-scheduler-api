package com.russell.scheduler.project.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.russell.scheduler.resource.dtos.ResourceResponse;
import com.russell.scheduler.project.Project;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ProjectResponse {
    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private ResourceResponse owner;


    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.owner = new ResourceResponse(project.getOwner());
    }
}

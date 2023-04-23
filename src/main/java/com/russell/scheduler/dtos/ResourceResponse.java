package com.russell.scheduler.dtos;

import com.russell.scheduler.entities.Project;
import com.russell.scheduler.entities.Resource;
import com.russell.scheduler.entities.Task;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ResourceResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<Project> projects;
    private Set<Task> assignedTasks;

    public ResourceResponse(Resource resource) {
        this.id = resource.getId();
        this.email = resource.getEmail();
        this.firstName = resource.getFirstName();
        this.lastName = resource.getLastName();
        this.projects = resource.getProjects();
        this.assignedTasks = resource.getAssignedTasks();
    }
}

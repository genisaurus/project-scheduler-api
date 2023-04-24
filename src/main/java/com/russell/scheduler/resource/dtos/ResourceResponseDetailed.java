package com.russell.scheduler.resource.dtos;

import com.russell.scheduler.task.dtos.TaskResponse;
import com.russell.scheduler.resource.Resource;
import com.russell.scheduler.project.dtos.ProjectResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
// This class exists to prevent circular references between Resource.tasks -> Task.project
// and Resource.projects -> Project.owner
public class ResourceResponseDetailed extends ResourceResponse {

    private Set<ProjectResponse> projects;
    private Set<TaskResponse> assignedTasks;

    public ResourceResponseDetailed(Resource resource) {
        super(resource);
        this.projects = resource.getProjects().stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toSet());
        this.assignedTasks = resource.getAssignedTasks().stream()
                .map(TaskResponse::new)
                .collect(Collectors.toSet());
    }
}

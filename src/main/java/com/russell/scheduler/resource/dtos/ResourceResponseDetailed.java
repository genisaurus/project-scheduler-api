package com.russell.scheduler.resource.dtos;

import com.russell.scheduler.task.dtos.TaskResponseWithProject;
import com.russell.scheduler.resource.Resource;
import com.russell.scheduler.project.dtos.ProjectResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
// This class exists to prevent circular references between Resource.tasks -> Task.project
// and Resource.projects -> Project.owner
public class ResourceResponseDetailed extends ResourceResponse {

    private Set<ProjectResponse> projects;
    private Set<TaskResponseWithProject> assignedTasks;

    public ResourceResponseDetailed(Resource resource) {
        super(resource);
        this.projects = resource.getProjects().stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toSet());
        this.assignedTasks = resource.getAssignedTasks().stream()
                .map(TaskResponseWithProject::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "ResourceResponseDetailed{" +
                super.toString() +
                ", projects=" + projects +
                ", assignedTasks=" + assignedTasks +
                '}';
    }
}

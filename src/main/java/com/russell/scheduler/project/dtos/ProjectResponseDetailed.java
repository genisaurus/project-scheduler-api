package com.russell.scheduler.project.dtos;

import com.russell.scheduler.project.Project;
import com.russell.scheduler.task.dtos.TaskResponseWithResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
// This class exists to prevent circular references between Resources
// and Tasks
public class ProjectResponseDetailed extends ProjectResponse {

    private Set<TaskResponseWithResource> tasks;

    public ProjectResponseDetailed(Project project) {
        super(project);
        this.tasks = project.getTasks().stream()
                .map(TaskResponseWithResource::new)
                .collect(Collectors.toSet());
    }

}

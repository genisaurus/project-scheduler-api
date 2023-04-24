package com.russell.scheduler.project.dtos;

import com.russell.scheduler.project.Project;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
// This class exists to prevent circular references between Project.tasks -> Task.project
public class TaskResponse extends ProjectResponse {

    private Set<com.russell.scheduler.task.dtos.TaskResponse> tasks;

    public TaskResponse(Project project) {
        super(project);
        this.tasks = project.getTasks().stream()
                .map(com.russell.scheduler.task.dtos.TaskResponse::new)
                .collect(Collectors.toSet());
    }
}

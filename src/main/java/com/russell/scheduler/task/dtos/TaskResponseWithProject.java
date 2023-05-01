package com.russell.scheduler.task.dtos;

import com.russell.scheduler.project.dtos.ProjectResponse;
import com.russell.scheduler.task.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
// DTO for displaying Task data embedded in a Resource DTO
// Prevents circular references to assigned Resource by omitting
// Resource information
public class TaskResponseWithProject extends TaskResponse {

    private ProjectResponse project;

    public TaskResponseWithProject(Task task) {
        super(task);
        this.project = new ProjectResponse(task.getProject());
    }
}

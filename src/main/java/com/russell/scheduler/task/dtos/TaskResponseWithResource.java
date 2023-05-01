package com.russell.scheduler.task.dtos;

import com.russell.scheduler.resource.dtos.ResourceResponse;
import com.russell.scheduler.task.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
// DTO for displaying Task data embedded in a Project DTO
// Prevents circular references to assigned Project by omitting
// Project information
public class TaskResponseWithResource extends TaskResponse {

    private ResourceResponse assignee;

    public TaskResponseWithResource(Task task) {
        super(task);
        this.assignee = new ResourceResponse(task.getAssignee());
    }
}

package com.russell.scheduler.dtos;

import com.russell.scheduler.entities.Project;
import com.russell.scheduler.entities.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class NewTaskRequest {
    private String name;
    private String description = ""; // defaults blank
    private LocalDate startDate;
    private LocalDate endDate;
    private Project project;

    public Task extractTask() {
        return new Task(name, description,startDate,endDate,project);
    }
}

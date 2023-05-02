package com.russell.scheduler.task.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignment {
    @NotNull
    private UUID taskId;
    @NotNull
    private UUID resourceId;
}

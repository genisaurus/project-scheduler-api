package com.russell.scheduler.task.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TaskAssignment {
    @NotNull
    private UUID resourceId;
    @NotNull
    private UUID taskId;
}

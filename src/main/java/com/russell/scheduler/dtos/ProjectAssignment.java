package com.russell.scheduler.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ProjectAssignment {
    @NotNull
    private UUID resourceId;
    @NotNull
    private UUID projectId;
}

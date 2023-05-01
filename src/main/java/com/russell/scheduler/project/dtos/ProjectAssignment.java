package com.russell.scheduler.project.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAssignment {
    @NotNull
    private UUID projectId;
    @NotNull
    private UUID resourceId;
}

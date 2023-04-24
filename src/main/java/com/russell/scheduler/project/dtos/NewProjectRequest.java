package com.russell.scheduler.project.dtos;

import com.russell.scheduler.project.Project;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class NewProjectRequest {
    @NotNull
    @Length(min = 3)
    private String name;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;

    public Project extractProject() {
        return new Project(name, startDate, endDate);
    }
}

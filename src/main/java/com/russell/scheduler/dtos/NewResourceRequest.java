package com.russell.scheduler.dtos;

import com.russell.scheduler.entities.Project;
import com.russell.scheduler.entities.Resource;
import com.russell.scheduler.entities.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class NewResourceRequest {
    @NotNull
    @Pattern(regexp = "^.+@.+\\..+$")
    private String email;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    public Resource extractResource() {
        return new Resource(email, firstName, lastName);
    }
}

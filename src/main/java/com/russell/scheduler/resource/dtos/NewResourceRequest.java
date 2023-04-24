package com.russell.scheduler.resource.dtos;

import com.russell.scheduler.resource.Resource;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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

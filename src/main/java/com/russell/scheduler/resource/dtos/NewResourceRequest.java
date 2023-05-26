package com.russell.scheduler.resource.dtos;

import com.russell.scheduler.resource.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewResourceRequest {
    @NotNull
    @Email
    private String email;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    public Resource extractResource() {
        return new Resource(email, firstName, lastName);
    }
}

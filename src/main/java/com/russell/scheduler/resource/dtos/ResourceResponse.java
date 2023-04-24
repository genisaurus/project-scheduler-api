package com.russell.scheduler.resource.dtos;

import com.russell.scheduler.resource.Resource;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@NoArgsConstructor
public class ResourceResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;


    public ResourceResponse(Resource resource) {

        this.id = resource.getId();
        this.email = resource.getEmail();
        this.firstName = resource.getFirstName();
        this.lastName = resource.getLastName();

    }
}

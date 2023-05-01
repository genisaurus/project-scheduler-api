package com.russell.scheduler.user.dtos;

import com.russell.scheduler.user.User;
import com.russell.scheduler.user.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotNull
    @Size(min = 4)
    private String username;

    @NotNull
    @Size(min = 8)
    // min 8 chars, 1+ uppercase letter, 1+ lowercase letter, 1+ number, 1+ special char
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private String password;

    @NotNull
    private String email;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String roleName;

    public User extractUser() {
        return new User(username, email, firstName, lastName, password);
    }

}

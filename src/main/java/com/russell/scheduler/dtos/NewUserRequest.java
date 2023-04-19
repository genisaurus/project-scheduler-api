package com.russell.scheduler.dtos;

import com.russell.scheduler.entities.User;
import com.russell.scheduler.entities.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
    private UserRole role;

    public User extractUser() {
        return new User(username, email, firstName, lastName, password, role);
    }

    @Override
    public String toString() {
        return "NewUserRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                '}';
    }
}

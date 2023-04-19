package com.russell.scheduler.auth.dtos;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Data
@NoArgsConstructor
public class AuthRequest {
    @Length(min = 3)
    String username;
    @Pattern(regexp = "\"^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@$!%*?&])[A-Za-z\\\\d@$!%*?&]{8,}$\"")
    String password;

    @Override
    public String toString() {
        return "AuthRequest{" +
                "username='" + username + '\'' +
                '}';
    }
}

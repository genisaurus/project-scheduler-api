package com.russell.scheduler.auth.dtos;

import com.russell.scheduler.user.dtos.UserResponse;
import com.russell.scheduler.user.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Principal {

    private String authUserId;
    private String authUserRole;

    public Principal(UserResponse user) {
        this.authUserId = user.getId().toString();
        this.authUserRole = user.getRoleName();
    }

    public Principal(String authUserId, String authUserRole) {
        this.authUserId = authUserId;
        this.authUserRole = authUserRole;
    }
}

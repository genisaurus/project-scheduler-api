package com.russell.scheduler.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class AuthRequest {
    String username;
    String password;
}

package com.russell.scheduler.controllers;

import com.russell.scheduler.dto.NewUserRequest;
import com.russell.scheduler.dto.RecordCreationResponse;
import com.russell.scheduler.dto.UserResponse;
import com.russell.scheduler.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = "application/json")
    public List<UserResponse> fetchAllUsers() {
        return userService.fetchAllUsers();
    }

    @GetMapping(produces = "application/json")
    public UserResponse fetchById(@RequestParam UUID id) {
        return userService.fetchUserById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public RecordCreationResponse createNewUser(@RequestBody NewUserRequest req){
        return userService.createUser(req);
    }
}

package com.russell.scheduler.controllers;

import com.russell.scheduler.dtos.NewUserRequest;
import com.russell.scheduler.dtos.RecordCreationResponse;
import com.russell.scheduler.dtos.ResourceResponse;
import com.russell.scheduler.dtos.UserResponse;
import com.russell.scheduler.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = "application/json")
    public Set<UserResponse> fetchAllUsers() {
        return userService.findAll();
    }

    @GetMapping(value="id/{id}", produces = "application/json")
    public UserResponse getSingleResource(@PathVariable(name="id") UUID userId) {
        return userService.findOne(userId);
    }

    @GetMapping(value = "/search", produces = "application/json")
    public Set<UserResponse> search(@RequestParam Map<String, String> params) {
        return userService.search(params);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public RecordCreationResponse createNewUser(@RequestBody NewUserRequest req){
        return userService.create(req);
    }
}

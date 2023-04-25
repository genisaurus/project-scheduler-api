package com.russell.scheduler.user;

import com.russell.scheduler.user.dtos.NewUserRequest;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.user.dtos.UserResponse;
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
    public UserResponse getSingleUser(@PathVariable(name="id") UUID userId) {
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

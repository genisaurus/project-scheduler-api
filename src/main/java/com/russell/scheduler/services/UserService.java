package com.russell.scheduler.services;

import com.russell.scheduler.dto.AuthRequest;
import com.russell.scheduler.dto.NewUserRequest;
import com.russell.scheduler.dto.RecordCreationResponse;
import com.russell.scheduler.dto.UserResponse;
import com.russell.scheduler.entities.User;
import com.russell.scheduler.exceptions.RecordNotFoundException;
import com.russell.scheduler.repos.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(AuthRequest dto) {
        return userRepository.findUserByUsernameAndPassword(dto.getUsername(), dto.getPassword()) == null;
    }

    public UserResponse fetchByUsernameAndPassword(AuthRequest dto) {
        return new UserResponse(
                userRepository.findUserByUsernameAndPassword(dto.getUsername(), dto.getPassword())
        );
    }

    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponse fetchUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserResponse::new)
                .orElseThrow(RecordNotFoundException::new);
    }

    public RecordCreationResponse createUser(@Valid NewUserRequest req) {
        User user = req.extractUser();
        user.setId(UUID.randomUUID());
        userRepository.save(user);
        return new RecordCreationResponse(user.getId().toString());
    }
}

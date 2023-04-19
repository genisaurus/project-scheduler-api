package com.russell.scheduler.services;

import com.russell.scheduler.auth.dtos.AuthRequest;
import com.russell.scheduler.dtos.NewUserRequest;
import com.russell.scheduler.dtos.RecordCreationResponse;
import com.russell.scheduler.dtos.UserResponse;
import com.russell.scheduler.entities.User;
import com.russell.scheduler.exceptions.InvalidCredentialsException;
import com.russell.scheduler.exceptions.RecordNotFoundException;
import com.russell.scheduler.exceptions.RecordPersistenceException;
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

    public UserResponse authenticate(@Valid AuthRequest req) {
        return userRepository.findUserByUsernameAndPassword(req.getUsername(), req.getPassword())
                .map(UserResponse::new)
                .orElseThrow(InvalidCredentialsException::new);
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

        // check DB for existing users with provided username/email
        if (userRepository.existsByUsername(req.getUsername()))
            throw new RecordPersistenceException("That username is taken");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new RecordPersistenceException("That email address is already associated with another user");

        user.setId(UUID.randomUUID());
        userRepository.save(user);
        return new RecordCreationResponse(user.getId().toString());
    }
}

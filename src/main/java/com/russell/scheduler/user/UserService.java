package com.russell.scheduler.user;

import com.russell.scheduler.auth.dtos.AuthRequest;
import com.russell.scheduler.common.EntitySearcher;
import com.russell.scheduler.user.dtos.NewUserRequest;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.user.dtos.UserResponse;
import com.russell.scheduler.common.exceptions.InvalidCredentialsException;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.common.exceptions.RecordPersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;
    private EntitySearcher entitySearcher;

    @Autowired
    public UserService(UserRepository userRepository, EntitySearcher entitySearcher) {
        this.userRepository = userRepository;
        this.entitySearcher = entitySearcher;
    }

    public UserResponse authenticate(@Valid AuthRequest req) {
        return userRepository.findUserByUsernameAndPassword(req.getUsername(), req.getPassword())
                .map(UserResponse::new)
                .orElseThrow(InvalidCredentialsException::new);
    }

    public Set<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toSet());
    }

    public UserResponse findOne(UUID resourceId) {
        return userRepository.findById(resourceId)
                .map(UserResponse::new)
                .orElseThrow(RecordNotFoundException::new);
    }

    public Set<UserResponse> search(Map<String, String> params) {
        if (params.isEmpty())
            return findAll();

        Set<User> results = entitySearcher.search(params, User.class);
        if (results.isEmpty())
            throw new RecordNotFoundException();
        return results.stream()
                .map(UserResponse::new)
                .collect(Collectors.toSet());
    }

    public RecordCreationResponse create(@Valid NewUserRequest req) {
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

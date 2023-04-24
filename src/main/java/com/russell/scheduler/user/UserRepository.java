package com.russell.scheduler.user;

import com.russell.scheduler.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findUserByUsernameAndPassword(String username, String password);
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}

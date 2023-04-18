package com.russell.scheduler.repos;

import com.russell.scheduler.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findUserByUsernameAndPassword(String username, String password);
}

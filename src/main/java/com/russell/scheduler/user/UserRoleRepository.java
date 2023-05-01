package com.russell.scheduler.user;

import com.russell.scheduler.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

    Optional<UserRole> findUserRoleByRoleName(String name);
}

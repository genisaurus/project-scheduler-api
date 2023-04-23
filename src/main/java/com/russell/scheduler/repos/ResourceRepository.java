package com.russell.scheduler.repos;

import com.russell.scheduler.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResourceRepository extends JpaRepository<Resource, UUID> {

    boolean existsByEmail(String email);
}

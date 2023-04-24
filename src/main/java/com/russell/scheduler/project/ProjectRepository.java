package com.russell.scheduler.project;

import com.russell.scheduler.resource.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Set<Project> findByOwnerIsNull();
    Set<Project> findByOwner(Resource owner);
}

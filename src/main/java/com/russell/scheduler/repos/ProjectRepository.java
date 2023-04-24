package com.russell.scheduler.repos;

import com.russell.scheduler.entities.Project;
import com.russell.scheduler.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Set<Project> findByOwnerIsNull();
    Set<Project> findByOwner(Resource owner);
}

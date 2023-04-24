package com.russell.scheduler.repos;

import com.russell.scheduler.entities.Project;
import com.russell.scheduler.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    Set<Task> findTasksByProject(Project project);
}

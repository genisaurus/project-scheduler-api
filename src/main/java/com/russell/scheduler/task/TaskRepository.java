package com.russell.scheduler.task;

import com.russell.scheduler.project.Project;
import com.russell.scheduler.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    Set<Task> findTasksByProject(Project project);
}

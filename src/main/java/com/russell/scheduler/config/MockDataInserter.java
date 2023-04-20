package com.russell.scheduler.config;

import com.russell.scheduler.entities.User;
import com.russell.scheduler.entities.UserRole;
import com.russell.scheduler.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@Profile("local")
public class MockDataInserter implements CommandLineRunner {

    private final UserRoleRepository userRoleRepo;
    private final UserRepository userRepo;
    private final ResourceRepository resourceRepo;
    private final ProjectRepository projectRepo;
    private final TaskRepository taskRepo;

    @Autowired
    public MockDataInserter(UserRoleRepository userRoleRepo,
                            UserRepository userRepo,
                            ResourceRepository resourceRepo,
                            ProjectRepository projectRepo,
                            TaskRepository taskRepo) {
        this.userRoleRepo = userRoleRepo;
        this.userRepo = userRepo;
        this.resourceRepo = resourceRepo;
        this.projectRepo = projectRepo;
        this.taskRepo = taskRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        UserRole adminRole = new UserRole(1, "admin", 1);
        userRoleRepo.save(adminRole);

        User user1 = new User("test", "test@test.com", "Urist", "McTester", "P@ssword1", adminRole);
        user1.setId(UUID.randomUUID());
        userRepo.save(user1);

    }
}

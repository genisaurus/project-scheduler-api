package com.russell.scheduler.task;

import com.russell.scheduler.auth.TokenService;
import com.russell.scheduler.common.EntitySearcher;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.resource.Resource;
import com.russell.scheduler.task.dtos.NewTaskRequest;
import com.russell.scheduler.task.dtos.TaskAssignment;
import com.russell.scheduler.task.dtos.TaskResponse;
import com.russell.scheduler.user.User;
import com.russell.scheduler.common.exceptions.InvalidJWTException;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.project.ProjectRepository;
import com.russell.scheduler.resource.ResourceRepository;
import com.russell.scheduler.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private ResourceRepository resourceRepository;
    private UserRepository userRepository;
    private EntitySearcher entitySearcher;
    private TokenService tokenService;

    @Autowired
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository,
                       ResourceRepository resourceRepository, UserRepository userRepository,
                       EntitySearcher entitySearcher, TokenService tokenService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
        this.entitySearcher = entitySearcher;
        this.tokenService = tokenService;
    }

    public TaskResponse findOne(UUID taskID) {
        return taskRepository.findById(taskID)
                .map(TaskResponse::new)
                .orElseThrow(RecordNotFoundException::new);
    }

    public RecordCreationResponse create(@Valid NewTaskRequest req) {
        Task task = req.extractTask();
        task.setId(UUID.randomUUID());
        task.setCreatedDate(LocalDate.now());
        taskRepository.save(task);
        return new RecordCreationResponse(task.getId().toString());
    }

    public Set<TaskResponse> search(Map<String, String> params) {
        if (params.isEmpty())
            return new HashSet<>();

        Set<Task> results = entitySearcher.search(params, Task.class);
        if (results.isEmpty())
            throw new RecordNotFoundException();
        return results.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toSet());
    }

    public TaskResponse assignTaskToResource(String token, @Valid TaskAssignment assignment) {
        Task task = taskRepository.findById(assignment.getTaskId())
                .orElseThrow(RecordNotFoundException::new);
        Resource resource = resourceRepository.findById(assignment.getResourceId())
                .orElseThrow(RecordNotFoundException::new);
        String userId = tokenService.extractTokenDetails(token)
                .orElseThrow(() -> new InvalidJWTException("User ID could not be parsed from JWT"))
                .getAuthUserId();

        User user = userRepository.findById(UUID.fromString(userId))
                        .orElseThrow(RecordNotFoundException::new);
        task.setAssignee(resource);
        task.setAssigner(user);
        if (resource.getAssignedTasks() == null)
            resource.setAssignedTasks(new HashSet<>());
        resource.getAssignedTasks().add(task);
        resourceRepository.save(resource);
        taskRepository.save(task);
        return new TaskResponse(task);
    }

    public void delete(UUID taskId) {
        taskRepository.deleteById(taskId);
    }

    public TaskResponse update(UUID taskId, NewTaskRequest req) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(RecordNotFoundException::new);
        task.setName(req.getName());
        task.setDescription(req.getDescription());
        task.setStartDate(req.getStartDate());
        task.setEndDate(req.getEndDate());
        taskRepository.save(task);
        return new TaskResponse(task);
    }
}

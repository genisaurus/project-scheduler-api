package com.russell.scheduler.task;

import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.task.dtos.NewTaskRequest;
import com.russell.scheduler.task.dtos.TaskAssignment;
import com.russell.scheduler.task.dtos.TaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping(value="id/{id}", produces = "application/json")
    public TaskResponse getSingleTask(@PathVariable(name="id") UUID taskId) {
        return taskService.findOne(taskId);
    }

    @GetMapping(value = "/search", produces = "application/json")
    public Set<TaskResponse> search(@RequestParam Map<String, String> params) {
        return taskService.search(params);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public RecordCreationResponse createNewTask(@RequestBody NewTaskRequest req){
        return taskService.create(req);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value="id/{id}")
    public void deleteTask(@PathVariable(name = "id") UUID taskId) {
        taskService.delete(taskId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="id/{id}", produces = "application/json")
    public TaskResponse updateTask(@PathVariable(name = "id") UUID taskId, @RequestBody NewTaskRequest req) {
        return taskService.update(taskId, req);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="assign", produces = "application/json")
    public TaskResponse assignTask(@RequestHeader("Authorization") String token, @RequestBody TaskAssignment assignment) {
        return taskService.assignTaskToResource(token, assignment);
    }
}

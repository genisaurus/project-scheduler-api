package com.russell.scheduler.controllers;

import com.russell.scheduler.dtos.*;
import com.russell.scheduler.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(produces = "application/json")
    public Set<ProjectResponse> getAllProjects() {
        return projectService.findAll();
    }

    @GetMapping(value="id/{id}", produces = "application/json")
    public ProjectResponse getSingleResource(@PathVariable(name="id") UUID projectId) {
        return projectService.findOne(projectId);
    }

    @GetMapping(produces = "application/json")
    public Set<ProjectResponse> search(@RequestParam Map<String, String> params) {
        return projectService.search(params);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public RecordCreationResponse createNewResource(@RequestBody NewProjectRequest req){
        return projectService.create(req);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteProject(@RequestParam(name = "id") UUID projectId) {
        projectService.delete(projectId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value="id/{id}")
    public ProjectResponse updateProject(@PathVariable(name = "id") UUID projectId, @RequestBody NewProjectRequest req) {
        return projectService.update(projectId, req);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value="assign")
    public ProjectResponse assignProject(@RequestBody ProjectAssignment assignment) {
        return projectService.assignProjectToResource(assignment);
    }
}

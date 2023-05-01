package com.russell.scheduler.project;

import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.project.dtos.NewProjectRequest;
import com.russell.scheduler.project.dtos.ProjectAssignment;
import com.russell.scheduler.project.dtos.ProjectResponseDetailed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(produces = "application/json")
    public Set<ProjectResponseDetailed> getAllProjects() {
        return projectService.findAll();
    }

    @GetMapping(value="id/{id}", produces = "application/json")
    public ProjectResponseDetailed getSingleProject(@PathVariable(name="id") UUID projectId) {
        return projectService.findOne(projectId);
    }

    @GetMapping(value = "/search", produces = "application/json")
    public Set<ProjectResponseDetailed> search(@RequestParam Map<String, String> params) {
        return projectService.search(params);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public RecordCreationResponse createNewResource(@RequestBody NewProjectRequest req){
        return projectService.create(req);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value="id/{id}")
    public void deleteProject(@PathVariable(name="id") UUID projectId) {
        projectService.delete(projectId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="id/{id}")
    public ProjectResponseDetailed updateProject(@PathVariable(name = "id") UUID projectId, @RequestBody NewProjectRequest req) {
        return projectService.update(projectId, req);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="assign")
    public ProjectResponseDetailed assignProject(@RequestBody ProjectAssignment assignment) {
        return projectService.assignOwnerToProject(assignment);
    }
}

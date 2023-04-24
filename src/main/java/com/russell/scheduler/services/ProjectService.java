package com.russell.scheduler.services;

import com.russell.scheduler.common.EntitySearcher;
import com.russell.scheduler.dtos.*;
import com.russell.scheduler.entities.Project;
import com.russell.scheduler.entities.Resource;
import com.russell.scheduler.exceptions.RecordNotFoundException;
import com.russell.scheduler.repos.ProjectRepository;
import com.russell.scheduler.repos.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;
    private ResourceRepository resourceRepository;
    private EntitySearcher entitySearcher;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, ResourceRepository resourceRepository, EntitySearcher entitySearcher) {
        this.projectRepository = projectRepository;
        this.resourceRepository = resourceRepository;
        this.entitySearcher = entitySearcher;
    }

    public Set<ProjectResponse> findAll() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toSet());
    }

    public ProjectResponse findOne(UUID projectID) {
        return projectRepository.findById(projectID)
                .map(ProjectResponse::new)
                .orElseThrow(RecordNotFoundException::new);
    }

    public RecordCreationResponse create(@Valid NewProjectRequest req) {
        Project project = req.extractProject();
        project.setId(UUID.randomUUID());
        projectRepository.save(project);
        return new RecordCreationResponse(project.getId().toString());
    }

    public Set<ProjectResponse> search(Map<String, String> params) {
        if (params.isEmpty())
            return findAll();

        Set<Project> results = entitySearcher.search(params, Project.class);
        if (results.isEmpty())
            throw new RecordNotFoundException();
        return results.stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toSet());
    }

    public ProjectResponse assignProjectToResource(@Valid ProjectAssignment assignment) {
        Resource resource = resourceRepository.findById(assignment.getResourceId())
                .orElseThrow(RecordNotFoundException::new);
        Project project = projectRepository.findById(assignment.getResourceId())
                .orElseThrow(RecordNotFoundException::new);
        project.setOwner(resource);
        if (resource.getProjects() == null)
            resource.setProjects(new HashSet<>());
        resource.getProjects().add(project);
        resourceRepository.save(resource);
        projectRepository.save(project);
        return new ProjectResponse(project);
    }

    public void delete(UUID projectId) {
        projectRepository.deleteById(projectId);
    }

    public ProjectResponse update(UUID projectId, NewProjectRequest req) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(RecordNotFoundException::new);
        project.setName(req.getName());
        project.setStartDate(req.getStartDate());
        project.setEndDate(req.getEndDate());
        projectRepository.save(project);
        return new ProjectResponse(project);
    }
}

package com.russell.scheduler.project;

import com.russell.scheduler.common.EntitySearcher;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.project.dtos.ProjectResponseDetailed;
import com.russell.scheduler.resource.Resource;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.project.dtos.NewProjectRequest;
import com.russell.scheduler.project.dtos.ProjectAssignment;
import com.russell.scheduler.resource.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
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

    public Set<ProjectResponseDetailed> findAll() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectResponseDetailed::new)
                .collect(Collectors.toSet());
    }

    public ProjectResponseDetailed findOne(UUID projectID) {
        return projectRepository.findById(projectID)
                .map(ProjectResponseDetailed::new)
                .orElseThrow(RecordNotFoundException::new);
    }

    public RecordCreationResponse create(@Valid NewProjectRequest req) {
        Project project = req.extractProject();
        project.setId(UUID.randomUUID());
        projectRepository.save(project);
        return new RecordCreationResponse(project.getId().toString());
    }

    public Set<ProjectResponseDetailed> search(Map<String, String> params) {
        if (params.isEmpty())
            return findAll();

        Set<Project> results = entitySearcher.search(params, Project.class);
        if (results.isEmpty())
            throw new RecordNotFoundException();
        return results.stream()
                .map(ProjectResponseDetailed::new)
                .collect(Collectors.toSet());
    }

    public ProjectResponseDetailed assignOwnerToProject(@Valid ProjectAssignment assignment) {
        Resource resource = resourceRepository.findById(assignment.getResourceId())
                .orElseThrow(RecordNotFoundException::new);
        Project project = projectRepository.findById(assignment.getProjectId())
                .orElseThrow(RecordNotFoundException::new);
        project.setOwner(resource);
        if (resource.getProjects() == null)
            resource.setProjects(new HashSet<>());
        resource.getProjects().add(project);
        resourceRepository.save(resource);
        projectRepository.save(project);
        return new ProjectResponseDetailed(project);
    }

    public void delete(UUID projectId) {
        projectRepository.deleteById(projectId);
    }

    public ProjectResponseDetailed update(UUID projectId, NewProjectRequest req) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(RecordNotFoundException::new);
        project.setName(req.getName());
        project.setStartDate(req.getStartDate());
        project.setEndDate(req.getEndDate());
        projectRepository.save(project);
        return new ProjectResponseDetailed(project);
    }
}

package com.russell.scheduler.services;

import com.russell.scheduler.common.EntitySearcher;
import com.russell.scheduler.dtos.NewResourceRequest;
import com.russell.scheduler.dtos.RecordCreationResponse;
import com.russell.scheduler.dtos.ResourceResponse;
import com.russell.scheduler.entities.Resource;
import com.russell.scheduler.exceptions.RecordNotFoundException;
import com.russell.scheduler.exceptions.RecordPersistenceException;
import com.russell.scheduler.repos.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private ResourceRepository resourceRepository;
    private EntitySearcher entitySearcher;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository, EntitySearcher entitySearcher) {
        this.resourceRepository = resourceRepository;
        this.entitySearcher = entitySearcher;
    }

    public Set<ResourceResponse> fetchAllResources() {
        return resourceRepository.findAll()
                .stream()
                .map(ResourceResponse::new)
                .collect(Collectors.toSet());
    }

    public Set<ResourceResponse> search(Map<String, String> params) {
        if (params.isEmpty())
            return fetchAllResources();

        Set<Resource> results = entitySearcher.search(params, Resource.class);
        if (results.isEmpty())
            throw new RecordNotFoundException();
        return results.stream()
                .map(ResourceResponse::new)
                .collect(Collectors.toSet());
    }

    public RecordCreationResponse createResource(@Valid NewResourceRequest req) {
        Resource resource = req.extractResource();

        // check DB for existing users with provided username/email
        if (resourceRepository.existsByEmail(req.getEmail()))
            throw new RecordPersistenceException("That email address is already associated with another resource");

        resource.setId(UUID.randomUUID());
        resourceRepository.save(resource);
        return new RecordCreationResponse(resource.getId().toString());
    }
}

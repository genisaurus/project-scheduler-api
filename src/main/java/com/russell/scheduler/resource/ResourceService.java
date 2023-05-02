package com.russell.scheduler.resource;

import com.russell.scheduler.common.EntitySearcher;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.common.exceptions.RecordPersistenceException;
import com.russell.scheduler.resource.dtos.NewResourceRequest;
import com.russell.scheduler.resource.dtos.ResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResourceService {

    private ResourceRepository resourceRepository;
    private EntitySearcher entitySearcher;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository, EntitySearcher entitySearcher) {
        this.resourceRepository = resourceRepository;
        this.entitySearcher = entitySearcher;
    }

    public Set<ResourceResponse> findAll() {
        return resourceRepository.findAll()
                .stream()
                .map(ResourceResponse::new)
                .collect(Collectors.toSet());
    }

    public ResourceResponse findOne(UUID resourceId) {
        return resourceRepository.findById(resourceId)
                .map(ResourceResponse::new)
                .orElseThrow(RecordNotFoundException::new);
    }

    public Set<ResourceResponse> search(Map<String, String> params) {
        if (params.isEmpty())
            return findAll();

        Set<Resource> results = entitySearcher.search(params, Resource.class);
        if (results.isEmpty())
            throw new RecordNotFoundException();
        return results.stream()
                .map(ResourceResponse::new)
                .collect(Collectors.toSet());
    }

    public RecordCreationResponse create(@Valid NewResourceRequest req) {
        Resource resource = req.extractResource();

        // check DB for existing users with provided username/email
        if (resourceRepository.existsByEmail(req.getEmail()))
            throw new RecordPersistenceException("That email address is already associated with another resource");

        resource.setId(UUID.randomUUID());
        resourceRepository.save(resource);
        return new RecordCreationResponse(resource.getId().toString());
    }

    public void delete(UUID resourceId) {
        resourceRepository.deleteById(resourceId);
    }

    public ResourceResponse update(UUID resourceId, NewResourceRequest req) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(RecordNotFoundException::new);
        resource.setEmail(req.getEmail());
        resource.setFirstName(req.getFirstName());
        resource.setLastName(req.getLastName());
        resourceRepository.save(resource);
        return new ResourceResponse(resource);
    }
}

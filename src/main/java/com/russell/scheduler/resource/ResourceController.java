package com.russell.scheduler.resource;

import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.resource.dtos.NewResourceRequest;
import com.russell.scheduler.resource.dtos.ResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping(produces = "application/json")
    public Set<ResourceResponse> getAllResources() {
        return resourceService.findAll();
    }

    @GetMapping(value="/id/{id}", produces = "application/json")
    public ResourceResponse getSingleResource(@PathVariable(name="id") UUID resourceId) {
        return resourceService.findOne(resourceId);
    }

    @GetMapping(value = "/search", produces = "application/json")
    public Set<ResourceResponse> search(@RequestParam Map<String, String> params) {
        return resourceService.search(params);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public RecordCreationResponse createNewResource(@RequestBody NewResourceRequest req){
        return resourceService.create(req);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value="/id/{id}")
    public void deleteResource(@PathVariable(name="id") UUID resourceId) {
        resourceService.delete(resourceId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="/id/{id}")
    public ResourceResponse updateResource(@PathVariable(name = "id") UUID resourceId, @RequestBody NewResourceRequest req) {
        return resourceService.update(resourceId, req);
    }
}

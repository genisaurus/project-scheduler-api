package com.russell.scheduler.controllers;

import com.russell.scheduler.dtos.*;
import com.russell.scheduler.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
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

    @GetMapping(value="id/{id}", produces = "application/json")
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
    @DeleteMapping
    public void deleteResource(@RequestParam(name = "id") UUID resourceId) {
        resourceService.delete(resourceId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value="id/{id}")
    public ResourceResponse updateResource(@PathVariable(name = "id") UUID resourceId, @RequestBody NewResourceRequest req) {
        return resourceService.update(resourceId, req);
    }
}

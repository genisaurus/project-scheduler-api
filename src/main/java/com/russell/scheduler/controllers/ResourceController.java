package com.russell.scheduler.controllers;

import com.russell.scheduler.dtos.*;
import com.russell.scheduler.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

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
        return resourceService.fetchAllResources();
    }

    @GetMapping(value = "/search", produces = "application/json")
    public Set<ResourceResponse> search(@RequestParam Map<String, String> params) {
        return resourceService.search(params);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public RecordCreationResponse createNewResource(@RequestBody NewResourceRequest req){
        return resourceService.createResource(req);
    }
}

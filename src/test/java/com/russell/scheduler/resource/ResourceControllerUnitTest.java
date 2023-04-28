package com.russell.scheduler.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.project.Project;
import com.russell.scheduler.resource.dtos.NewResourceRequest;
import com.russell.scheduler.resource.dtos.ResourceResponseDetailed;
import com.russell.scheduler.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ResourceController.class)
public class ResourceControllerUnitTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private ResourceService mockResourceService;
    private final String PATH = "/resources";
    private final String CONTENT_TYPE = "application/json";

    @Test
    void test_getAll_returnsSetOfResourceResponses() throws Exception {
        Set<ResourceResponseDetailed> mockResources = new HashSet<>();
        mockResources.add(
                new ResourceResponseDetailed(
                        new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                                "mock@resource.one", "first1", "last1",
                                new HashSet<Project>(), new HashSet<Task>())));
        mockResources.add(
                new ResourceResponseDetailed(
                        new Resource(UUID.fromString("aa4a20ab-cc98-4f9a-a09d-37b6fbd8087c"),
                                "mock@resource.two", "first2", "last2",
                                new HashSet<Project>(), new HashSet<Task>())));

        when(mockResourceService.findAll()).thenReturn(mockResources);

        MvcResult result = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();
    }

    @Test
    void test_getOneResource_returnsResourceResponse_providedValidUUID() throws Exception {
        ResourceResponseDetailed mockResource = new ResourceResponseDetailed(
                new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mock@resource.one", "first1", "last1",
                new HashSet<Project>(), new HashSet<Task>()));

        when(mockResourceService.findOne(mockResource.getId())).thenReturn(mockResource);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+mockResource.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.email").value(mockResource.getEmail()))
                .andExpect(jsonPath("$.firstName").value(mockResource.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(mockResource.getLastName()))
                .andReturn();
    }

    @Test
    void test_getOneResource_throwsRecordNotFoundException_providedInvalidUUID() throws Exception {
        ResourceResponseDetailed mockResource = new ResourceResponseDetailed(
                new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                        "mock@resource.one", "first1", "last1",
                        new HashSet<Project>(), new HashSet<Task>()));

        when(mockResourceService.findOne(mockResource.getId())).thenThrow(RecordNotFoundException.class);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+mockResource.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andReturn();
    }

    @Test
    void test_search_returnsSetOfResourceResponses_providedValidParam() throws Exception {
        ResourceResponseDetailed mockResource = new ResourceResponseDetailed(
                new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                        "mock@resource.one", "first1", "last1",
                        new HashSet<Project>(), new HashSet<Task>()));
        Map<String, String> params = new HashMap<>();
        params.put("firstName", mockResource.getFirstName());

        when(mockResourceService.search(params)).thenReturn(Set.of(mockResource));

        MvcResult result = mockMvc.perform(get(PATH+"/search")
                        .param("firstName", mockResource.getFirstName()))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    void test_search_throwsRecordNotFoundException_givenBadParams() throws Exception {
        when(mockResourceService.search(Map.of("firstName", "DoesNotExist"))).thenThrow(RecordNotFoundException.class);

        MvcResult result = mockMvc.perform(get(PATH+"/search")
                        .param("firstName", "DoesNotExist"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andReturn();
    }

    @Test
    void test_create_returnsRecordCreationResponse_givenNewResourceRequest() throws Exception {
        NewResourceRequest req = new NewResourceRequest("mock@resource.one", "first1", "last1");
        RecordCreationResponse resp = new RecordCreationResponse();
        resp.setId("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b");
        ObjectMapper json = new ObjectMapper();

        when(mockResourceService.create(req)).thenReturn(resp);

        MvcResult result = mockMvc.perform(post(PATH)
                        .contentType(CONTENT_TYPE)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.id").value(resp.getId()))
                .andReturn();
    }

    @Test
    void test_update_returnsResourceResponse_givenNewResourceRequest() throws Exception {
        Resource mockResource = new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mock@resource.one", "first1", "last1", new HashSet<Project>(), new HashSet<Task>());
        NewResourceRequest req = new NewResourceRequest("mock@resource.two", "first2", "last2");
        ResourceResponseDetailed response = new ResourceResponseDetailed(
                new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mock@resource.two", "first2", "last2", new HashSet<Project>(), new HashSet<Task>()));


        ObjectMapper json = new ObjectMapper();

        when(mockResourceService.update(mockResource.getId(), req)).thenReturn(response);

        MvcResult result = mockMvc.perform(patch(PATH+"/id/"+mockResource.getId())
                        .contentType(CONTENT_TYPE)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.email").value(req.getEmail()))
                .andExpect(jsonPath("$.firstName").value(req.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(req.getLastName()))
                .andReturn();
    }
}

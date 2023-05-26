package com.russell.scheduler.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.project.Project;
import com.russell.scheduler.resource.dtos.NewResourceRequest;
import com.russell.scheduler.resource.dtos.ResourceResponse;
import com.russell.scheduler.task.Task;
import org.junit.jupiter.api.BeforeEach;
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
class ResourceControllerUnitTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private ResourceService mockResourceService;
    private final String PATH = "/resources";
    private final String CONTENT_TYPE = "application/json";
    private Resource mockResource1;
    private Resource mockResource2;

    @BeforeEach
    public void config() {
        mockResource1 = new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mock@resource.one", "first1", "last1",
                new HashSet<Project>(), new HashSet<Task>());
        mockResource2 = new Resource(UUID.fromString("aa4a20ab-cc98-4f9a-a09d-37b6fbd8087c"),
                "mock@resource.two", "first2", "last2",
                new HashSet<Project>(), new HashSet<Task>());
    }

    @Test
    void test_getAll_returnsSetOfResourceResponses() throws Exception {
        Set<ResourceResponse> mockResourceResp = Set.of(
                new ResourceResponse(mockResource1),
                new ResourceResponse(mockResource2));

        when(mockResourceService.findAll()).thenReturn(mockResourceResp);

        MvcResult result = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();
    }

    @Test
    void test_getOneResource_returnsResourceResponse_providedValidUUID() throws Exception {
        ResourceResponse mockResourceResp = new ResourceResponse(mockResource1);

        when(mockResourceService.findOne(mockResourceResp.getId())).thenReturn(mockResourceResp);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+mockResourceResp.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.email").value(mockResourceResp.getEmail()))
                .andExpect(jsonPath("$.firstName").value(mockResourceResp.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(mockResourceResp.getLastName()))
                .andReturn();
    }

    @Test
    void test_getOneResource_throwsRecordNotFoundException_providedInvalidUUID() throws Exception {
        ResourceResponse mockResourceResp = new ResourceResponse(mockResource1);

        when(mockResourceService.findOne(mockResourceResp.getId())).thenThrow(RecordNotFoundException.class);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+mockResourceResp.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andReturn();
    }

    @Test
    void test_search_returnsSetOfResourceResponses_providedValidParam() throws Exception {
        ResourceResponse mockResourceResp = new ResourceResponse(mockResource1);
        Map<String, String> params = new HashMap<>();
        params.put("firstName", mockResourceResp.getFirstName());

        when(mockResourceService.search(params)).thenReturn(Set.of(mockResourceResp));

        MvcResult result = mockMvc.perform(get(PATH+"/search")
                        .param("firstName", mockResourceResp.getFirstName()))
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
        NewResourceRequest req = new NewResourceRequest("mock@resource.two", "first2", "last2");
        ResourceResponse response = new ResourceResponse(
                new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mock@resource.two", "first2", "last2", new HashSet<Project>(), new HashSet<Task>()));


        ObjectMapper json = new ObjectMapper();

        when(mockResourceService.update(mockResource1.getId(), req)).thenReturn(response);

        MvcResult result = mockMvc.perform(patch(PATH+"/id/"+mockResource1.getId())
                        .contentType(CONTENT_TYPE)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.email").value(req.getEmail()))
                .andExpect(jsonPath("$.firstName").value(req.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(req.getLastName()))
                .andReturn();
    }

    @Test
    void test_delete_returnsStatusOk_givenUUID() throws Exception {
        MvcResult result = mockMvc.perform(delete(PATH+"/id/"+ UUID.randomUUID())
                        .contentType(CONTENT_TYPE))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}

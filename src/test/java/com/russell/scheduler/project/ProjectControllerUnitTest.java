package com.russell.scheduler.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.project.dtos.NewProjectRequest;
import com.russell.scheduler.project.dtos.ProjectAssignment;
import com.russell.scheduler.project.dtos.ProjectResponse;
import com.russell.scheduler.resource.Resource;
import com.russell.scheduler.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProjectController.class)
class ProjectControllerUnitTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper json;
    @MockBean
    private ProjectService mockProjectService;
    private final String PATH = "/projects";
    private final String CONTENT_TYPE = "application/json";
    private Project mockProject1;
    private Project mockProject2;

    @BeforeEach
    public void config() {
        mockProject1 = new Project(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mockProject1", LocalDate.now(), LocalDate.now(), new Resource(), new HashSet<Task>());
        mockProject2 = new Project(UUID.fromString("aa4a20ab-cc98-4f9a-a09d-37b6fbd8087c"),
                "mockProject2", LocalDate.now(), LocalDate.now(), new Resource(), new HashSet<Task>());
    }

    @Test
    void test_getAll_returnsSetOfProjectResponses() throws Exception {
        Set<ProjectResponse> mockProjectResp = Set.of(
                new ProjectResponse(mockProject1),
                new ProjectResponse(mockProject2));

        when(mockProjectService.findAll()).thenReturn(mockProjectResp);

        MvcResult result = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();
    }

    @Test
    void test_getOneResource_returnsProjectResponse_providedValidUUID() throws Exception {
        ProjectResponse mockProjectResp = new ProjectResponse(mockProject1);

        when(mockProjectService.findOne(mockProjectResp.getId())).thenReturn(mockProjectResp);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+mockProjectResp.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.name").value(mockProjectResp.getName()))
                .andExpect(jsonPath("$.startDate").value(mockProjectResp.getStartDate().toString()))
                .andExpect(jsonPath("$.endDate").value(mockProjectResp.getEndDate().toString()))
                .andReturn();
    }

    @Test
    void test_getOneResource_throwsRecordNotFoundException_providedInvalidUUID() throws Exception {
        ProjectResponse mockProjectResp = new ProjectResponse(mockProject1);

        when(mockProjectService.findOne(mockProjectResp.getId())).thenThrow(RecordNotFoundException.class);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+ mockProjectResp.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andReturn();
    }

    @Test
    void test_search_returnsSetOfProjectResponses_providedValidParam() throws Exception {
        ProjectResponse mockProjectResp = new ProjectResponse(mockProject1);
        Map<String, String> params = new HashMap<>();
        params.put("name", mockProjectResp.getName());

        when(mockProjectService.search(params)).thenReturn(Set.of(mockProjectResp));

        MvcResult result = mockMvc.perform(get(PATH+"/search")
                        .param("name", mockProjectResp.getName()))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    void test_search_throwsRecordNotFoundException_givenBadParams() throws Exception {
        when(mockProjectService.search(Map.of("name", "DoesNotExist"))).thenThrow(RecordNotFoundException.class);

        MvcResult result = mockMvc.perform(get(PATH+"/search")
                        .param("name", "DoesNotExist"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andReturn();
    }

    @Test
    void test_create_returnsRecordCreationResponse_givenNewProjectRequest() throws Exception {
        NewProjectRequest req = new NewProjectRequest("mockProject", LocalDate.now(), LocalDate.now());
        RecordCreationResponse resp = new RecordCreationResponse();
        resp.setId("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b");

        when(mockProjectService.create(req)).thenReturn(resp);

        MvcResult result = mockMvc.perform(post(PATH)
                        .contentType(CONTENT_TYPE)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.id").value(resp.getId()))
                .andReturn();
    }

    @Test
    void test_update_returnsProjectResponse_givenNewProjectRequest() throws Exception {
        NewProjectRequest req = new NewProjectRequest("updatedMockProject", LocalDate.now(), LocalDate.now());
        ProjectResponse response = new ProjectResponse(
                new Project(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                        "updatedMockProject", LocalDate.now(), LocalDate.now(), new Resource(), new HashSet<Task>()));


        when(mockProjectService.update(mockProject1.getId(), req)).thenReturn(response);

        MvcResult result = mockMvc.perform(patch(PATH+"/id/"+ mockProject1.getId())
                        .contentType(CONTENT_TYPE)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.name").value(req.getName()))
                .andExpect(jsonPath("$.startDate").value(req.getStartDate().toString()))
                .andExpect(jsonPath("$.endDate").value(req.getEndDate().toString()))
                .andReturn();
    }

    @Test
    void test_delete_returnsStatusOk_givenUUID() throws Exception {
        MvcResult result = mockMvc.perform(delete(PATH+"/id/"+ UUID.randomUUID())
                        .contentType(CONTENT_TYPE))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    void test_assign_returnsProjectResponse_givenAssignRequest() throws Exception {
        Resource mockResource1 = new Resource(UUID.fromString("a27951ae-22fc-491d-b0c3-48bf5d8a4590"),
                "mock@resource.one", "first1", "last1",
                new HashSet<Project>(), new HashSet<Task>());
        mockProject1.setOwner(mockResource1);
        ProjectResponse response = new ProjectResponse(mockProject1);

        ProjectAssignment assign = new ProjectAssignment(mockProject1.getId(), mockResource1.getId());

        when(mockProjectService.assignOwnerToProject(assign)).thenReturn(response);

        MvcResult result = mockMvc.perform(patch(PATH+"/assign")
                        .contentType(CONTENT_TYPE)
                        .content(json.writeValueAsString(assign)))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.ownerId").value(mockProject1.getOwner().getId().toString()))
                .andReturn();
    }
}

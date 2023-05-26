package com.russell.scheduler.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.project.Project;
import com.russell.scheduler.resource.Resource;
import com.russell.scheduler.task.dtos.NewTaskRequest;
import com.russell.scheduler.task.dtos.TaskAssignment;
import com.russell.scheduler.task.dtos.TaskResponse;
import com.russell.scheduler.user.User;
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

@WebMvcTest(TaskController.class)
class TaskControllerUnitTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper json;
    @MockBean
    private TaskService mockTaskService;
    private final String PATH = "/tasks";
    private final String CONTENT_TYPE = "application/json";
    private Task mockTask;
    private Task mockTask2;
    private Project mockProject;
    private Resource mockResource;
    private User mockUser;

    @BeforeEach
    public void config() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockResource = new Resource(UUID.randomUUID(), "mock@resource.one", "first1", "last1",
                new HashSet<Project>(), new HashSet<Task>());
        mockProject = new Project(UUID.randomUUID(), "mockProject", LocalDate.now(), LocalDate.now(),
                mockResource, new HashSet<Task>());
        mockTask = new Task(UUID.randomUUID(), "mockTask1", "a test task", mockResource, mockUser,
                LocalDate.now(), LocalDate.now().plusDays(1), mockProject, LocalDate.now());
    }

    @Test
    void test_getOneTask_returnsTaskResponse_providedValidUUID() throws Exception {
        TaskResponse mockTaskResp = new TaskResponse(mockTask);

        when(mockTaskService.findOne(mockTaskResp.getId())).thenReturn(mockTaskResp);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+mockTaskResp.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.name").value(mockTaskResp.getName()))
                .andExpect(jsonPath("$.description").value(mockTaskResp.getDescription()))
                .andExpect(jsonPath("$.projectId").value(mockTaskResp.getProjectId().toString()))
                .andExpect(jsonPath("$.assigneeId").value(mockTaskResp.getAssigneeId().toString()))
                .andExpect(jsonPath("$.assignerId").value(mockTaskResp.getAssignerId().toString()))
                .andExpect(jsonPath("$.startDate").value(mockTaskResp.getStartDate().toString()))
                .andExpect(jsonPath("$.endDate").value(mockTaskResp.getEndDate().toString()))
                .andExpect(jsonPath("$.createdDate").value(mockTaskResp.getCreatedDate().toString()))
                .andReturn();
    }

    @Test
    void test_getOneResource_throwsRecordNotFoundException_providedInvalidUUID() throws Exception {
        TaskResponse mockTaskResp = new TaskResponse(mockTask);

        when(mockTaskService.findOne(mockTaskResp.getId())).thenThrow(RecordNotFoundException.class);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+ mockTaskResp.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andReturn();
    }

    @Test
    void test_search_returnsSetOfTaskResponses_providedValidParam() throws Exception {
        TaskResponse mockTaskResp = new TaskResponse(mockTask);
        Map<String, String> params = new HashMap<>();
        params.put("name", mockTaskResp.getName());

        when(mockTaskService.search(params)).thenReturn(Set.of(mockTaskResp));

        MvcResult result = mockMvc.perform(get(PATH+"/search")
                        .param("name", mockTaskResp.getName()))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    void test_search_throwsRecordNotFoundException_givenBadParams() throws Exception {
        when(mockTaskService.search(Map.of("name", "DoesNotExist"))).thenThrow(RecordNotFoundException.class);

        MvcResult result = mockMvc.perform(get(PATH+"/search")
                        .param("name", "DoesNotExist"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andReturn();
    }

    @Test
    void test_create_returnsRecordCreationResponse_givenNewTaskRequest() throws Exception {
        NewTaskRequest req = new NewTaskRequest("test", "test", LocalDate.now(), LocalDate.now(), mockProject.getId());
        RecordCreationResponse resp = new RecordCreationResponse();
        resp.setId("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b");

        when(mockTaskService.create(req)).thenReturn(resp);

        MvcResult result = mockMvc.perform(post(PATH)
                        .contentType(CONTENT_TYPE)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.id").value(resp.getId()))
                .andReturn();
    }

    @Test
    void test_update_returnsTaskResponse_givenNewTaskRequest() throws Exception {
        NewTaskRequest req = new NewTaskRequest("test", "updated!", LocalDate.now(), LocalDate.now(), mockProject.getId());
        TaskResponse response = new TaskResponse(
                new Task(mockTask.getId(), req.getName(), req.getDescription(), mockTask.getAssignee(),
                        mockTask.getAssigner(), req.getStartDate(), req.getEndDate(), mockProject, mockTask.getCreatedDate()));


        when(mockTaskService.update(mockTask.getId(), req)).thenReturn(response);

        MvcResult result = mockMvc.perform(patch(PATH+"/id/"+ mockTask.getId())
                        .contentType(CONTENT_TYPE)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.name").value(req.getName()))
                .andExpect(jsonPath("$.description").value(req.getDescription()))
                .andExpect(jsonPath("$.startDate").value(req.getStartDate().toString()))
                .andExpect(jsonPath("$.endDate").value(req.getEndDate().toString()))
                .andReturn();
    }

    @Test
    void test_delete_returnsStatusNoContent_givenUUID() throws Exception {
        MvcResult result = mockMvc.perform(delete(PATH+"/id/"+ UUID.randomUUID())
                        .contentType(CONTENT_TYPE))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    void test_assign_returnsTaskResponse_givenAssignRequest() throws Exception {
        mockTask.setAssigner(mockUser);

        mockTask.setAssignee(mockResource);
        TaskResponse response = new TaskResponse(mockTask);

        TaskAssignment assign = new TaskAssignment(mockTask.getId(), mockResource.getId());

        when(mockTaskService.assignTaskToResource("", assign)).thenReturn(response);

        MvcResult result = mockMvc.perform(patch(PATH+"/assign")
                        .contentType(CONTENT_TYPE)
                        .header("Authorization", "")
                        .content(json.writeValueAsString(assign)))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.assigneeId").value(mockTask.getAssignee().getId().toString()))
                .andReturn();
    }
}

package com.russell.scheduler.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.user.dtos.NewUserRequest;
import com.russell.scheduler.user.dtos.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerUnitTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserService mockUserService;
    private final String PATH = "/users";
    private final String CONTENT_TYPE = "application/json";
    private User mockUser1;
    private User mockUser2;
    private UserRole mockRole;

    @BeforeEach
    public void config() {
        mockRole = new UserRole(1, "ADMIN", 1);
        mockUser1 = new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mockuser1", "mock@user.one", "first1", "last1",
                "P@ssword1", mockRole);
        mockUser2 = new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087c"),
                "mockuser2", "mock@user.two", "first2", "last2",
                "P@ssword2", mockRole);
    }

    @Test
    void test_getAll_returnsSetOfUserResponses() throws Exception {
        Set<UserResponse> mockUsers = Set.of(new UserResponse(mockUser1), new UserResponse(mockUser2));

        when(mockUserService.findAll()).thenReturn(mockUsers);

        MvcResult result = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();
    }

    @Test
    void test_getOneUser_returnsUserResponse_providedValidUUID() throws Exception {
        UserResponse mockUserResp = new UserResponse(mockUser1);

        when(mockUserService.findOne(mockUserResp.getId())).thenReturn(mockUserResp);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+mockUserResp.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.username").value(mockUserResp.getUsername()))
                .andExpect(jsonPath("$.email").value(mockUserResp.getEmail()))
                .andExpect(jsonPath("$.firstName").value(mockUserResp.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(mockUserResp.getLastName()))
                .andExpect(jsonPath("$.roleName").value(mockUserResp.getRoleName()))
                .andReturn();
    }

    @Test
    void test_getOneUser_throwsRecordNotFoundException_providedInvalidUUID() throws Exception {
        UserResponse mockUserResp = new UserResponse(mockUser1);

        when(mockUserService.findOne(mockUserResp.getId())).thenThrow(RecordNotFoundException.class);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+mockUserResp.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andReturn();
    }

    @Test
    void test_search_returnsSetOfUserResponses_providedValidParam() throws Exception {
        UserResponse mockUserResp = new UserResponse(mockUser1);
        Map<String, String> params = new HashMap<>();
        params.put("firstName", mockUserResp.getFirstName());

        when(mockUserService.search(params)).thenReturn(Set.of(mockUserResp));

        MvcResult result = mockMvc.perform(get(PATH+"/search")
                        .param("firstName", mockUserResp.getFirstName()))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    void test_search_throwsRecordNotFoundException_givenBadParam() throws Exception {
        when(mockUserService.search(Map.of("firstName", "DoesNotExist"))).thenThrow(RecordNotFoundException.class);

        MvcResult result = mockMvc.perform(get(PATH+"/search")
                    .param("firstName", "DoesNotExist"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andReturn();
    }

    @Test
    void test_create_returnsRecordCreationResponse_givenNewUserRequest() throws Exception {
        NewUserRequest req = new NewUserRequest(mockUser1.getUsername(), mockUser1.getPassword(), mockUser1.getEmail(),
                mockUser1.getFirstName(), mockUser1.getLastName(), mockUser1.getRole().getRoleName());
        RecordCreationResponse resp = new RecordCreationResponse();
        resp.setId("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b");
        ObjectMapper json = new ObjectMapper();

        when(mockUserService.create(req)).thenReturn(resp);

        MvcResult result = mockMvc.perform(post(PATH)
                        .contentType(CONTENT_TYPE)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.id").value(resp.getId()))
                .andReturn();
    }
}

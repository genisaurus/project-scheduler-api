package com.russell.scheduler.user;

import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.user.dtos.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    @Test
    void test_getAll_returnSetOfUserResponses() throws Exception {
        Set<UserResponse> mockUsers = new HashSet<>();
        mockUsers.add(
                new UserResponse(
                        new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                                "mockuser1", "mock@user.one", "first1", "last1",
                                "P@ssword1", new UserRole(1, "ADMIN", 1))));
        mockUsers.add(
                new UserResponse(
                        new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087c"),
                                "mockuser2", "mock@user.two", "first2", "last2",
                                "P@ssword2", new UserRole(2, "TEST", 2))));

        when(mockUserService.findAll()).thenReturn(mockUsers);

        MvcResult result = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();
    }

    @Test
    void test_getOneUser_returnUserResponse_providedValidUUID() throws Exception {
        UserResponse mockUser = new UserResponse(new User(
                UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mockuser1", "mock@user.one", "first1", "last1",
                "P@ssword1", new UserRole(1, "ADMIN", 1)));

        when(mockUserService.findOne(mockUser.getId())).thenReturn(mockUser);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+mockUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.username").value("mockuser1"))
                .andExpect(jsonPath("$.email").value("mock@user.one"))
                .andExpect(jsonPath("$.firstName").value("first1"))
                .andExpect(jsonPath("$.lastName").value("last1"))
                .andExpect(jsonPath("$.role").isMap())
                .andReturn();
    }

    @Test
    void test_getOneUser_throwsRecordNotFoundException_providedInvalidUUID() throws Exception {
        UserResponse mockUser = new UserResponse(new User(
                UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mockuser1", "mock@user.one", "first1", "last1",
                "P@ssword1", new UserRole(1, "ADMIN", 1)));

        when(mockUserService.findOne(mockUser.getId())).thenThrow(RecordNotFoundException.class);

        MvcResult result = mockMvc.perform(get(PATH+"/id/"+mockUser.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(header().string("content-type", CONTENT_TYPE))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andReturn();
    }
}

package com.russell.scheduler.task;

import com.russell.scheduler.auth.TokenService;
import com.russell.scheduler.auth.dtos.Principal;
import com.russell.scheduler.common.EntitySearcher;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.project.Project;
import com.russell.scheduler.project.ProjectRepository;
import com.russell.scheduler.project.dtos.NewProjectRequest;
import com.russell.scheduler.project.dtos.ProjectAssignment;
import com.russell.scheduler.resource.Resource;
import com.russell.scheduler.resource.ResourceRepository;
import com.russell.scheduler.task.dtos.NewTaskRequest;
import com.russell.scheduler.task.dtos.TaskAssignment;
import com.russell.scheduler.task.dtos.TaskResponse;
import com.russell.scheduler.user.User;
import com.russell.scheduler.user.UserRepository;
import com.russell.scheduler.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class TaskServiceUnitTest {

    private TaskService service;
    private final TaskRepository mockTaskRepo = mock(TaskRepository.class);
    private final ProjectRepository mockProjectRepo = mock(ProjectRepository.class);
    private final ResourceRepository mockResourceRepo = mock(ResourceRepository.class);
    private final UserRepository mockUserRepo = mock(UserRepository.class);
    private final EntitySearcher mockEntitySearcher = mock(EntitySearcher.class);
    private final TokenService mockTokenService = mock(TokenService.class);
    private Task mockTask;
    private Project mockProject;
    private Resource mockResource;
    private User mockUser;

    @BeforeEach
    public void setup() {
        reset(mockTaskRepo, mockProjectRepo, mockResourceRepo, mockUserRepo, mockEntitySearcher, mockTokenService);
        service = new TaskService(mockTaskRepo, mockProjectRepo, mockResourceRepo, mockUserRepo, mockEntitySearcher, mockTokenService);
        mockProject = new Project(UUID.fromString("aa4a20ab-cc98-4f9a-a09d-37b6fbd8087c"),
                "mockProject", LocalDate.now(), LocalDate.now(), new Resource(), new HashSet<Task>());
        mockResource = new Resource(UUID.fromString("a27951ae-22fc-491d-b0c3-48bf5d8a4590"),
                "mock@resource.one", "first1", "last1",
                new HashSet<Project>(), new HashSet<Task>());
        mockUser = new User(UUID.fromString("2fdfea91-896f-4624-91c5-b1c7d88bfe37"),
                "test", "mock@user.one", "first1", "last1",
                "P@ssword1", new UserRole(1, "ADMIN", 1));
        mockTask = new Task(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mockTask", "a mock task", mockResource, mockUser,
                LocalDate.now(), LocalDate.now().plusDays(1), mockProject, LocalDate.now());
    }

    @Test
    void test_findOne_returnTaskResponse_providedTaskId() {
        when(mockTaskRepo.findById(mockTask.getId())).thenReturn(Optional.of(mockTask));

        TaskResponse response = service.findOne(mockTask.getId());

        // assert all fields of the generated response match the test resource, and that the repo was only
        // queried once
        assertAll(
                () -> assertEquals(mockTask.getId(), response.getId()),
                () -> assertEquals(mockTask.getName(), response.getName()),
                () -> assertEquals(mockTask.getDescription(), response.getDescription()),
                () -> assertEquals(mockTask.getProject().getId(), response.getProjectId()),
                () -> assertEquals(mockTask.getAssignee().getId(), response.getAssigneeId()),
                () -> assertEquals(mockTask.getAssigner().getId(), response.getAssignerId()),
                () -> assertEquals(mockTask.getStartDate(), response.getStartDate()),
                () -> assertEquals(mockTask.getEndDate(), response.getEndDate()),
                () -> assertEquals(mockTask.getCreatedDate(), response.getCreatedDate()));
        verify(mockTaskRepo, times(1)).findById(mockTask.getId());
    }

    @Test
    void test_findOne_throwsRecordNotFoundException_providedBadTaskId() {
        UUID badTaskId = UUID.randomUUID();

        when(mockTaskRepo.findById(badTaskId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.findOne(badTaskId));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockTaskRepo, times(1)).findById(badTaskId);
    }

    @Test
    void test_search_returnsSetOfTaskResponses_providedValidParam() {

        Set<Task> mockTasks = Set.of(mockTask);

        Map<String, String> criteria = new HashMap<>();
        criteria.put("name", mockTask.getName());
        when(mockEntitySearcher.search(criteria, Task.class)).thenReturn(mockTasks);

        Set<TaskResponse> response = service.search(criteria);
        // assert the proper number of users was returned
        TaskResponse content = response.stream().findFirst().get();
        assertAll(
                () -> assertEquals(mockTasks.size(), response.size()),
                () -> assertEquals(mockTask.getId(), content.getId()),
                () -> assertEquals(mockTask.getName(), content.getName()),
                () -> assertEquals(mockTask.getDescription(), content.getDescription()),
                () -> assertEquals(mockTask.getProject().getId(), content.getProjectId()),
                () -> assertEquals(mockTask.getAssignee().getId(), content.getAssigneeId()),
                () -> assertEquals(mockTask.getAssigner().getId(), content.getAssignerId()),
                () -> assertEquals(mockTask.getStartDate(), content.getStartDate()),
                () -> assertEquals(mockTask.getEndDate(), content.getEndDate()),
                () -> assertEquals(mockTask.getCreatedDate(), content.getCreatedDate()));
        verify(mockEntitySearcher, times(1)).search(criteria, Task.class);
    }

    @Test
    void test_search_throwsRecordNotFoundException_providedBadParam() {
        Map<String, String> criteria = new HashMap<>();
        criteria.put("name", "Does Not Exist");
        when(mockEntitySearcher.search(criteria, Task.class)).thenReturn(new HashSet<>());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.search(criteria));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockEntitySearcher, times(1)).search(criteria, Task.class);
    }


    @Test
    void test_create_returnsResourceCreationResponse_providedValidTaskInfo() {
        NewTaskRequest request = new NewTaskRequest();
        request.setName("test");
        request.setDescription("a test task");
        request.setProjectId(mockProject.getId());
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(1));

        when(mockProjectRepo.findById(mockProject.getId()))
                .thenReturn(Optional.of(mockProject));
        when(mockTaskRepo.save(any(Task.class))).thenReturn(any(Task.class));

        RecordCreationResponse response = service.create(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        verify(mockProjectRepo, times(1)).findById(request.getProjectId());
        verify(mockTaskRepo, times(1)).save(any());
    }

    @Test
    void test_create_throwsRecordNotFoundException_providedBadProjectId() {
        NewTaskRequest request = new NewTaskRequest();
        request.setName("test");
        request.setDescription("a test task");
        request.setProjectId(mockProject.getId());
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(1));

        when(mockProjectRepo.findById(mockProject.getId()))
                .thenReturn(Optional.empty());
        when(mockTaskRepo.save(any(Task.class))).thenReturn(any(Task.class));

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.create(request));

        // Assert
        verify(mockProjectRepo, times(1)).findById(request.getProjectId());
        verify(mockTaskRepo, times(0)).save(any());
    }

    @Test
    void test_update_returnsTaskResponse_providedValidTaskInfo() {
        NewTaskRequest request = new NewTaskRequest();
        request.setName("test");
        request.setDescription("a test task");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(1));


        when(mockTaskRepo.findById(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b")))
                .thenReturn(Optional.of(mockTask));
        when(mockTaskRepo.save(any(Task.class))).thenReturn(any(Task.class));

        TaskResponse response = service.update(mockTask.getId(), request);

        // Assert
        assertAll(
                () -> assertEquals(mockTask.getId(), response.getId()),
                () -> assertEquals(request.getName(), response.getName()),
                () -> assertEquals(request.getDescription(), response.getDescription()),
                () -> assertEquals(request.getStartDate(), response.getStartDate()),
                () -> assertEquals(request.getEndDate(), response.getEndDate()));
        verify(mockTaskRepo, times(1)).findById(any());
        verify(mockTaskRepo, times(1)).save(any());
    }

    @Test
    void test_update_throwsRecordNotFoundException_providedBadTaskId() {
        UUID id = UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b");
        when(mockTaskRepo.findById(id))
                .thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.update(id, new NewTaskRequest()));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockTaskRepo, times(1)).findById(any());
    }

    @Test
    void test_assign_returnsTaskResponse_providedValidParams() {
        when(mockTaskRepo.findById(mockTask.getId()))
                .thenReturn(Optional.of(mockTask));
        when(mockResourceRepo.findById(mockResource.getId()))
                .thenReturn(Optional.of(mockResource));
        when(mockTokenService.extractTokenDetails(anyString()))
                .thenReturn(new Principal(mockUser.getId().toString(), mockUser.getRole().getRoleName()));
        when(mockUserRepo.findById(mockUser.getId()))
                .thenReturn(Optional.of(mockUser));
        when(mockTaskRepo.save(mockTask)).thenReturn(any(Task.class));
        when(mockResourceRepo.save(mockResource)).thenReturn(any(Resource.class));

        TaskAssignment request = new TaskAssignment(mockTask.getId(), mockResource.getId());

        TaskResponse response = service.assignTaskToResource("", request);

        // assert the proper message is returned, and that the repo was only queried once
        assertAll(
                () -> assertEquals(mockTask.getId(), response.getId()),
                () -> assertEquals(mockResource.getId(), response.getAssigneeId()));
        verify(mockTaskRepo, times(1)).findById(any());
        verify(mockResourceRepo, times(1)).findById(any());
        verify(mockTokenService, times(1)).extractTokenDetails(any());
        verify(mockUserRepo, times(1)).findById(any());
        verify(mockTaskRepo, times(1)).save(any());
        verify(mockResourceRepo, times(1)).save(any());
    }

    @Test
    void test_assign_throwsRecordNotFoundException_providedBadTaskId() {
        when(mockTaskRepo.findById(mockTask.getId()))
                .thenReturn(Optional.empty());

        TaskAssignment request = new TaskAssignment(mockTask.getId(), mockResource.getId());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.assignTaskToResource(
                        mockTokenService.generateToken(
                                new Principal(mockUser.getId().toString(), mockUser.getRole().getRoleName())),
                        request));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockTaskRepo, times(1)).findById(any());
        verify(mockResourceRepo, times(0)).findById(any());
        verify(mockUserRepo, times(0)).findById(any());
        verify(mockTaskRepo, times(0)).save(any());
        verify(mockResourceRepo, times(0)).save(any());

    }

    @Test
    void test_assign_throwsRecordNotFoundException_providedBadResourceId() {
        when(mockTaskRepo.findById(mockTask.getId()))
                .thenReturn(Optional.of(mockTask));
        when(mockResourceRepo.findById(mockResource.getId()))
                .thenReturn(Optional.empty());

        TaskAssignment request = new TaskAssignment(mockTask.getId(), mockResource.getId());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.assignTaskToResource(
                        mockTokenService.generateToken(
                                new Principal(mockUser.getId().toString(), mockUser.getRole().getRoleName())),
                        request));


        // assert the proper message is returned, and that the repo was only queried once
        verify(mockTaskRepo, times(1)).findById(any());
        verify(mockResourceRepo, times(1)).findById(any());
        verify(mockUserRepo, times(0)).findById(any());
        verify(mockTaskRepo, times(0)).save(any());
        verify(mockResourceRepo, times(0)).save(any());
    }

    @Test
    void test_assign_throwsRecordNotFoundException_providedBadPrincipalToken() {
        when(mockTaskRepo.findById(mockTask.getId()))
                .thenReturn(Optional.of(mockTask));
        when(mockResourceRepo.findById(mockResource.getId()))
                .thenReturn(Optional.of(mockResource));
        when(mockTokenService.extractTokenDetails(anyString()))
                .thenReturn(new Principal(mockUser.getId().toString(), mockUser.getRole().getRoleName()));
        when(mockUserRepo.findById(mockUser.getId()))
                .thenReturn(Optional.empty());

        TaskAssignment request = new TaskAssignment(mockTask.getId(), mockResource.getId());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.assignTaskToResource("",request));


        // assert the proper message is returned, and that the repo was only queried once
        verify(mockTaskRepo, times(1)).findById(any());
        verify(mockResourceRepo, times(1)).findById(any());
        verify(mockUserRepo, times(1)).findById(any());
        verify(mockTaskRepo, times(0)).save(any());
        verify(mockResourceRepo, times(0)).save(any());
    }

    @Test
    void test_delete() {
        service.delete(UUID.randomUUID());
        verify(mockTaskRepo, times(1)).deleteById(any());
    }
}

package com.russell.scheduler.project;

import com.russell.scheduler.common.EntitySearcher;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.project.dtos.NewProjectRequest;
import com.russell.scheduler.project.dtos.ProjectAssignment;
import com.russell.scheduler.project.dtos.ProjectResponseDetailed;
import com.russell.scheduler.resource.Resource;
import com.russell.scheduler.resource.ResourceRepository;
import com.russell.scheduler.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProjectServiceUnitTest {

    private ProjectService service;
    private final ProjectRepository mockProjectRepo = mock(ProjectRepository.class);
    private final ResourceRepository mockResourceRepo = mock(ResourceRepository.class);
    private final EntitySearcher mockEntitySearcher = mock(EntitySearcher.class);
    private Project mockProject1;
    private Project mockProject2;
    private Resource mockResource;

    @BeforeEach
    public void setup() {
        reset(mockProjectRepo, mockResourceRepo, mockEntitySearcher);
        service = new ProjectService(mockProjectRepo, mockResourceRepo, mockEntitySearcher);
        mockProject1 = new Project(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mockProject1", LocalDate.now(), LocalDate.now(), new Resource(), new HashSet<Task>());
        mockProject2 = new Project(UUID.fromString("aa4a20ab-cc98-4f9a-a09d-37b6fbd8087c"),
                "mockProject2", LocalDate.now(), LocalDate.now(), new Resource(), new HashSet<Task>());
        mockResource = new Resource(UUID.fromString("a27951ae-22fc-491d-b0c3-48bf5d8a4590"),
                "mock@resource.one", "first1", "last1",
                new HashSet<Project>(), new HashSet<Task>());
    }

    @Test
    void test_findAll_returnSetOfProjectResponses_providedRepoReturnsProjects() {
        List<Project> mockProjects = List.of(mockProject1, mockProject2);
        when(mockProjectRepo.findAll()).thenReturn(mockProjects);

        Set<ProjectResponseDetailed> response = service.findAll();

        // assert the proper number of users was returned, and that the repo was only queried once
        assertEquals(mockProjects.size(), response.size());
        verify(mockProjectRepo, times(1)).findAll();
    }

    @Test
    void test_findOne_returnProjectResponse_providedProjectId() {
        when(mockProjectRepo.findById(mockProject1.getId())).thenReturn(Optional.of(mockProject1));

        ProjectResponseDetailed response = service.findOne(mockProject1.getId());

        // assert all fields of the generated response match the test resource, and that the repo was only
        // queried once
        assertAll(
                () -> assertEquals(mockProject1.getId(), response.getId()),
                () -> assertEquals(mockProject1.getName(), response.getName()),
                () -> assertEquals(mockProject1.getStartDate(), response.getStartDate()),
                () -> assertEquals(mockProject1.getEndDate(), response.getEndDate()));
        verify(mockProjectRepo, times(1)).findById(mockProject1.getId());
    }

    @Test
    void test_findOne_throwsRecordNotFoundException_providedBadProjectId() {
        UUID badResourceId = UUID.randomUUID();

        when(mockProjectRepo.findById(badResourceId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.findOne(badResourceId));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockProjectRepo, times(1)).findById(badResourceId);
    }

    @Test
    void test_search_returnsSetOfProjectResponses_providedValidParam() {

        Set<Project> mockProjects = Set.of(mockProject1);

        Map<String, String> criteria = new HashMap<>();
        criteria.put("name", mockProject1.getName());
        when(mockEntitySearcher.search(criteria, Project.class)).thenReturn(mockProjects);

        Set<ProjectResponseDetailed> response = service.search(criteria);
        // assert the proper number of users was returned
        ProjectResponseDetailed content = response.stream().findFirst().get();
        assertAll(
                () -> assertEquals(mockProjects.size(), response.size()),
                () -> assertEquals(mockProject1.getId(), content.getId()),
                () -> assertEquals(mockProject1.getName(), content.getName()),
                () -> assertEquals(mockProject1.getStartDate(), content.getStartDate()),
                () -> assertEquals(mockProject1.getEndDate(), content.getEndDate()));
        verify(mockEntitySearcher, times(1)).search(criteria, Project.class);
    }

    @Test
    void test_search_redirectsToFindAll_providedEmptyParams() {
        List<Project> mockProjects = Arrays.asList(mockProject1, mockProject2);

        when(mockProjectRepo.findAll()).thenReturn(mockProjects);

        Set<ProjectResponseDetailed> response = service.search(new HashMap<String,String>());

        // assert the proper number of users was returned, and that the repo was only queried once
        assertEquals(mockProjects.size(), response.size());
        verify(mockProjectRepo, times(1)).findAll();
    }

    @Test
    void test_search_throwsRecordNotFoundException_providedBadParam() {
        Map<String, String> criteria = new HashMap<>();
        criteria.put("name", "Does Not Exist");
        when(mockEntitySearcher.search(criteria, Project.class)).thenReturn(new HashSet<>());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.search(criteria));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockEntitySearcher, times(1)).search(criteria, Project.class);
    }


    @Test
    void test_create_returnsResourceCreationResponse_providedValidResourceInfo() {
        NewProjectRequest request = new NewProjectRequest();
        request.setName("test");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(1));

        when(mockProjectRepo.save(any(Project.class))).thenReturn(any(Project.class));

        RecordCreationResponse response = service.create(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        verify(mockProjectRepo, times(1)).save(any());
    }

    @Test
    void test_update_returnsProjectResponse_providedValidResourceInfo() {
        NewProjectRequest request = new NewProjectRequest();
        request.setName("test");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(1));


        when(mockProjectRepo.findById(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b")))
                .thenReturn(Optional.of(mockProject1));
        when(mockProjectRepo.save(any(Project.class))).thenReturn(any(Project.class));

        ProjectResponseDetailed response = service.update(mockProject1.getId(), request);

        // Assert
        assertAll(
                () -> assertEquals(mockProject1.getId(), response.getId()),
                () -> assertEquals(request.getName(), response.getName()),
                () -> assertEquals(request.getStartDate(), response.getStartDate()),
                () -> assertEquals(request.getEndDate(), response.getEndDate()));
        verify(mockProjectRepo, times(1)).findById(any());
        verify(mockProjectRepo, times(1)).save(any());
    }

    @Test
    void test_update_throwsRecordNotFoundException_providedBadParam() {
        UUID id = UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b");
        when(mockProjectRepo.findById(id))
                .thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.update(id, new NewProjectRequest()));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockProjectRepo, times(1)).findById(any());
    }

    @Test
    void test_assign_returnsProjectResponse_providedValidParams() {
        when(mockProjectRepo.findById(mockProject1.getId()))
                .thenReturn(Optional.of(mockProject1));
        when(mockResourceRepo.findById(mockResource.getId()))
                .thenReturn(Optional.of(mockResource));
        when(mockProjectRepo.save(mockProject1)).thenReturn(any(Project.class));
        when(mockResourceRepo.save(mockResource)).thenReturn(any(Resource.class));

        ProjectAssignment request = new ProjectAssignment(mockProject1.getId(), mockResource.getId());

        ProjectResponseDetailed response = service.assignOwnerToProject(request);

        // assert the proper message is returned, and that the repo was only queried once
        assertAll(
                () -> assertEquals(mockProject1.getId(), response.getId()),
                () -> assertEquals(mockResource.getId(), response.getOwner().getId()));
        verify(mockResourceRepo, times(1)).findById(any());
        verify(mockProjectRepo, times(1)).findById(any());
        verify(mockResourceRepo, times(1)).save(any());
        verify(mockProjectRepo, times(1)).save(any());
    }

    @Test
    void test_assign_throwsRecordNotFoundException_providedBadResourceId() {
        when(mockResourceRepo.findById(mockResource.getId()))
                .thenReturn(Optional.empty());

        ProjectAssignment request = new ProjectAssignment(mockProject1.getId(), mockResource.getId());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.assignOwnerToProject(request));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockResourceRepo, times(1)).findById(any());
        verify(mockProjectRepo, times(0)).findById(any());
        verify(mockResourceRepo, times(0)).save(any());
        verify(mockProjectRepo, times(0)).save(any());
    }

    @Test
    void test_assign_throwsRecordNotFoundException_providedBadProjectId() {
        when(mockResourceRepo.findById(mockResource.getId()))
                .thenReturn(Optional.of(mockResource));
        when(mockProjectRepo.findById(mockProject1.getId()))
                .thenReturn(Optional.empty());

        ProjectAssignment request = new ProjectAssignment(mockProject1.getId(), mockResource.getId());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.assignOwnerToProject(request));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockResourceRepo, times(1)).findById(any());
        verify(mockProjectRepo, times(1)).findById(any());
        verify(mockResourceRepo, times(0)).save(any());
        verify(mockProjectRepo, times(0)).save(any());
    }

    @Test
    void test_delete() {
        service.delete(UUID.randomUUID());
        verify(mockResourceRepo, times(1)).delete(any());
    }
}

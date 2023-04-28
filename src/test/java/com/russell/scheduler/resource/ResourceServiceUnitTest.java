package com.russell.scheduler.resource;

import com.russell.scheduler.common.EntitySearcher;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.common.exceptions.RecordPersistenceException;
import com.russell.scheduler.project.Project;
import com.russell.scheduler.resource.dtos.NewResourceRequest;
import com.russell.scheduler.resource.dtos.ResourceResponseDetailed;
import com.russell.scheduler.task.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ResourceServiceUnitTest {

    private ResourceService service;
    private final ResourceRepository mockResourceRepo = mock(ResourceRepository.class);
    private final EntitySearcher mockEntitySearcher = mock(EntitySearcher.class);

    @BeforeEach
    public void setup() {
        reset(mockResourceRepo, mockEntitySearcher);
        service = new ResourceService(mockResourceRepo, mockEntitySearcher);
    }

    @Test
    void test_findAllResources_returnSetOfResourceResponses_providedRepoReturnsResources() {
        List<Resource> mockResources = List.of(
                new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                        "mock@resource.one", "first1", "last1", new HashSet<Project>(), new HashSet<Task>()),
                new Resource(UUID.fromString("aa4a20ab-cc98-4f9a-a09d-37b6fbd8087c"),
                        "mock@resource.two", "first2", "last2", new HashSet<Project>(), new HashSet<Task>()));
        when(mockResourceRepo.findAll()).thenReturn(mockResources);

        Set<ResourceResponseDetailed> response = service.findAll();

        // assert the proper number of users was returned, and that the repo was only queried once
        assertEquals(mockResources.size(), response.size());
        verify(mockResourceRepo, times(1)).findAll();
    }

    @Test
    void test_findOneResource_returnResourceResponse_providedResourceId() {
        Resource mockResource = new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mock@resource.one", "first1", "last1", new HashSet<Project>(), new HashSet<Task>());

        when(mockResourceRepo.findById(mockResource.getId())).thenReturn(Optional.of(mockResource));

        ResourceResponseDetailed response = service.findOne(mockResource.getId());

        // assert all fields of the generated response match the test resource, and that the repo was only
        // queried once
        assertAll(
                () -> assertEquals(mockResource.getId(), response.getId()),
                () -> assertEquals(mockResource.getEmail(), response.getEmail()),
                () -> assertEquals(mockResource.getFirstName(), response.getFirstName()),
                () -> assertEquals(mockResource.getLastName(), response.getLastName()));
        verify(mockResourceRepo, times(1)).findById(mockResource.getId());
    }

    @Test
    void test_findOneResource_throwsRecordNotFoundException_providedBadResourceId() {
        UUID badResourceId = UUID.randomUUID();

        when(mockResourceRepo.findById(badResourceId)).thenReturn(Optional.empty());

        // assert the InvalidCredentialsException is thrown on authentication
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.findOne(badResourceId));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockResourceRepo, times(1)).findById(badResourceId);
    }

    @Test
    void test_search_returnsSetOfResourceResponses_providedValidParam() {
        Resource mockResource = new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mock@resource.one", "first1", "last1", new HashSet<Project>(), new HashSet<Task>());
        Set<Resource> mockResources = Set.of(mockResource);

        Map<String, String> criteria = new HashMap<>();
        criteria.put("email", "mock@resource.one");
        when(mockEntitySearcher.search(criteria, Resource.class)).thenReturn(mockResources);

        Set<ResourceResponseDetailed> response = service.search(criteria);
        // assert the proper number of users was returned
        ResourceResponseDetailed content = response.stream().findFirst().get();
        assertAll(
                () -> assertEquals(mockResources.size(), response.size()),
                () -> assertEquals(mockResource.getId(), content.getId()),
                () -> assertEquals(mockResource.getEmail(), content.getEmail()),
                () -> assertEquals(mockResource.getFirstName(), content.getFirstName()),
                () -> assertEquals(mockResource.getLastName(), content.getLastName()));
        verify(mockEntitySearcher, times(1)).search(criteria, Resource.class);
    }

    @Test
    void test_search_redirectsToFindAll_providedEmptyParams() {
        List<Resource> mockResources = Arrays.asList(new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mock@resource.one", "first1", "last1", new HashSet<Project>(), new HashSet<Task>()));

        when(mockResourceRepo.findAll()).thenReturn(mockResources);

        Set<ResourceResponseDetailed> response = service.search(new HashMap<String,String>());

        // assert the proper number of users was returned, and that the repo was only queried once
        assertEquals(mockResources.size(), response.size());
        verify(mockResourceRepo, times(1)).findAll();
    }

    @Test
    void test_search_throwsRecordNotFoundException_providedBadParam() {
        Map<String, String> criteria = new HashMap<>();
        criteria.put("email", "bad@email.com");
        when(mockEntitySearcher.search(criteria, Resource.class)).thenReturn(new HashSet<>());

        // assert the InvalidCredentialsException is thrown on authentication
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.search(criteria));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockEntitySearcher, times(1)).search(criteria, Resource.class);
    }

    @Test
    void test_create_throwsRecordPersistenceException_providedDuplicateEmail() {
        NewResourceRequest request = new NewResourceRequest();
        request.setEmail("test@mock.one");
        request.setFirstName("first1");
        request.setLastName("last1");

        when(mockResourceRepo.existsByEmail(request.getEmail())).thenReturn(true);

        RecordPersistenceException exception = assertThrows(
                RecordPersistenceException.class,
                () -> service.create(request));

        // Assert
        assertEquals("That email address is already associated with another resource", exception.getMessage());
        verify(mockResourceRepo, times(1)).existsByEmail(request.getEmail());
        verify(mockResourceRepo, times(0)).save(any());
    }

    @Test
    void test_create_returnsResourceCreationResponse_providedValidResourceInfo() {
        NewResourceRequest request = new NewResourceRequest();
        request.setEmail("test@mock.one");
        request.setFirstName("first1");
        request.setLastName("last1");

        when(mockResourceRepo.existsByEmail(request.getEmail())).thenReturn(false);
        when(mockResourceRepo.save(any(Resource.class))).thenReturn(any(Resource.class));

        RecordCreationResponse response = service.create(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        verify(mockResourceRepo, times(1)).existsByEmail(anyString());
        verify(mockResourceRepo, times(1)).save(any());
    }

    @Test
    void test_update_returnsResourceResponse_providedValidResourceInfo() {
        Resource mockResource = new Resource(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mock@resource.one", "first1", "last1", new HashSet<Project>(), new HashSet<Task>());
        NewResourceRequest request = new NewResourceRequest();
        request.setEmail("test@mock.two");
        request.setFirstName("first2");
        request.setLastName("last2");


        when(mockResourceRepo.findById(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b")))
                .thenReturn(Optional.of(mockResource));
        when(mockResourceRepo.save(any(Resource.class))).thenReturn(any(Resource.class));

        ResourceResponseDetailed response = service.update(mockResource.getId(), request);

        // Assert
        assertAll(
                () -> assertEquals(mockResource.getId(), response.getId()),
                () -> assertEquals(request.getEmail(), response.getEmail()),
                () -> assertEquals(request.getFirstName(), response.getFirstName()),
                () -> assertEquals(request.getLastName(), response.getLastName()));
        verify(mockResourceRepo, times(1)).findById(any());
        verify(mockResourceRepo, times(1)).save(any());
    }

    @Test
    void test_update_throwsRecordNotFoundException_providedBadParam() {
        UUID id = UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b");
        when(mockResourceRepo.findById(id))
                .thenReturn(Optional.empty());
        // assert the InvalidCredentialsException is thrown on authentication
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.update(id, new NewResourceRequest()));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockResourceRepo, times(1)).findById(any());
    }
}

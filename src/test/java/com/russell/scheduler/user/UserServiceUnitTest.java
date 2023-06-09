package com.russell.scheduler.user;

import com.russell.scheduler.auth.dtos.AuthRequest;
import com.russell.scheduler.common.EntitySearcher;
import com.russell.scheduler.common.dtos.RecordCreationResponse;
import com.russell.scheduler.common.exceptions.InvalidCredentialsException;
import com.russell.scheduler.common.exceptions.RecordNotFoundException;
import com.russell.scheduler.common.exceptions.RecordPersistenceException;
import com.russell.scheduler.user.dtos.NewUserRequest;
import com.russell.scheduler.user.dtos.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {

    private UserService service;
    private final UserRepository mockUserRepo = mock(UserRepository.class);
    private final UserRoleRepository mockUserRoleRepo = mock(UserRoleRepository.class);
    private final EntitySearcher mockEntitySearcher = mock(EntitySearcher.class);
    private User mockUser1;
    private User mockUser2;
    private UserRole mockRole;

    @BeforeEach
    public void setup() {
        reset(mockUserRepo, mockUserRoleRepo, mockEntitySearcher);
        service = new UserService(mockUserRepo, mockUserRoleRepo, mockEntitySearcher);
        mockRole = new UserRole(1, "ADMIN", 1);
        mockUser1 = new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mockuser1", "mock@user.one", "first1", "last1",
                "P@ssword1", mockRole);
        mockUser2 = new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087c"),
                "mockuser2", "mock@user.two", "first2", "last2",
                "P@ssword2", mockRole);
    }

    @Test
    void test_authenticate_returnUserResponse_providedAuthRequest() {
        AuthRequest request = new AuthRequest();
        request.setUsername(mockUser1.getUsername());
        request.setPassword(mockUser1.getPassword());

        when(mockUserRepo.findUserByUsernameAndPassword(request.getUsername(), request.getPassword()))
                .thenReturn(Optional.of(mockUser1));

        UserResponse response = service.authenticate(request);

        // assert all fields of the generated response match the test user, and that the repo was only
        // queried once
        assertAll(
                () -> assertEquals(mockUser1.getId(), response.getId()),
                () -> assertEquals(mockUser1.getUsername(), response.getUsername()),
                () -> assertEquals(mockUser1.getRole().getRoleName(), response.getRoleName()));
        verify(mockUserRepo, times(1))
                .findUserByUsernameAndPassword(mockUser1.getUsername(), mockUser1.getPassword());
    }

    @Test
    void test_authenticate_throwsInvalidCredentialsException_providedBadUsername() {
        AuthRequest request = new AuthRequest();
        request.setUsername(mockUser1.getUsername());
        request.setPassword("wrongP@ssword");

        when(mockUserRepo.findUserByUsernameAndPassword(request.getUsername(), request.getPassword()))
                .thenReturn(Optional.empty());

        // assert the InvalidCredentialsException is thrown on authentication
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> service.authenticate(request));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Invalid username or password", exception.getMessage());
        verify(mockUserRepo, times(1))
                .findUserByUsernameAndPassword(request.getUsername(), request.getPassword());
    }

    @Test
    void test_findAllUsers_returnSetOfUserResponses_providedRepoReturnsUsers() {
        List<User> mockUsers = Arrays.asList(mockUser1, mockUser2);
        when(mockUserRepo.findAll()).thenReturn(mockUsers);

        Set<UserResponse> response = service.findAll();

        // assert the proper number of users was returned, and that the repo was only queried once
        assertEquals(mockUsers.size(), response.size());
        verify(mockUserRepo, times(1)).findAll();
    }

    @Test
    void test_findSingleUser_returnUserResponse_providedUserId() {
        when(mockUserRepo.findById(mockUser1.getId())).thenReturn(Optional.of(mockUser1));

        UserResponse response = service.findOne(mockUser1.getId());

        // assert all fields of the generated response match the test user, and that the repo was only
        // queried once
        assertAll(
                () -> assertEquals(mockUser1.getId(), response.getId()),
                () -> assertEquals(mockUser1.getUsername(), response.getUsername()),
                () -> assertEquals(mockUser1.getRole().getRoleName(), response.getRoleName()));
        verify(mockUserRepo, times(1)).findById(mockUser1.getId());
    }

    @Test
    void test_findSingleUser_throwsRecordNotFoundException_providedBadUserId() {
        UUID badUserId = UUID.randomUUID();

        when(mockUserRepo.findById(badUserId)).thenReturn(Optional.empty());

        // assert the InvalidCredentialsException is thrown on authentication
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.findOne(badUserId));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockUserRepo, times(1)).findById(badUserId);
    }

    @Test
    void test_search_returnsSetOfUserResponses_providedValidParam() {
        Set<User> mockUsers = Set.of(mockUser1);

        Map<String, String> criteria = new HashMap<>();
        criteria.put("username", mockUser1.getUsername());
        when(mockEntitySearcher.search(criteria, User.class)).thenReturn(mockUsers);

        Set<UserResponse> response = service.search(criteria);
        // assert the proper number of users was returned
        UserResponse content = response.stream().findFirst().get();
        assertAll(
                () -> assertEquals(mockUsers.size(), response.size()),
                () -> assertEquals(mockUser1.getId(), content.getId()),
                () -> assertEquals(mockUser1.getUsername(), content.getUsername()),
                () -> assertEquals(mockUser1.getRole().getRoleName(), content.getRoleName()));
        verify(mockEntitySearcher, times(1)).search(criteria, User.class);
    }

    @Test
    void test_search_redirectsToFindAll_providedEmptyParams() {
        List<User> mockUsers = Arrays.asList(mockUser1, mockUser2);
        when(mockUserRepo.findAll()).thenReturn(mockUsers);

        Set<UserResponse> response = service.search(new HashMap<String,String>());

        // assert the proper number of users was returned, and that the repo was only queried once
        assertEquals(mockUsers.size(), response.size());
        verify(mockUserRepo, times(1)).findAll();
    }

    @Test
    void test_search_throwsRecordNotFoundException_providedBadParam() {
        Map<String, String> criteria = new HashMap<>();
        criteria.put("username", "doesnotexist");
        when(mockEntitySearcher.search(criteria, User.class)).thenReturn(new HashSet<>());

        // assert the InvalidCredentialsException is thrown on authentication
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> service.search(criteria));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Record could not be found with the given search parameters", exception.getMessage());
        verify(mockEntitySearcher, times(1)).search(criteria, User.class);
    }

    @Test
    void test_create_throwsRecordPersistenceException_providedDuplicateUsername() {
        NewUserRequest request = new NewUserRequest();
        request.setUsername(mockUser1.getUsername());
        request.setPassword(mockUser1.getPassword());
        request.setEmail(mockUser1.getEmail());
        request.setFirstName(mockUser1.getFirstName());
        request.setLastName(mockUser1.getLastName());
        request.setRoleName(mockUser1.getRole().getRoleName());

        when(mockUserRepo.existsByUsername(request.getUsername())).thenReturn(true);

        RecordPersistenceException exception = assertThrows(
                RecordPersistenceException.class,
                () -> service.create(request));

        // Assert
        assertEquals("That username is taken", exception.getMessage());
        verify(mockUserRepo, times(1)).existsByUsername(request.getUsername());
        verify(mockUserRepo, times(0)).existsByEmail(anyString());
        verify(mockUserRoleRepo, times(0)).findUserRoleByRoleName(anyString());
        verify(mockUserRepo, times(0)).save(any());
    }

    @Test
    void test_create_throwsRecordPersistenceException_providedDuplicateEmail() {
        NewUserRequest request = new NewUserRequest();
        request.setUsername(mockUser1.getUsername());
        request.setPassword(mockUser1.getPassword());
        request.setEmail(mockUser1.getEmail());
        request.setFirstName(mockUser1.getFirstName());
        request.setLastName(mockUser1.getLastName());
        request.setRoleName(mockUser1.getRole().getRoleName());

        when(mockUserRepo.existsByEmail(request.getEmail())).thenReturn(true);

        RecordPersistenceException exception = assertThrows(
                RecordPersistenceException.class,
                () -> service.create(request));

        // Assert
        assertEquals("That email address is already associated with another user", exception.getMessage());
        verify(mockUserRepo, times(1)).existsByUsername(request.getUsername());
        verify(mockUserRepo, times(1)).existsByEmail(anyString());
        verify(mockUserRoleRepo, times(0)).findUserRoleByRoleName(anyString());
        verify(mockUserRepo, times(0)).save(any());
    }

    @Test
    void test_create_throwsRecordPersistenceException_providedBadRoleName() {
        NewUserRequest request = new NewUserRequest();
        request.setUsername(mockUser1.getUsername());
        request.setPassword(mockUser1.getPassword());
        request.setEmail(mockUser1.getEmail());
        request.setFirstName(mockUser1.getFirstName());
        request.setLastName(mockUser1.getLastName());
        request.setRoleName("Not a Real Role");

        when(mockUserRoleRepo.findUserRoleByRoleName(request.getRoleName())).thenReturn(Optional.empty());

        RecordPersistenceException exception = assertThrows(
                RecordPersistenceException.class,
                () -> service.create(request));

        // Assert
        assertEquals("Invalid role supplied", exception.getMessage());
        verify(mockUserRepo, times(1)).existsByUsername(request.getUsername());
        verify(mockUserRepo, times(1)).existsByEmail(anyString());
        verify(mockUserRoleRepo, times(1)).findUserRoleByRoleName(anyString());
        verify(mockUserRepo, times(0)).save(any());
    }

    @Test
    void test_create_returnsResourceCreationResponse_providedValidUserInfo() {
        NewUserRequest request = new NewUserRequest();
        request.setUsername(mockUser1.getUsername());
        request.setPassword(mockUser1.getPassword());
        request.setEmail(mockUser1.getEmail());
        request.setFirstName(mockUser1.getFirstName());
        request.setLastName(mockUser1.getLastName());
        request.setRoleName(mockUser1.getRole().getRoleName());

        when(mockUserRepo.existsByUsername(request.getUsername())).thenReturn(false);
        when(mockUserRepo.existsByEmail(request.getEmail())).thenReturn(false);
        when(mockUserRoleRepo.findUserRoleByRoleName(request.getRoleName())).thenReturn(Optional.of(mockRole));
        when(mockUserRepo.save(any(User.class))).thenReturn(any(User.class));

        RecordCreationResponse response = service.create(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        verify(mockUserRepo, times(1)).existsByUsername(request.getUsername());
        verify(mockUserRepo, times(1)).existsByEmail(anyString());
        verify(mockUserRepo, times(1)).save(any());
    }


}

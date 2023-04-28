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

public class UserServiceUnitTest {

    private UserService service;
    private final UserRepository mockUserRepo = mock(UserRepository.class);
    private final EntitySearcher mockEntitySearcher = mock(EntitySearcher.class);

    @BeforeEach
    public void setup() {
        reset(mockUserRepo, mockEntitySearcher);
        service = new UserService(mockUserRepo, mockEntitySearcher);
    }

    @Test
    void test_authenticate_returnUserResponse_providedAuthRequest() {
        String username = "mockuser1";
        String password = "P@ssword1";
        AuthRequest request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(password);

        User mockUser = new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mockuser1", "mock@user.one", "first1", "last1",
                "P@ssword1", new UserRole(1, "ADMIN", 1));

        when(mockUserRepo.findUserByUsernameAndPassword(username, password)).thenReturn(Optional.of(mockUser));

        UserResponse response = service.authenticate(request);

        // assert all fields of the generated response match the test user, and that the repo was only
        // queried once
        assertAll(
                () -> assertEquals(mockUser.getId(), response.getId()),
                () -> assertEquals(mockUser.getUsername(), response.getUsername()),
                () -> assertEquals(mockUser.getRole(), response.getRole()));
        verify(mockUserRepo, times(1)).findUserByUsernameAndPassword(username, password);
    }

    @Test
    void test_authenticate_throwsInvalidCredentialsException_providedBadUsername() {
        String username = "mockuser1";
        String password = "wrongP@ssword";
        AuthRequest request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(password);

        User mockUser = new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mockuser1", "mock@user.one", "first1", "last1",
                "P@ssword1", new UserRole(1, "ADMIN", 1));

        when(mockUserRepo.findUserByUsernameAndPassword(username, password)).thenReturn(Optional.empty());

        // assert the InvalidCredentialsException is thrown on authentication
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> service.authenticate(request));

        // assert the proper message is returned, and that the repo was only queried once
        assertEquals("Invalid username or password", exception.getMessage());
        verify(mockUserRepo, times(1)).findUserByUsernameAndPassword(username, password);
    }

    @Test
    void test_findAllUsers_returnSetOfUserResponses_providedRepoReturnsUsers() {
        List<User> mockUsers = Arrays.asList(
                new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"), "mockuser1", "mock@user.one", "first1", "last1", "P@ssword1", new UserRole(1, "ADMIN", 1)),
                new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087c"), "mockuser2", "mock@user.two", "first2", "last2", "P@ssword2", new UserRole(2, "TEST", 2))
        );
        when(mockUserRepo.findAll()).thenReturn(mockUsers);

        Set<UserResponse> response = service.findAll();

        // assert the proper number of users was returned, and that the repo was only queried once
        assertEquals(mockUsers.size(), response.size());
        verify(mockUserRepo, times(1)).findAll();
    }

    @Test
    void test_findSingleUser_returnUserResponse_providedUserId() {
        User mockUser = new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                "mockuser1", "mock@user.one", "first1", "last1",
                "P@ssword1", new UserRole(1, "ADMIN", 1));
        when(mockUserRepo.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        UserResponse response = service.findOne(mockUser.getId());

        // assert all fields of the generated response match the test user, and that the repo was only
        // queried once
        assertAll(
                () -> assertEquals(mockUser.getId(), response.getId()),
                () -> assertEquals(mockUser.getUsername(), response.getUsername()),
                () -> assertEquals(mockUser.getRole(), response.getRole()));
        verify(mockUserRepo, times(1)).findById(mockUser.getId());
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
        Set<User> mockUsers = new HashSet<>();
        User mockUser = new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"),
                        "mockuser1", "mock@user.one", "first1", "last1",
                        "P@ssword1", new UserRole(1, "ADMIN", 1));
        mockUsers.add(mockUser);

        Map<String, String> criteria = new HashMap<>();
        criteria.put("username", "mockuser1");
        when(mockEntitySearcher.search(criteria, User.class)).thenReturn(mockUsers);

        Set<UserResponse> response = service.search(criteria);
        // assert the proper number of users was returned
        UserResponse content = response.stream().findFirst().get();
        assertAll(
                () -> assertEquals(mockUsers.size(), response.size()),
                () -> assertEquals(mockUser.getId(), content.getId()),
                () -> assertEquals(mockUser.getUsername(), content.getUsername()),
                () -> assertEquals(mockUser.getRole(), content.getRole()));
        verify(mockEntitySearcher, times(1)).search(criteria, User.class);
    }

    @Test
    void test_search_redirectsToFindAll_providedEmptyParams() {
        List<User> mockUsers = Arrays.asList(
                new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087b"), "mockuser1", "mock@user.one", "first1", "last1", "P@ssword1", new UserRole(1, "ADMIN", 1)),
                new User(UUID.fromString("aa4a20aa-cc97-4f99-a09c-37b6fbd8087c"), "mockuser2", "mock@user.two", "first2", "last2", "P@ssword2", new UserRole(2, "TEST", 2))
        );
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
        request.setUsername("mockuser1");
        request.setPassword("P@ssword1");
        request.setEmail("test@mock.one");
        request.setFirstName("first1");
        request.setLastName("last1");
        request.setRole(new UserRole(1, "ADMIN", 1));

        when(mockUserRepo.existsByUsername(request.getUsername())).thenReturn(true);

        RecordPersistenceException exception = assertThrows(
                RecordPersistenceException.class,
                () -> service.create(request));

        // Assert
        assertEquals("That username is taken", exception.getMessage());
        verify(mockUserRepo, times(1)).existsByUsername(request.getUsername());
        verify(mockUserRepo, times(0)).existsByEmail(anyString());
        verify(mockUserRepo, times(0)).save(any());
    }

    @Test
    void test_create_throwsRecordPersistenceException_providedDuplicateEmail() {
        NewUserRequest request = new NewUserRequest();
        request.setUsername("mockuser1");
        request.setPassword("P@ssword1");
        request.setEmail("test@mock.one");
        request.setFirstName("first1");
        request.setLastName("last1");
        request.setRole(new UserRole(1, "ADMIN", 1));

        when(mockUserRepo.existsByEmail(request.getEmail())).thenReturn(true);

        RecordPersistenceException exception = assertThrows(
                RecordPersistenceException.class,
                () -> service.create(request));

        // Assert
        assertEquals("That email address is already associated with another user", exception.getMessage());
        verify(mockUserRepo, times(1)).existsByUsername(request.getUsername());
        verify(mockUserRepo, times(1)).existsByEmail(anyString());
        verify(mockUserRepo, times(0)).save(any());
    }

    @Test
    void test_create_returnsResourceCreationResponse_providedValidUserInfo() {
        NewUserRequest request = new NewUserRequest();
        request.setUsername("mockuser1");
        request.setPassword("P@ssword1");
        request.setEmail("test@mock.one");
        request.setFirstName("first1");
        request.setLastName("last1");
        request.setRole(new UserRole(1, "ADMIN", 1));

        when(mockUserRepo.existsByUsername(request.getUsername())).thenReturn(false);
        when(mockUserRepo.existsByEmail(request.getEmail())).thenReturn(false);
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

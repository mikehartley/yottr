package uk.co.yottr.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.security.MemberSessions;
import uk.co.yottr.testconfig.ConstantsForTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private MemberSessions mockMemberSessions;

    private UserService userService;

    @BeforeEach
    public void init() {
        userService = new UserService(mockUserRepository, passwordEncoder, mockMemberSessions);
    }

    @Test
    public void save() throws Exception {

        final String plaintextPassword = "this is plaintext";

        final User userWithPlaintextPassword = new User();
        userWithPlaintextPassword.setPassword(plaintextPassword);

        userService.save(userWithPlaintextPassword, true);

        verify(passwordEncoder).encode(plaintextPassword);
        verify(mockUserRepository).save(any(User.class));
    }

    @Test
    public void doesNotEncodePasswordWhenNotRequired() {
        final String originalPassword = "this is the original";

        final User userWithPlaintextPassword = new User();
        userWithPlaintextPassword.setPassword(originalPassword);

        userService.save(userWithPlaintextPassword, false);

        verify(passwordEncoder, never()).encode(originalPassword);
        verify(mockUserRepository).save(any(User.class));
    }

    @Test
    public void findAll() {
        List<User> userList = new ArrayList<>();
        userList.add(createUser("u1"));
        userList.add(createUser("u2"));
        when(mockUserRepository.findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST)).thenReturn(new PageImpl<>(userList));

        final Iterable<User> allUsers = userService.findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST);
        final Iterator<User> userIterator = allUsers.iterator();

        assertEquals("u1", userIterator.next().getUsername());
        assertEquals("u2", userIterator.next().getUsername());
        assertFalse(userIterator.hasNext());

        verify(mockUserRepository).findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST);
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    public void deleteByUserEndsTheirSessions() {
        User user = createUser("u1");

        userService.delete(user);

        verify(mockUserRepository).delete(user);
        verify(mockMemberSessions).expire("u1");
        verifyNoMoreInteractions(mockUserRepository, mockMemberSessions);
    }

    @Test
    public void deleteByIdEndsTheirSessions() {
        final long id = 5L;
        User user = createUser("u1");
        user.setId(id);
        when(mockUserRepository.findById(id)).thenReturn(Optional.of(user));

        userService.delete(id);

        verify(mockUserRepository).findById(id);
        verify(mockUserRepository).delete(user);
        verify(mockMemberSessions).expire("u1");
        verifyNoMoreInteractions(mockUserRepository, mockMemberSessions);
    }

    @Test
    public void deleteByIdWhenNotFoundDoesNothing() {
        final long id = 5L;
        when(mockUserRepository.findById(id)).thenReturn(Optional.empty());

        userService.delete(id);

        verify(mockUserRepository).findById(id);
        verifyNoMoreInteractions(mockUserRepository, mockMemberSessions);
    }

    @Test
    public void disablingAUserEndsTheirSessions() {
        final User user = createUser("u1");
        user.setEnabled(true);
        when(mockUserRepository.save(user)).thenReturn(user);

        final User saved = userService.flipEnabled(user);

        assertFalse(saved.isEnabled(), "disabled");
        verify(mockUserRepository).save(user);
        verify(mockMemberSessions).expire("u1");
        verifyNoMoreInteractions(mockUserRepository, mockMemberSessions);
    }

    @Test
    public void enablingAUserLeavesSessionsAlone() {
        final User user = createUser("u1");
        user.setEnabled(false);
        when(mockUserRepository.save(user)).thenReturn(user);

        final User saved = userService.flipEnabled(user);

        assertTrue(saved.isEnabled(), "enabled");
        verify(mockUserRepository).save(user);
        verifyNoMoreInteractions(mockUserRepository, mockMemberSessions);
    }

    @Test
    public void findByUsername() {
        final String username = "u1";
        User user = createUser(username);

        when(mockUserRepository.findByUsername(username)).thenReturn(user);

        final Optional<User> foundUser = userService.findByUsername(username);

        assertEquals(Optional.of(user), foundUser, "user");
        verify(mockUserRepository).findByUsername(username);
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    public void findByUsernameWhenNotFound() {
        final String username = "u1";

        when(mockUserRepository.findByUsername(username)).thenReturn(null);

        final Optional<User> foundUser = userService.findByUsername(username);

        assertTrue(foundUser.isEmpty(), "user should not have been found");
        verify(mockUserRepository).findByUsername(username);
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    public void findMember() {
        final String username = "u1";
        final User user = createUser(username);
        when(mockUserRepository.findByUsername(username)).thenReturn(user);

        assertSame(user, userService.findMember(username), "member");
    }

    @Test
    public void findMemberWhenNotFound() {
        final String username = "u1";
        when(mockUserRepository.findByUsername(username)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> userService.findMember(username));
    }

    @Test
    public void findById() {
        Long id = 5L;
        User user = createUser("u1");
        user.setId(id);
        when(mockUserRepository.findById(id)).thenReturn(Optional.of(user));

        final Optional<User> foundUser = userService.findById(id);

        assertEquals(Optional.of(user), foundUser, "user");
        verify(mockUserRepository).findById(id);
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    public void userExists() {
        Long id = 5L;
        User user = createUser("u1");
        user.setId(id);
        when(mockUserRepository.existsById(id)).thenReturn(true);

        final boolean userExists = userService.userExists(id);

        assertTrue(userExists, "user should exist");
        verify(mockUserRepository).existsById(id);
        verifyNoMoreInteractions(mockUserRepository);
    }

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
}
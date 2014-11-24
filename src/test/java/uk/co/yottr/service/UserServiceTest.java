package uk.co.yottr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.testconfig.ConstantsForTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository mockUserRepository;

    private UserService userService;

    @Before
    public void init() {
        userService = new UserService(mockUserRepository, passwordEncoder);
    }

    @Test
    public void save() throws Exception {

        final String plaintextPassword = "this is plaintext";

        final User userWithPlaintextPassword = new User();
        userWithPlaintextPassword.setPassword(plaintextPassword);

        userService.save(userWithPlaintextPassword, true);

        verify(passwordEncoder, times(1)).encode(plaintextPassword);
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
    public void deleteByUser() {
        User user = createUser("u1");

        userService.delete(user);

        verify(mockUserRepository).delete(user);
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    public void deleteById() {
        final long id = 5L;
        User user = createUser("u1");
        user.setId(id);

        userService.delete(id);

        verify(mockUserRepository).delete(id);
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    public void findByUsername() {
        final String username = "u1";
        User user = createUser(username);

        when(mockUserRepository.findByUsername(username)).thenReturn(user);

        final User foundUser = userService.findByUsername(username);

        assertEquals("user", user, foundUser);
        verify(mockUserRepository).findByUsername(username);
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    public void findByUsernameWhenNotFound() {
        final String username = "u1";

        when(mockUserRepository.findByUsername(username)).thenReturn(null);

        final User foundUser = userService.findByUsername(username);

        assertNull("user should not have been found", foundUser);
        verify(mockUserRepository).findByUsername(username);
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    public void findById() {
        Long id = 5L;
        User user = createUser("u1");
        user.setId(id);
        when(mockUserRepository.findOne(id)).thenReturn(user);

        final User foundUser = userService.findById(id);

        assertEquals("user", user, foundUser);
        verify(mockUserRepository).findOne(id);
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    public void userExists() {
        Long id = 5L;
        User user = createUser("u1");
        user.setId(id);
        when(mockUserRepository.exists(id)).thenReturn(true);

        final boolean userExists = userService.userExists(id);

        assertTrue("user should exist", userExists);
        verify(mockUserRepository).exists(id);
        verifyNoMoreInteractions(mockUserRepository);
    }

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
}
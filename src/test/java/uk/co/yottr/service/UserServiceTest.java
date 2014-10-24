package uk.co.yottr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;

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
}
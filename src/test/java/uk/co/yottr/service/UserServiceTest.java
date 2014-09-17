package uk.co.yottr.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

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

    @Test
    public void testSave() throws Exception {

        final UserService userService = new UserService(mockUserRepository, passwordEncoder);

        final String plaintextPassword = "this is plaintext";

        final User userWithPlaintextPassword = new User();
        userWithPlaintextPassword.setPassword(plaintextPassword);

        userService.save(userWithPlaintextPassword);

        verify(passwordEncoder).encode(plaintextPassword);
        verify(mockUserRepository).save(any(User.class));
    }
}
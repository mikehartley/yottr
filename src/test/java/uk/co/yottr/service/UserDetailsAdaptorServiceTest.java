package uk.co.yottr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.security.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class UserDetailsAdaptorServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    public void testLoadUserByUsername() throws Exception {
        final String username = "testUser";
        final String password = "testPassword";
        final boolean enabled = true;
        final Role firstRole = Role.CREW;
        final Role secondRole = Role.FREE;

        final User yottrUser = new User();
        yottrUser.setUsername(username);
        yottrUser.setPassword(password);
        yottrUser.setEnabled(enabled);
        yottrUser.addRole(firstRole);
        yottrUser.addRole(secondRole);

        when(userRepository.findByUsername(username)).thenReturn(yottrUser);

        final UserDetailsAdaptorService userDetailsAdaptorService = new UserDetailsAdaptorService(userRepository);
        final UserDetails springUserDetails = userDetailsAdaptorService.loadUserByUsername(username);

        assertEquals(username, springUserDetails.getUsername(), "username");
        assertEquals(password, springUserDetails.getPassword(), "password");
        assertEquals(enabled, springUserDetails.isEnabled(), "isEnabled");
        assertTrue(springUserDetails.isAccountNonExpired(), "isAccountNonExpired should always be true");
        assertTrue(springUserDetails.isCredentialsNonExpired(), "isCredentialsNonExpired should always be true");
        assertTrue(springUserDetails.isAccountNonLocked(), "isAccountNonLocked should always be true");

        final List<GrantedAuthority> grantedAuthoritiesAsList = new ArrayList<>(springUserDetails.getAuthorities());
        Collections.sort(grantedAuthoritiesAsList, new Comparator<GrantedAuthority>() {
            @Override
            public int compare(GrantedAuthority grantedAuthority1, GrantedAuthority grantedAuthority2) {
                return grantedAuthority1.getAuthority().compareTo(grantedAuthority2.getAuthority());
            }
        });
        assertEquals(2, grantedAuthoritiesAsList.size(), "authorities size");
        assertEquals(firstRole.name(), grantedAuthoritiesAsList.get(0).getAuthority(), "first authority");
        assertEquals(secondRole.name(), grantedAuthoritiesAsList.get(1).getAuthority(), "second authority");
    }

    @Test
    public void whenUserNotFound() throws Exception {
        assertThrows(UsernameNotFoundException.class, () -> {
            final UserDetailsAdaptorService userDetailsAdaptorService = new UserDetailsAdaptorService(userRepository);
            userDetailsAdaptorService.loadUserByUsername("zippy");
        });
    }
}
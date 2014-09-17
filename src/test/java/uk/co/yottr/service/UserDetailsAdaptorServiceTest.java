package uk.co.yottr.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uk.co.yottr.model.User;
import uk.co.yottr.model.UserRole;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.security.Role;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(MockitoJUnitRunner.class)
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
        final Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(null, firstRole));
        userRoles.add(new UserRole(null, secondRole));

        final User yottrUser = new User();
        yottrUser.setUsername(username);
        yottrUser.setPassword(password);
        yottrUser.setEnabled(enabled);
        yottrUser.setUserRoles(userRoles);

        when(userRepository.findByUsername(username)).thenReturn(yottrUser);

        final UserDetailsAdaptorService userDetailsAdaptorService = new UserDetailsAdaptorService(userRepository);
        final UserDetails springUserDetails = userDetailsAdaptorService.loadUserByUsername(username);

        assertEquals("username", username, springUserDetails.getUsername());
        assertEquals("password", password, springUserDetails.getPassword());
        assertEquals("isEnabled", enabled, springUserDetails.isEnabled());
        assertTrue("isAccountNonExpired should always be true", springUserDetails.isAccountNonExpired());
        assertTrue("isCredentialsNonExpired should always be true", springUserDetails.isCredentialsNonExpired());
        assertTrue("isAccountNonLocked should always be true", springUserDetails.isAccountNonLocked());

        final List<GrantedAuthority> grantedAuthoritiesAsList = new ArrayList<>(springUserDetails.getAuthorities());
        Collections.sort(grantedAuthoritiesAsList, new Comparator<GrantedAuthority>() {
            @Override
            public int compare(GrantedAuthority grantedAuthority1, GrantedAuthority grantedAuthority2) {
                return grantedAuthority1.getAuthority().compareTo(grantedAuthority2.getAuthority());
            }
        });
        assertEquals("authorities size", 2, grantedAuthoritiesAsList.size());
        assertEquals("first authority", firstRole.name(), grantedAuthoritiesAsList.get(0).getAuthority());
        assertEquals("second authority", secondRole.name(), grantedAuthoritiesAsList.get(1).getAuthority());
    }
}
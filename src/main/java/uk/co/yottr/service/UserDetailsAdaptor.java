package uk.co.yottr.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uk.co.yottr.model.User;
import uk.co.yottr.model.UserRole;

import java.util.ArrayList;
import java.util.Collection;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class UserDetailsAdaptor {

    public static UserDetails toUserDetails(User user) {
        return adaptUserForAuthentication(user);
    }

    private static org.springframework.security.core.userdetails.User adaptUserForAuthentication(uk.co.yottr.model.User user) {
        Collection<GrantedAuthority> authorities = buildUserAuthority(user.getUserRoles());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.isEnabled(), true, true, true, authorities);
    }

    private static Collection<GrantedAuthority> buildUserAuthority(Collection<UserRole> userRoles) {

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (UserRole userRole : userRoles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRole().name()));
        }

        return grantedAuthorities;
    }
}

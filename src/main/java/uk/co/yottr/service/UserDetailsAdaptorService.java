package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.model.UserRole;
import uk.co.yottr.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Service("userDetailsService")
public class UserDetailsAdaptorService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public UserDetailsAdaptorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        return adaptUserForAuthentication(userRepository.findByUsername(username));
    }

    // Converts uk.co.yottr.model.User to org.springframework.security.core.userdetails.User
    private User adaptUserForAuthentication(uk.co.yottr.model.User user) {
        Collection<GrantedAuthority> authorities = buildUserAuthority(user.getUserRoles());
        return new User(user.getUsername(), user.getPassword(), user.isEnabled(), true, true, true, authorities);
    }

    private Collection<GrantedAuthority> buildUserAuthority(Collection<UserRole> userRoles) {

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (UserRole userRole : userRoles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRole()));
        }

        return grantedAuthorities;
    }
}

package uk.co.yottr.security.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.co.yottr.model.User;
import uk.co.yottr.tempDatastore.Database;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class AppUserDetailsServiceDAO implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserDetailsServiceDAO.class);

    private final Database database = new Database();

    @Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		
		LOG.info("loadUserByUsername username="+username);


        for (User user : database.getUsers().values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        LOG.info("User not found: " + username);
        throw new UsernameNotFoundException(username + " not found");
	}
}

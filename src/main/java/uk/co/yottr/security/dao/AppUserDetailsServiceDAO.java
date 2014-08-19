package uk.co.yottr.security.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.co.yottr.model.User;
import uk.co.yottr.tempDatastore.Database;

public class AppUserDetailsServiceDAO implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserDetailsServiceDAO.class);

    private final Database database = new Database();

    @Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		
		LOG.info("loadUserByUsername username="+username);

        if (!database.getUsers().containsKey(username)) {
			throw new UsernameNotFoundException(username + " not found");
		}
		
		return database.getUsers().get(username);
	}
}

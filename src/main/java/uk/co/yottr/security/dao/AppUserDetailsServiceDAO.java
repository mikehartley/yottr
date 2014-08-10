package uk.co.yottr.security.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AppUserDetailsServiceDAO implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserDetailsServiceDAO.class);

    private static final String ROLE_ADMIN = "Admin";

    @Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		
		LOG.info("loadUserByUsername username="+username);
		
		if(!username.equals("mike")) {
			throw new UsernameNotFoundException(username + " not found");
		}
		
		//creating dummy user details, should do JDBC operations
		return new UserDetails() {
			
			private static final long serialVersionUID = 2059202961588104658L;

			@Override
			public boolean isEnabled() {
				return true;
			}
			
			@Override
			public boolean isCredentialsNonExpired() {
				return true;
			}
			
			@Override
			public boolean isAccountNonLocked() {
				return true;
			}
			
			@Override
			public boolean isAccountNonExpired() {
				return true;
			}
			
			@Override
			public String getUsername() {
				return username;
			}
			
			@Override
			public String getPassword() {
				return "mike";
			}
			
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
                authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
				return authorities;
			}
		};
	}

}

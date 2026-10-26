package uk.co.yottr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;

@Service("jpaUserDetailsService")
public class UserDetailsAdaptorService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsAdaptorService.class);

    private UserRepository userRepository;

    @Autowired
    public UserDetailsAdaptorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        LOG.info("loading user {} for sign-in", username);

        final User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return UserDetailsAdaptor.toUserDetails(user);
    }
}

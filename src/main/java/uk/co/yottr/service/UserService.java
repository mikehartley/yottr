package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.form.MemberSearch;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.repository.UserSpecifications;
import uk.co.yottr.security.MemberSessions;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private MemberSessions memberSessions;
    private TemporalProvider temporalProvider;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MemberSessions memberSessions,
                       TemporalProvider temporalProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.memberSessions = memberSessions;
        this.temporalProvider = temporalProvider;
    }

    /**
     * Saves a new member, joined today, with their password encoded.
     */
    @Transactional
    public User signUp(User user) {
        user.setJoined(temporalProvider.today());
        return save(user, true);
    }

    @Transactional
    public User save(User user, boolean encodePassword) {

        if (encodePassword) {
            final String plaintextPassword = user.getPassword();
            final String encodedPassword = passwordEncoder.encode(plaintextPassword);
            user.setPassword(encodedPassword);
        }

        return userRepository.save(user);
    }

    /**
     * A page of the members that match the search, with their listings loaded for the page to count.
     */
    @Transactional(readOnly = true)
    public Page<User> search(MemberSearch search, Pageable pageable) {
        final Page<User> users = userRepository.findAll(UserSpecifications.matching(search), pageable);
        users.forEach(user -> user.getBoatListings().size());
        return users;
    }

    /**
     * The user, with their listings loaded.
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        final Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        user.ifPresent(found -> found.getBoatListings().size());
        return user;
    }

    /**
     * The member, with their listings loaded, who must exist: one is missing only if their account was deleted
     * during the request, as deleting an account ends its sessions.
     *
     * @throws ResourceNotFoundException if there is no such member
     */
    @Transactional(readOnly = true)
    public User findMember(String username) {
        return findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("No member " + username));
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Enables a disabled account, or disables an enabled one and ends its sessions.
     */
    @Transactional
    public User flipEnabled(User user) {
        user.setEnabled(!user.isEnabled());
        final User saved = save(user, false);

        if (!saved.isEnabled()) {
            memberSessions.expire(saved.getUsername());
        }

        return saved;
    }

    /**
     * Deletes the account and its listings, and ends its sessions.
     */
    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
        memberSessions.expire(user.getUsername());
    }

    @Transactional
    public void delete(Long id) {
        userRepository.findById(id).ifPresent(this::delete);
    }

    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }
}

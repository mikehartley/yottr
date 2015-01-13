package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        final User user = userRepository.findByUsername(username);

        if (user != null) {
            user.getBoatListings().size();
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findOne(id);
    }

    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.exists(id);
    }
}

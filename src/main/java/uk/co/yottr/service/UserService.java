package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
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

    public User save(User user) {
        final String plaintextPassword = user.getPassword();
        final String encodedPassword = passwordEncoder.encode(plaintextPassword);
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public List<User> findAll() {

        List<User> allUsers = new ArrayList<>();

        for (User user : userRepository.findAll()) {
            allUsers.add(user);
        }

        return allUsers;
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findOne(id);
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }

    public boolean userExists(Long id) {
        return userRepository.exists(id);
    }
}

package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public List<User> findAll() {

        List<User> allUsers = new ArrayList<>();

        for (User user : userRepository.findAll()) {
            allUsers.add(user);
        }

        return allUsers;
    }
}

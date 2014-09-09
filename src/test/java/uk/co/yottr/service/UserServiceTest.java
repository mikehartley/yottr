package uk.co.yottr.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.model.User;
import uk.co.yottr.model.UserRole;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring-context-test.xml")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void canCreateUpdateAndDelete() throws Exception {

        final String username = "user"  + rnd(12);

        User user = createUser(username);

        final User savedUser = userService.save(user);

        final List<User> allUsers = userService.findAll();

        final int indexOfSavedUser = allUsers.lastIndexOf(savedUser);
        Assert.assertTrue("user did not appear in find all", indexOfSavedUser > -1);

        final User userFromFindAll = allUsers.get(indexOfSavedUser);
        Assert.assertEquals("username", username, userFromFindAll.getUsername());

        userService.delete(savedUser);
        Assert.assertFalse("find all should not contain the deleted user", userService.findAll().contains(savedUser));
    }

    private User createUser(String username) {
        User user = new User();

        user.setUsername(username);
        user.setPassword("password" + rnd(6));
        user.setTitle("Mr");
        user.setFirstName("Izzy");
        user.setLastName("Wizzy");
        user.setEmail("izzy@wizzy.test");
        user.setMobile(rnd(5) + " " + rnd(6));
        user.setCountry(User.Country.UK);
        user.setPostcode("W8 4QT");
        user.setAboutMe("This is a bunch of text followed by some random numbers " + rnd(50));
        user.setUserRole(new HashSet<UserRole>());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        return user;
    }

    private String rnd(int numberOfRandomChars) {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < numberOfRandomChars; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }
}
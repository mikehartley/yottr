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
import java.util.Set;

import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring-context-test.xml")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    public void canCreateFindUpdateAndDelete() throws Exception {

        final String username = "user"  + randomAlphanumeric(12);

        User user = createUser(username);

        final User savedUser = userService.save(user);

        final User foundUser = userService.findByUsername(username);

        assertEquals("findByUsername", savedUser, foundUser);

        userService.delete(savedUser);
        assertFalse("find all should not contain the deleted user", userService.findAll().contains(savedUser));
    }

    @Test
    public void whenUsernameNotFound() throws Exception {
        final User result = userService.findByUsername("garbage");
        Assert.assertNull("result of not found should be null", result);
    }

    @Test
    public void userWithRoles() throws Exception {

        User user = createUser(randomAlphanumeric(10));
        Set<UserRole> userRoles = new HashSet<>();
        final UserRole userRole = new UserRole(user, "rolex");
        userRoles.add(userRole);
        user.setUserRoles(userRoles);

        final User savedUser = userService.save(user);
        final List<User> allUsers = userService.findAll();
        final User userFromFindAll = allUsers.get(allUsers.lastIndexOf(savedUser));

        assertFalse("roles should not be empty", userFromFindAll.getUserRoles().isEmpty());
        final UserRole savedRole = userFromFindAll.getUserRoles().iterator().next();
        assertEquals("user from saved role", userRole.getUser(), savedRole.getUser());
        assertEquals("role from saved role", userRole.getRole(), savedRole.getRole());
    }

    @Test
    public void allFieldsPersist() throws Exception {
        final String username = randomAlphanumeric(20);
        final String password = "password" + randomAlphanumeric(22);
        final String title = randomAlphabetic(10);
        final String firstName = randomAlphabetic(20);
        final String lastName = randomAlphabetic(20);
        final String email = randomAlphabetic(10) + "@" + randomAlphabetic(10) + "." + randomAlphabetic(10);
        final String mobile = randomNumeric(5) + " " + randomNumeric(6);
        final User.Country country = new Random().nextBoolean() ? User.Country.UK : User.Country.OTHER;
        final String postcode = randomAlphabetic(1) + randomNumeric(1) + " " + randomNumeric(1) + randomAlphabetic(2);
        final String aboutMe = "This is a bunch of text followed by some random characters " + randomAlphanumeric(50);
        final boolean enabled = true;

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setTitle(title);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setCountry(country);
        user.setPostcode(postcode);
        user.setAboutMe(aboutMe);
        user.setEnabled(enabled);

        final User savedUser = userService.save(user);

        assertEquals("username", username, savedUser.getUsername());
        assertEquals("password", password, savedUser.getPassword());
        assertEquals("title", title, savedUser.getTitle());
        assertEquals("firstName", firstName, savedUser.getFirstName());
        assertEquals("lastName", lastName, savedUser.getLastName());
        assertEquals("email", email, savedUser.getEmail());
        assertEquals("mobile", mobile, savedUser.getMobile());
        assertEquals("country", country, savedUser.getCountry());
        assertEquals("postcode", postcode, savedUser.getPostcode());
        assertEquals("aboutMe", aboutMe, savedUser.getAboutMe());
        assertEquals("enabled", enabled, savedUser.isEnabled());
    }

    private User createUser(String username) {
        User user = new User();

        user.setUsername(username);
        user.setPassword("password" + randomAlphanumeric(6));
        user.setTitle("Mr");
        user.setFirstName("Izzy");
        user.setLastName("Wizzy");
        user.setEmail("izzy@wizzy.test");
        user.setMobile(randomNumeric(5) + " " + randomNumeric(6));
        user.setCountry(User.Country.UK);
        user.setPostcode("W8 4QT");
        user.setAboutMe("This is a bunch of text followed by some random characters " + randomAlphanumeric(50));
        user.setUserRoles(new HashSet<UserRole>());
        user.setEnabled(true);

        return user;
    }


}
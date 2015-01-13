package uk.co.yottr.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;
import uk.co.yottr.model.UserRole;
import uk.co.yottr.security.Role;
import uk.co.yottr.testconfig.IntegrationTestConfig;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.junit.Assert.*;
import static uk.co.yottr.builder.UserBuilder.aUser;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationTestConfig.class)
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void canCreateFindByUsernameAndDelete() throws Exception {

        final String username = "user"  + randomAlphanumeric(12);

        final User user = aUser().withUsername(username).build();

        final User savedUser = userService.save(user, true);

        final User foundUser = userService.findByUsername(username);

        assertEquals("findByUsername user.getId()", savedUser.getId(), foundUser.getId());

        userService.delete(savedUser);

        assertNull("find by username should not contain the deleted user", userService.findByUsername(username));
    }

    @Test
    public void whenUsernameNotFound() throws Exception {
        final User result = userService.findByUsername("garbage");
        Assert.assertNull("result of not found should be null", result);
    }

    @Test
    public void userWithRoles() throws Exception {

        final Role expectedRole = Role.CREW;
        final User user = aUser().withUsername(randomAlphanumeric(10)).build();
        user.addRole(expectedRole);

        final User savedUser = userService.save(user, true);

        assertFalse("roles should not be empty", savedUser.getUserRoles().isEmpty());

        final UserRole savedRole = savedUser.getUserRoles().iterator().next();
        assertEquals("role from saved role", expectedRole, savedRole.getRole());
    }

    @Test
    public void findById() throws Exception {

        final String username = randomAlphanumeric(10);
        final User user = aUser().withUsername(username).build();
        final User savedUser = userService.save(user, true);

        final User foundUser = userService.findById(savedUser.getId());

        assertEquals("username", username, foundUser.getUsername());
    }

    @Test
    public void deleteByIdAndUserExists() throws Exception {

        final User savedUser = userService.save(aUser().withUsername(randomAlphanumeric(10)).build(), true);

        final boolean userExistsBeforeDelete = userService.userExists(savedUser.getId());
        assertTrue("user should exist before delete", userExistsBeforeDelete);

        userService.delete(savedUser.getId());

        final boolean userExistsAfterDelete = userService.userExists(savedUser.getId());
        assertFalse("user should not exist after delete", userExistsAfterDelete);
    }

    @Test
    public void deleteByUser() throws Exception {

        final User savedUser = userService.save(aUser().withUsername(randomAlphanumeric(10)).build(), true);

        final boolean userExistsBeforeDelete = userService.userExists(savedUser.getId());
        assertTrue("user should exist before delete", userExistsBeforeDelete);

        userService.delete(savedUser);

        final boolean userExistsAfterDelete = userService.userExists(savedUser.getId());
        assertFalse("user should not exist after delete", userExistsAfterDelete);
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
        final Country country = new Random().nextBoolean() ? Country.UK : Country.OTHER;
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

        final User savedUser = userService.save(user, true);

        assertEquals("username", username, savedUser.getUsername());
        assertTrue("password matches", passwordEncoder.matches(password, savedUser.getPassword()));
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
}
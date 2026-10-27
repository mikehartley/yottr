package uk.co.yottr.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;
import uk.co.yottr.model.UserRole;
import uk.co.yottr.security.Role;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.yottr.builder.UserBuilder.aUser;

@SpringBootTest
@Import(TestcontainersConfig.class)
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void canCreateFindByUsernameAndDelete() throws Exception {

        final String username = "user"  + secure().nextAlphanumeric(12);

        final User user = aUser().withUsername(username).build();

        final User savedUser = userService.save(user, true);

        final User foundUser = userService.findByUsername(username).orElseThrow();

        assertEquals(savedUser.getId(), foundUser.getId(), "findByUsername user.getId()");

        userService.delete(savedUser);

        assertTrue(userService.findByUsername(username).isEmpty(), "find by username should not contain the deleted user");
    }

    @Test
    public void whenUsernameNotFound() throws Exception {
        assertTrue(userService.findByUsername("garbage").isEmpty(), "a username that isn't there is not found");
    }

    @Test
    public void userWithRoles() throws Exception {

        final Role expectedRole = Role.CREW;
        final User user = aUser().withUsername(secure().nextAlphanumeric(10)).build();
        user.addRole(expectedRole);

        final User savedUser = userService.save(user, true);

        assertFalse(savedUser.getUserRoles().isEmpty(), "roles should not be empty");

        final UserRole savedRole = savedUser.getUserRoles().iterator().next();
        assertEquals(expectedRole, savedRole.getRole(), "role from saved role");
    }

    @Test
    public void findById() throws Exception {

        final String username = secure().nextAlphanumeric(10);
        final User user = aUser().withUsername(username).build();
        final User savedUser = userService.save(user, true);

        final User foundUser = userService.findById(savedUser.getId()).orElseThrow();

        assertEquals(username, foundUser.getUsername(), "username");
    }

    @Test
    public void deleteByIdAndUserExists() throws Exception {

        final User savedUser = userService.save(aUser().withUsername(secure().nextAlphanumeric(10)).build(), true);

        final boolean userExistsBeforeDelete = userService.userExists(savedUser.getId());
        assertTrue(userExistsBeforeDelete, "user should exist before delete");

        userService.delete(savedUser.getId());

        final boolean userExistsAfterDelete = userService.userExists(savedUser.getId());
        assertFalse(userExistsAfterDelete, "user should not exist after delete");
    }

    @Test
    public void deleteByUser() throws Exception {

        final User savedUser = userService.save(aUser().withUsername(secure().nextAlphanumeric(10)).build(), true);

        final boolean userExistsBeforeDelete = userService.userExists(savedUser.getId());
        assertTrue(userExistsBeforeDelete, "user should exist before delete");

        userService.delete(savedUser);

        final boolean userExistsAfterDelete = userService.userExists(savedUser.getId());
        assertFalse(userExistsAfterDelete, "user should not exist after delete");
    }

    @Test
    public void allFieldsPersist() throws Exception {
        final String username = secure().nextAlphanumeric(20);
        final String password = "password" + secure().nextAlphanumeric(22);
        final String title = secure().nextAlphabetic(10);
        final String firstName = secure().nextAlphabetic(20);
        final String lastName = secure().nextAlphabetic(20);
        final String email = secure().nextAlphabetic(10) + "@" + secure().nextAlphabetic(10) + "." + secure().nextAlphabetic(10);
        final String mobile = secure().nextNumeric(5) + " " + secure().nextNumeric(6);
        final Country country = new Random().nextBoolean() ? Country.UK : Country.OTHER;
        final String postcode = secure().nextAlphabetic(1) + secure().nextNumeric(1) + " " + secure().nextNumeric(1) + secure().nextAlphabetic(2);
        final String aboutMe = "This is a bunch of text followed by some random characters " + secure().nextAlphanumeric(50);
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

        assertEquals(username, savedUser.getUsername(), "username");
        assertTrue(passwordEncoder.matches(password, savedUser.getPassword()), "password matches");
        assertEquals(title, savedUser.getTitle(), "title");
        assertEquals(firstName, savedUser.getFirstName(), "firstName");
        assertEquals(lastName, savedUser.getLastName(), "lastName");
        assertEquals(email, savedUser.getEmail(), "email");
        assertEquals(mobile, savedUser.getMobile(), "mobile");
        assertEquals(country, savedUser.getCountry(), "country");
        assertEquals(postcode, savedUser.getPostcode(), "postcode");
        assertEquals(aboutMe, savedUser.getAboutMe(), "aboutMe");
        assertEquals(enabled, savedUser.isEnabled(), "enabled");
    }
}
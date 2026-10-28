package uk.co.yottr.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import uk.co.yottr.form.MemberSearch;
import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;
import uk.co.yottr.model.UserRole;
import uk.co.yottr.security.Role;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

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

    @Test
    public void searchFindsTextInTheUsernameNameOrEmailIgnoringCase() {
        final String token = secure().nextAlphabetic(12).toLowerCase(Locale.ROOT);
        final User byUsername = saveMember("user" + token, u -> { });
        final User byName = saveMember(secure().nextAlphanumeric(12), u -> u.setLastName("Mc" + token));
        final User byEmail = saveMember(secure().nextAlphanumeric(12), u -> u.setEmail(token + "@example.test"));
        saveMember(secure().nextAlphanumeric(12), u -> { });

        assertEquals(Set.of(byUsername.getUsername(), byName.getUsername(), byEmail.getUsername()),
                usernames(search(s -> s.setText(token.toUpperCase(Locale.ROOT)))));
    }

    @Test
    public void searchFindsAFullName() {
        final String token = secure().nextAlphabetic(12);
        final User member = saveMember(secure().nextAlphanumeric(12), u -> {
            u.setFirstName("Ann");
            u.setLastName(token);
        });

        assertEquals(Set.of(member.getUsername()), usernames(search(s -> s.setText("ann " + token))));
    }

    @Test
    public void searchNarrowsByTheDayJoinedIncludingTheEnds() {
        // a year no other test uses, so the dates alone pick out these members
        final User january = saveMember("joined" + secure().nextAlphanumeric(10), u -> u.setJoined(LocalDate.of(1999, 1, 1)));
        final User june = saveMember("joined" + secure().nextAlphanumeric(10), u -> u.setJoined(LocalDate.of(1999, 6, 15)));
        final User december = saveMember("joined" + secure().nextAlphanumeric(10), u -> u.setJoined(LocalDate.of(1999, 12, 31)));

        final Consumer<MemberSearch> in1999 = s -> {
            s.setJoinedFrom(LocalDate.of(1999, 1, 1));
            s.setJoinedTo(LocalDate.of(1999, 12, 31));
        };
        assertEquals(Set.of(january.getUsername(), june.getUsername(), december.getUsername()), usernames(search(in1999)));
        assertEquals(Set.of(june.getUsername(), december.getUsername()),
                usernames(search(in1999.andThen(s -> s.setJoinedFrom(LocalDate.of(1999, 6, 15))))));
        assertEquals(Set.of(june.getUsername()), usernames(search(s -> {
            s.setJoinedFrom(LocalDate.of(1999, 2, 1));
            s.setJoinedTo(LocalDate.of(1999, 6, 15));
        })));
    }

    @Test
    public void signUpRecordsTheDayJoined() {
        final User member = userService.signUp(aUser().withUsername(secure().nextAlphanumeric(12)).build());

        assertEquals(LocalDate.now(), userService.findById(member.getId()).orElseThrow().getJoined());
    }

    private User saveMember(String username, Consumer<User> details) {
        final User user = aUser().withUsername(username).build();
        details.accept(user);
        return userService.save(user, true);
    }

    private Set<String> usernames(Iterable<User> users) {
        final Set<String> usernames = new HashSet<>();
        users.forEach(user -> usernames.add(user.getUsername()));
        return usernames;
    }

    private Iterable<User> search(Consumer<MemberSearch> criteria) {
        final MemberSearch search = new MemberSearch();
        criteria.accept(search);
        return userService.search(search, PageRequest.of(0, 30));
    }
}

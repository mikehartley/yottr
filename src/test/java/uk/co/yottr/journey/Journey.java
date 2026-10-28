package uk.co.yottr.journey;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.model.User;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static uk.co.yottr.builder.UserBuilder.aUser;

/**
 * The site as people use it: the whole application on a random port, against Postgres in Docker, driven through
 * its pages by a browser for each person. A journey checks what the lower-level tests can't see on their own, such
 * as sessions and what one person's actions mean for another. The rules themselves are tested further down.
 * <p>
 * Journeys share the database, so each person gets a unique username.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestcontainersConfig.class)
abstract class Journey {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private UserService userService;

    private final List<Person> people = new ArrayList<>();

    /**
     * Someone who hasn't joined yet.
     */
    Person visitor(String name) {
        return person(name, uniqueUsername(name));
    }

    Person member(String name) {
        return member(name, 2);
    }

    Person member(String name, int maxListings) {
        return join(name, maxListings, Role.FREE);
    }

    Person administrator(String name) {
        return join(name, 2, Role.FREE, Role.ADMIN);
    }

    private Person join(String name, int maxListings, Role... roles) {
        final String username = uniqueUsername(name);
        final User user = aUser().withUsername(username).withMaxListings(maxListings).build();
        user.setPassword(Person.PASSWORD);
        user.setFirstName(name);
        user.setLastName("Sailor");
        user.setEmail(username + "@example.test");
        user.setMobile("07700 900123");
        user.setPostcode("PO1 1AA");
        for (Role role : roles) {
            user.addRole(role);
        }
        userService.signUp(user);
        return person(name, username);
    }

    private Person person(String name, String username) {
        final Person person = new Person(name, username, "http://localhost:" + port);
        people.add(person);
        return person;
    }

    private static String uniqueUsername(String name) {
        return name + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @AfterEach
    void closeBrowsers() {
        people.forEach(Person::closeBrowser);
    }
}

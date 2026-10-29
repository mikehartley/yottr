package uk.co.yottr.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;
import uk.co.yottr.model.UserRole;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Accounts for trying the site out, which the home page signs in as with one click. They're on only with the
 * test-users profile, which reads them from test-users.properties in the project root (not committed); without it
 * there are none, wherever else the properties might be.
 * <p>
 * The file is all there is to them: when the site starts, each that doesn't exist is made, with the TESTER role as
 * well as its own, and each that does has its password set and any roles it's missing added. A name that belongs
 * to an account without the TESTER role, a real member's, is left alone and not offered.
 */
@Component
public class TestUsers {

    public static final String PROFILE = "test-users";

    private static final Logger LOG = LoggerFactory.getLogger(TestUsers.class);

    /**
     * @param roles the roles besides TESTER; FREE, as every member has, if none are given
     */
    public record TestUser(String username, String password, String description, List<Role> roles) {
        public TestUser {
            roles = roles == null || roles.isEmpty() ? List.of(Role.FREE) : List.copyOf(roles);
        }
    }

    private final Environment environment;
    private final List<TestUser> configured;
    private final UserService userService;
    private List<TestUser> offered = List.of();

    public TestUsers(Environment environment, UserService userService) {
        this.environment = environment;
        this.configured = environment.acceptsProfiles(Profiles.of(PROFILE))
                ? Binder.get(environment).bind("yottr.test-users", Bindable.listOf(TestUser.class)).orElse(List.of())
                : List.of();
        this.userService = userService;
    }

    /**
     * The test users the home page offers: those set up when the site started.
     */
    public List<TestUser> all() {
        return offered;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setUp() {
        if (configured.isEmpty() && environment.acceptsProfiles(Profiles.of(PROFILE))) {
            LOG.warn("the {} profile is on, but test-users.properties lists no test users", PROFILE);
        }
        offered = configured.stream().filter(this::setUp).toList();
    }

    private boolean setUp(TestUser testUser) {
        final Optional<User> existing = userService.findByUsername(testUser.username());
        if (existing.isEmpty()) {
            final User user = new User();
            user.setUsername(testUser.username());
            user.setPassword(testUser.password());
            user.setFirstName(testUser.username());
            user.setLastName("Tester");
            user.setEmail(testUser.username() + "@example.test");
            user.setMobile("07700 900000");
            user.setCountry(Country.UK);
            user.setPostcode("PO1 1AA");
            user.setEnabled(true);
            user.addRole(Role.TESTER);
            testUser.roles().forEach(user::addRole);
            userService.signUp(user);
            LOG.info("made test user {}", testUser.username());
            return true;
        }

        final User user = existing.get();
        final List<Role> roles = new ArrayList<>(user.getUserRoles().stream().map(UserRole::getRole).toList());
        if (!roles.contains(Role.TESTER)) {
            LOG.warn("test user {} is a member's account without the TESTER role, so it's left alone and not offered", testUser.username());
            return false;
        }
        testUser.roles().stream().filter(role -> !roles.contains(role)).forEach(user::addRole);
        user.setPassword(testUser.password());
        userService.save(user, true);
        return true;
    }
}

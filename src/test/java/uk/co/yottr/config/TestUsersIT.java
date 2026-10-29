package uk.co.yottr.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.yottr.model.User;
import uk.co.yottr.model.UserRole;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.yottr.builder.UserBuilder.aUser;

/**
 * The test users, with the test-users profile on. SecurityIT checks that none are offered without it.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
@ActiveProfiles(TestUsers.PROFILE)
@TestPropertySource(properties = {
        "yottr.test-users[0].username=" + TestUsersIT.USERNAME,
        "yottr.test-users[0].password=" + TestUsersIT.PASSWORD,
        "yottr.test-users[0].description=Administrator, for the one-click test",
        "yottr.test-users[0].roles=FREE,ADMIN"})
public class TestUsersIT {

    static final String USERNAME = "oneclick";
    static final String PASSWORD = "a password for one click";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    public void aTestUserIsMadeWhenTheSiteStartsAndSignedInWithOneClick() throws Exception {
        assertEquals(Set.of(Role.FREE, Role.ADMIN, Role.TESTER), rolesOf(USERNAME));

        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"test-users\"")))
                .andExpect(content().string(containsString("<input type=\"hidden\" name=\"username\" value=\"" + USERNAME + "\"/>")))
                .andExpect(content().string(containsString("<input type=\"hidden\" name=\"password\" value=\"" + PASSWORD + "\"/>")))
                .andExpect(content().string(containsString("Administrator, for the one-click test")));

        // what the button sends
        mockMvc.perform(post("/index").param("username", USERNAME).param("password", PASSWORD).with(csrf()))
                .andExpect(authenticated().withUsername(USERNAME));
    }

    @Test
    public void aTestUsersPasswordFollowsTheFileAndRolesAreAdded() throws Exception {
        final String username = "tester" + secure().nextAlphanumeric(8);
        testUsers(true, username, "the first password", "FREE").setUp();

        final TestUsers changed = testUsers(true, username, "the second password", "FREE,CREW");
        changed.setUp();

        assertEquals(List.of(username), changed.all().stream().map(TestUsers.TestUser::username).toList());
        assertEquals(Set.of(Role.FREE, Role.CREW, Role.TESTER), rolesOf(username));
        mockMvc.perform(formLogin("/index").user(username).password("the second password")).andExpect(authenticated());
        mockMvc.perform(formLogin("/index").user(username).password("the first password")).andExpect(unauthenticated());
    }

    @Test
    public void aMembersOwnAccountIsNeverChangedOrOffered() throws Exception {
        final User member = aUser().withUsername("member" + secure().nextAlphanumeric(8)).build();
        member.setPassword("the member's own password");
        member.addRole(Role.FREE);
        userService.save(member, true);

        final TestUsers testUsers = testUsers(true, member.getUsername(), "a test password", "FREE,ADMIN");
        testUsers.setUp();

        assertTrue(testUsers.all().isEmpty());
        assertEquals(Set.of(Role.FREE), rolesOf(member.getUsername()));
        mockMvc.perform(formLogin("/index").user(member.getUsername()).password("the member's own password")).andExpect(authenticated());
    }

    @Test
    public void withoutTheProfileNoneAreMade() {
        final String username = "tester" + secure().nextAlphanumeric(8);

        final TestUsers testUsers = testUsers(false, username, "a test password", "FREE");
        testUsers.setUp();

        assertTrue(testUsers.all().isEmpty());
        assertTrue(userService.findByUsername(username).isEmpty());
    }

    private TestUsers testUsers(boolean profile, String username, String password, String roles) {
        final MockEnvironment environment = new MockEnvironment()
                .withProperty("yottr.test-users[0].username", username)
                .withProperty("yottr.test-users[0].password", password)
                .withProperty("yottr.test-users[0].description", "A test user")
                .withProperty("yottr.test-users[0].roles", roles);
        if (profile) {
            environment.setActiveProfiles(TestUsers.PROFILE);
        }
        return new TestUsers(environment, userService);
    }

    private Set<Role> rolesOf(String username) {
        return userService.findByUsername(username).orElseThrow().getUserRoles().stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());
    }
}

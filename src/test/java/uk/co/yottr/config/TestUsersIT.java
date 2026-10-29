package uk.co.yottr.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.TestcontainersConfig;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.yottr.builder.UserBuilder.aUser;

/**
 * Signing in as a test user with one click, when some are configured. SecurityIT checks that none show otherwise.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
@TestPropertySource(properties = {
        "yottr.test-users[0].username=" + TestUsersIT.USERNAME,
        "yottr.test-users[0].password=" + TestUsersIT.PASSWORD,
        "yottr.test-users[0].description=Crew, for the one-click test"})
public class TestUsersIT {

    static final String USERNAME = "oneclick";
    static final String PASSWORD = "a password for one click";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void aTestUserIsSignedInWithTheDetailsOnTheHomePage() throws Exception {
        if (userRepository.findByUsername(USERNAME) == null) {
            final User member = aUser().withUsername(USERNAME).build();
            member.setPassword(PASSWORD);
            member.addRole(Role.FREE);
            userService.save(member, true);
        }

        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"test-users\"")))
                .andExpect(content().string(containsString("<input type=\"hidden\" name=\"username\" value=\"" + USERNAME + "\"/>")))
                .andExpect(content().string(containsString("<input type=\"hidden\" name=\"password\" value=\"" + PASSWORD + "\"/>")))
                .andExpect(content().string(containsString("Crew, for the one-click test")));

        // what the button sends
        mockMvc.perform(post("/index").param("username", USERNAME).param("password", PASSWORD).with(csrf()))
                .andExpect(authenticated().withUsername(USERNAME));
    }
}

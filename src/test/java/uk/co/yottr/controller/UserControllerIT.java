package uk.co.yottr.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import uk.co.yottr.model.User;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.TestcontainersConfig;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.co.yottr.builder.UserBuilder.aUser;

/**
 * The user forms against a real database, including fields that are not on the forms but that a request
 * can still send. They must not reach the stored user.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private static String unique(String prefix) {
        return prefix + secure().nextAlphanumeric(10);
    }

    private static MockHttpServletRequestBuilder signup(String username) {
        return post("/signup").contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf())
                .param("username", username)
                .param("password", "new-members-password")
                .param("firstName", "Newly")
                .param("lastName", "Joined")
                .param("email", "new@example.test")
                .param("mobile", "07700 900123")
                .param("country", "UK")
                .param("postcode", "PO1 1AA");
    }

    @Test
    public void signupCannotOverwriteAnExistingAccount() throws Exception {
        final String victimUsername = unique("victim");
        final User victim = userService.save(aUser().withUsername(victimUsername).build(), true);
        final String newUsername = unique("newcomer");

        mockMvc.perform(signup(newUsername).param("id", String.valueOf(victim.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("signupSuccess"));

        final User victimAfterwards = userService.findById(victim.getId());
        assertEquals(victimUsername, victimAfterwards.getUsername(), "the existing account's username");
        assertEquals(victim.getPassword(), victimAfterwards.getPassword(), "the existing account's password");

        final User newcomer = userService.findByUsername(newUsername);
        assertNotNull(newcomer, "the new member is saved as a new account");
        assertNotEquals(victim.getId(), newcomer.getId());
    }

    @Test
    public void signupCannotSetFieldsThatAreNotOnTheForm() throws Exception {
        final String username = unique("greedy");

        mockMvc.perform(signup(username)
                        .param("maxListings", "1000")
                        .param("enabled", "false"))
                .andExpect(view().name("signupSuccess"));

        final User saved = userService.findByUsername(username);
        assertEquals(Integer.valueOf(2), saved.getMaxListings(), "listing limit");
        assertEquals(true, saved.isEnabled(), "enabled");
    }

    @Test
    public void myDetailsCannotChangeTheUsernameOrListingLimit() throws Exception {
        final String username = unique("member");
        userService.save(aUser().withUsername(username).build(), true);

        mockMvc.perform(post("/s/myDetails").contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf())
                        .with(user(username).authorities(new SimpleGrantedAuthority(Role.FREE.name())))
                        .param("username", unique("renamed"))
                        .param("maxListings", "1000")
                        .param("firstName", "Changed")
                        .param("lastName", "Name")
                        .param("email", "changed@example.test")
                        .param("mobile", "07700 900456")
                        .param("country", "UK")
                        .param("postcode", "PO1 1AA"))
                .andExpect(redirectedUrl("myDetails?updated"));

        final User saved = userService.findByUsername(username);
        assertNotNull(saved, "still found by the original username");
        assertEquals("Changed", saved.getFirstName(), "the change that is on the form");
        assertEquals(Integer.valueOf(2), saved.getMaxListings(), "listing limit");
    }
}

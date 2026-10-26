package uk.co.yottr.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.TestcontainersConfig;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.yottr.builder.UserBuilder.aUser;

/**
 * The security rules, through the real filter chain.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
public class SecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private static final SimpleGrantedAuthority ADMIN = new SimpleGrantedAuthority(Role.ADMIN.name());
    private static final SimpleGrantedAuthority FREE = new SimpleGrantedAuthority(Role.FREE.name());

    @Test
    public void aPostWithoutTheCsrfTokenIsRefused() throws Exception {
        mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("username", "someone"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void aPostWithTheCsrfTokenIsAccepted() throws Exception {
        mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }

    @Test
    public void changesCannotBeMadeWithAGet() throws Exception {
        mockMvc.perform(get("/admin/user/1/delete").with(user("ada").authorities(ADMIN, FREE)))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(get("/admin/user/1/enabled/flip").with(user("ada").authorities(ADMIN, FREE)))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(get("/s/listings/ref/delete").with(user("alice").authorities(FREE)))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(get("/s/listings/ref/suspended/flip").with(user("alice").authorities(FREE)))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(get("/s/deleteMe").with(user("alice").authorities(FREE)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void logoutIsAPost() throws Exception {
        mockMvc.perform(post("/logout").with(user("alice").authorities(FREE)).with(csrf()))
                .andExpect(redirectedUrl("/index?logout"));
    }

    @Test
    public void pagesThatAreNotListedNeedASignedInMember() throws Exception {
        mockMvc.perform(get("/not-a-page"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }

    @Test
    public void publicPagesAreOpen() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/index")).andExpect(status().isOk());
        mockMvc.perform(get("/signup")).andExpect(status().isOk());
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    public void membersCannotOpenAdministratorsPages() throws Exception {
        mockMvc.perform(get("/admin/users").with(user("alice").authorities(FREE)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void theUserListDoesNotShowPasswordHashes() throws Exception {
        userService.save(aUser().withUsername("hashed" + secure().nextAlphanumeric(8)).build(), true);

        mockMvc.perform(get("/admin/users").param("size", "30").with(user("ada").authorities(ADMIN, FREE)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("$2a$"))));
    }
}

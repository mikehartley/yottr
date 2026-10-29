package uk.co.yottr.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.BlockedAddressService;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ListingPictureService;
import uk.co.yottr.service.PictureProcessor;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.TestPictures.jpeg;
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

    @Autowired
    private BoatService boatService;

    @Autowired
    private ListingPictureService listingPictureService;

    @Autowired
    private PictureProcessor pictureProcessor;

    @Autowired
    private BlockedAddressService blockedAddressService;

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
        mockMvc.perform(get("/s/listings/ref/crew/1/accept").with(user("alice").authorities(FREE)))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(get("/s/listings/ref/crew/leave").with(user("alice").authorities(FREE)))
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
        // and with no test users configured, none are offered (see TestUsersIT)
        mockMvc.perform(get("/index")).andExpect(status().isOk())
                .andExpect(content().string(containsString("main-sign-in-form")))
                .andExpect(content().string(not(containsString("test-users"))));
        mockMvc.perform(get("/signup")).andExpect(status().isOk());
        mockMvc.perform(get("/about")).andExpect(status().isOk()).andExpect(content().string(containsString("Useful links")));
        mockMvc.perform(get("/faq")).andExpect(status().isOk()).andExpect(content().string(containsString("Does it cost anything?")));
        mockMvc.perform(get("/terms")).andExpect(status().isOk()).andExpect(content().string(containsString("placeholder")));
        mockMvc.perform(get("/privacy")).andExpect(status().isOk()).andExpect(content().string(containsString("placeholder")));
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    public void membersCannotOpenAdministratorsPages() throws Exception {
        mockMvc.perform(get("/admin/users").with(user("alice").authorities(FREE)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/logging").with(user("alice").authorities(FREE)))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/admin/logging").param("name", "uk.co.yottr").param("level", "DEBUG")
                        .with(user("alice").authorities(FREE)).with(csrf()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/actuator/loggers").with(user("alice").authorities(FREE)))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/admin/listings/any/pictures/1/delete").with(user("alice").authorities(FREE)).with(csrf()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/blocked").with(user("alice").authorities(FREE)))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/admin/blocked").param("address", "203.0.113.0/24").with(user("alice").authorities(FREE)).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void aMembersBrowserMayKeepAPictureButNothingSharedMay() throws Exception {
        // Spring Security says not to cache anything, unless the response has said otherwise
        final User owner = userService.save(aUser().withUsername("owner" + secure().nextAlphanumeric(8)).build(), true);
        final Boat boat = boatService.save(aBoat().withOwner(owner).build());
        listingPictureService.add(boat, List.of(pictureProcessor.process(jpeg(40, 30))));
        final long pictureId = listingPictureService.pictures(boat).get(0).id();

        mockMvc.perform(get("/s/listings/" + boat.getReference() + "/pictures/" + pictureId + "/thumbnail")
                        .with(user("alice").authorities(FREE)))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "max-age=86400, private"))
                .andExpect(header().doesNotExist("Pragma"));
        mockMvc.perform(get("/s/listings/" + boat.getReference() + "/pictures/" + pictureId + "/thumbnail"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void aBlockedAddressIsTurnedAwayFromEveryPage() throws Exception {
        blockedAddressService.block("198.51.100.0/24", "for SecurityIT", "127.0.0.1");
        try {
            mockMvc.perform(get("/about").with(from("198.51.100.7")))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string("Requests from your address have been blocked."));
            mockMvc.perform(get("/s/listings/all").with(from("198.51.100.7")).with(user("alice").authorities(FREE)))
                    .andExpect(status().isForbidden());
            mockMvc.perform(get("/about").with(from("198.51.101.7"))).andExpect(status().isOk());
        } finally {
            blockedAddressService.all().stream().filter(entry -> entry.getAddress().equals("198.51.100.0/24"))
                    .forEach(entry -> blockedAddressService.unblock(entry.getId()));
        }
        mockMvc.perform(get("/about").with(from("198.51.100.7"))).andExpect(status().isOk());
    }

    private static RequestPostProcessor from(String address) {
        return request -> {
            request.setRemoteAddr(address);
            return request;
        };
    }

    @Test
    public void theUserListDoesNotShowPasswordHashes() throws Exception {
        userService.save(aUser().withUsername("hashed" + secure().nextAlphanumeric(8)).build(), true);

        mockMvc.perform(get("/admin/users").param("size", "30").with(user("ada").authorities(ADMIN, FREE)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("$2a$"))));
    }

    @Test
    public void manageUsersHasNoDisableOrDeleteButtonsForTheAdministratorsOwnAccount() throws Exception {
        final User administrator = userService.save(aUser().withUsername("admin" + secure().nextAlphanumeric(8)).build(), true);
        final User member = userService.save(aUser().withUsername("member" + secure().nextAlphanumeric(8)).build(), true);

        // newest first, so the page has these two whatever other tests have saved
        mockMvc.perform(get("/admin/users").param("sort", "id,desc").with(user(administrator.getUsername()).authorities(ADMIN, FREE)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("/admin/user/" + administrator.getId() + "/delete"))))
                .andExpect(content().string(not(containsString("/admin/user/" + administrator.getId() + "/enabled/flip"))))
                .andExpect(content().string(containsString("/admin/user/" + member.getId() + "/delete")));
    }

    @Test
    public void disablingAMemberEndsTheirSession() throws Exception {
        final User member = signUpMember();
        final MockHttpSession session = signIn(member);

        userService.flipEnabled(userService.findMember(member.getUsername()));

        mockMvc.perform(get("/s/myDetails").session(session))
                .andExpect(redirectedUrl("/index?ended"));
    }

    @Test
    public void deletingAMemberEndsTheirSession() throws Exception {
        final User member = signUpMember();
        final MockHttpSession session = signIn(member);

        userService.delete(userService.findMember(member.getUsername()));

        mockMvc.perform(get("/s/myDetails").session(session))
                .andExpect(redirectedUrl("/index?ended"));
    }

    @Test
    public void otherMembersStaySignedIn() throws Exception {
        final User member = signUpMember();
        final MockHttpSession session = signIn(member);

        userService.flipEnabled(userService.findMember(signUpMember().getUsername()));

        mockMvc.perform(get("/s/myDetails").session(session))
                .andExpect(status().isOk());
    }

    private static final String MEMBER_PASSWORD = "a password for the session tests";

    private User signUpMember() {
        final User member = aUser().withUsername("session" + secure().nextAlphanumeric(8)).build();
        member.setPassword(MEMBER_PASSWORD);
        member.addRole(Role.FREE);
        return userService.save(member, true);
    }

    private MockHttpSession signIn(User member) throws Exception {
        final MvcResult result = mockMvc.perform(formLogin("/index").user(member.getUsername()).password(MEMBER_PASSWORD))
                .andExpect(authenticated())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }
}

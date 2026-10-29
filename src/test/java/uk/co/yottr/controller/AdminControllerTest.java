package uk.co.yottr.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import uk.co.yottr.form.MemberSearch;
import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;
import uk.co.yottr.model.Boat;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.ConstantsForTests;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminControllerTest extends AbstractControllerTest {

    private static final Principal ADMINISTRATOR = () -> "ada";

    private static final PageRequest BY_USERNAME = PageRequest.of(ConstantsForTests.DEFAULT_PAGE_REQUEST_PAGE,
            ConstantsForTests.DEFAULT_PAGE_SIZE, Sort.by("username"));

    @Autowired
    private UserService mockUserService;

    @Autowired
    private ReferenceDataService mockReferenceDataService;

    @Autowired
    private BoatService mockBoatService;

    @BeforeEach
    public void resetMocks() {
        reset(mockUserService, mockReferenceDataService, mockBoatService);
    }

    @AfterEach
    public void afterEachTest() {
        verifyNoMoreInteractions(mockUserService, mockBoatService);
    }

    @Test
    public void testGetUsers() throws Exception {
        final ArrayList<User> userList = new ArrayList<>();
        userList.add(new User());
        PageImpl<User> userPage = new PageImpl<>(userList);
        when(mockUserService.search(any(MemberSearch.class), eq(BY_USERNAME))).thenReturn(userPage);

        mockMvc.perform(get("/admin/users").contentType(MediaType.TEXT_HTML)
                        .param("text", "smith").param("joinedFrom", "2026-01-01").param("joinedTo", "2026-06-30"))
                .andExpect(status().isOk())
                .andExpect(view().name("manageUsers"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("search", allOf(
                        hasProperty("text", equalTo("smith")),
                        hasProperty("joinedFrom", equalTo(LocalDate.of(2026, 1, 1))),
                        hasProperty("joinedTo", equalTo(LocalDate.of(2026, 6, 30))))))
                .andExpect(model().attribute("searchQuery", equalTo("text=smith&joinedFrom=2026-01-01&joinedTo=2026-06-30")))
                .andExpect(model().attributeExists("wrapper"))
                .andExpect(header().string("Vary", containsString("HX-Request")));

        verify(mockUserService).search(any(MemberSearch.class), eq(BY_USERNAME));
    }

    @Test
    public void testGetUsersForHtmxIsOnlyTheResults() throws Exception {
        when(mockUserService.search(any(MemberSearch.class), eq(BY_USERNAME))).thenReturn(new PageImpl<>(new ArrayList<>()));

        mockMvc.perform(get("/admin/users").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("manageUsers :: results"))
                .andExpect(content().string(not(containsString("member-search-form"))))
                .andExpect(content().string(containsString("No members match")));

        verify(mockUserService).search(any(MemberSearch.class), eq(BY_USERNAME));
    }

    @Test
    public void testDeleteUser() throws Exception {
        final Long userId = 4L;
        final User user = userCalled("bob");
        when(mockUserService.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/admin/user/" + userId + "/delete").principal(ADMINISTRATOR))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(mockUserService).findById(userId);
        verify(mockUserService).delete(user);
    }

    @Test
    public void testDeleteUserWhenUserNotFound() throws Exception {
        final Long userId = 4L;
        when(mockUserService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/admin/user/" + userId + "/delete").principal(ADMINISTRATOR))
                .andExpect(status().isNotFound());

        verify(mockUserService).findById(userId);
    }

    @Test
    public void anAdministratorCannotDeleteTheirOwnAccount() throws Exception {
        final Long userId = 4L;
        when(mockUserService.findById(userId)).thenReturn(Optional.of(userCalled(ADMINISTRATOR.getName())));

        mockMvc.perform(post("/admin/user/" + userId + "/delete").principal(ADMINISTRATOR))
                .andExpect(status().isForbidden());

        verify(mockUserService).findById(userId);
    }

    @Test
    public void testFlipEnabled() throws Exception {
        final Long userId = 4L;
        final User user = userCalled("bob");
        when(mockUserService.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/admin/user/" + userId + "/enabled/flip").principal(ADMINISTRATOR))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(mockUserService).findById(userId);
        verify(mockUserService).flipEnabled(user);
    }

    @Test
    public void testFlipEnabledWhenUserNotFound() throws Exception {
        final Long userId = 4L;
        when(mockUserService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/admin/user/" + userId + "/enabled/flip").principal(ADMINISTRATOR))
                .andExpect(status().isNotFound());

        verify(mockUserService).findById(userId);
    }

    @Test
    public void anAdministratorCannotDisableTheirOwnAccount() throws Exception {
        final Long userId = 4L;
        when(mockUserService.findById(userId)).thenReturn(Optional.of(userCalled(ADMINISTRATOR.getName())));

        mockMvc.perform(post("/admin/user/" + userId + "/enabled/flip").principal(ADMINISTRATOR))
                .andExpect(status().isForbidden());

        verify(mockUserService).findById(userId);
    }

    @Test
    public void testEditUserPageHasTheirDetails() throws Exception {
        final User user = memberAlice();
        when(mockUserService.findMemberById(4L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/admin/user/4/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("memberDetails"))
                .andExpect(model().attribute("member", user))
                .andExpect(model().attribute("details", allOf(
                        hasProperty("lastName", equalTo("Smith")),
                        hasProperty("maxListings", equalTo(2)))))
                .andExpect(model().attributeExists("countries"));

        verify(mockUserService).findMemberById(4L);
    }

    @Test
    public void testEditUserSavesTheDetailsAndSaysSo() throws Exception {
        final User user = memberAlice();
        when(mockUserService.findMemberById(4L)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/admin/user/4/edit")
                        .param("firstName", "Alice").param("lastName", "Jones").param("email", "alice@example.test")
                        .param("mobile", "07700 900123").param("country", "UK").param("postcode", "PO1 1AA")
                        .param("crew", "true")
                        .param("maxListings", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("updated", "alice"));

        verify(mockUserService).findMemberById(4L);
        verify(mockUserService).save(user, false);
        assertEquals("Jones", user.getLastName());
        assertEquals(Integer.valueOf(5), user.getMaxListings());
        assertEquals("alice", user.getUsername(), "the username can't be changed");
    }

    @Test
    public void testEditUserWithErrorsShowsThemAndSavesNothing() throws Exception {
        final User user = memberAlice();
        when(mockUserService.findMemberById(4L)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/admin/user/4/edit")
                        .param("firstName", "Alice").param("lastName", "Jones").param("email", "not an email")
                        .param("mobile", "07700 900123").param("country", "UK").param("postcode", "PO1 1AA")
                        .param("maxListings", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("memberDetails"))
                .andExpect(model().attributeHasFieldErrors("details", "email", "maxListings"));

        verify(mockUserService).findMemberById(4L);
        verify(mockUserService, never()).save(any(User.class), anyBoolean());
        assertEquals("Smith", user.getLastName(), "nothing is copied onto the member");
    }

    @Test
    public void testEditUserWhenUserNotFound() throws Exception {
        when(mockUserService.findMemberById(4L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/user/4/edit")).andExpect(status().isNotFound());

        verify(mockUserService).findMemberById(4L);
    }

    @Test
    public void testFlipWithdrawnGoesBackToTheListing() throws Exception {
        final Boat boat = new Boat(userCalled("alice"));
        when(mockBoatService.findAnyListing(boat.getReference())).thenReturn(Optional.of(boat));

        mockMvc.perform(post("/admin/listings/" + boat.getReference() + "/withdrawn/flip"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/s/listings/" + boat.getReference()));

        verify(mockBoatService).findAnyListing(boat.getReference());
        verify(mockBoatService).flipWithdrawn(boat);
    }

    @Test
    public void testDeleteAnyListingSaysWhichWasDeleted() throws Exception {
        final Boat boat = new Boat(userCalled("alice"));
        boat.setTitle("Weekends in the Solent");
        when(mockBoatService.findAnyListing(boat.getReference())).thenReturn(Optional.of(boat));

        mockMvc.perform(post("/admin/listings/" + boat.getReference() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/s/listings/all"))
                .andExpect(flash().attribute("deleted", "Weekends in the Solent"));

        verify(mockBoatService).findAnyListing(boat.getReference());
        verify(mockBoatService).delete(boat);
    }

    @Test
    public void testModeratingAListingThatIsNotThere() throws Exception {
        when(mockBoatService.findAnyListing("nope")).thenReturn(Optional.empty());

        mockMvc.perform(post("/admin/listings/nope/withdrawn/flip")).andExpect(status().isNotFound());
        mockMvc.perform(post("/admin/listings/nope/delete")).andExpect(status().isNotFound());

        verify(mockBoatService, times(2)).findAnyListing("nope");
    }

    private static User memberAlice() {
        final User user = userCalled("alice");
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setEmail("alice@example.test");
        user.setMobile("07700 900123");
        user.setCountry(Country.UK);
        user.setPostcode("PO1 1AA");
        return user;
    }

    private static User userCalled(String username) {
        final User user = new User();
        user.setUsername(username);
        return user;
    }
}

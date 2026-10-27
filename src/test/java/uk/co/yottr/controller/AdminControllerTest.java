package uk.co.yottr.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import uk.co.yottr.model.User;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.ConstantsForTests;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminControllerTest extends AbstractControllerTest {

    private static final Principal ADMINISTRATOR = () -> "ada";

    @Autowired
    private UserService mockUserService;

    @BeforeEach
    public void resetMocks() {
        reset(mockUserService);
    }

    @AfterEach
    public void afterEachTest() {
        verifyNoMoreInteractions(mockUserService);
    }

    @Test
    public void testGetUsers() throws Exception {
        final ArrayList<User> userList = new ArrayList<>();
        userList.add(new User());
        PageImpl<User> userPage = new PageImpl<>(userList);
        when(mockUserService.findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST)).thenReturn(userPage);

        final String viewName = "manageUsers";
        mockMvc.perform(get("/admin/users").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("wrapper"));

        verify(mockUserService).findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST);
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

    private static User userCalled(String username) {
        final User user = new User();
        user.setUsername(username);
        return user;
    }
}

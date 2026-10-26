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

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class AdminControllerTest extends AbstractControllerTest {

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

        Long userId = 4L;

        when(mockUserService.userExists(userId)).thenReturn(true);

        mockMvc.perform(post("/admin/user/" + userId + "/delete").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(mockUserService).userExists(userId);
        verify(mockUserService).delete(userId);
    }

    @Test
    public void testDeleteUserWhenUserNotFound() throws Exception {
        final Long userId = 4L;

        mockMvc.perform(post("/admin/user/" + userId + "/delete").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());

        verify(mockUserService).userExists(userId);
    }

    @Test
    public void testFlipEnabled() throws Exception {
        Long userId = 4L;

        when(mockUserService.userExists(userId)).thenReturn(true);
        when(mockUserService.findById(userId)).thenReturn(new User());

        mockMvc.perform(post("/admin/user/4/enabled/flip").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(mockUserService).userExists(4L);
        verify(mockUserService).findById(4L);
        verify(mockUserService).save(any(User.class), eq(false));
    }

    @Test
    public void testFlipEnabledWhenUserNotFound() throws Exception {
        final Long userId = 4L;

        mockMvc.perform(post("/admin/user/" + userId + "/enabled/flip").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());

        verify(mockUserService).userExists(userId);
    }
}
package uk.co.yottr.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.co.yottr.model.User;
import uk.co.yottr.service.UserService;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class AdminControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService mockUserService;

    @Before
    public void resetMocks() {
        reset(mockUserService);
    }

    @After
    public void afterEachTest() {
        verifyNoMoreInteractions(mockUserService);
    }

    @Test
    public void testGetUsers() throws Exception {
        when(mockUserService.findAll()).thenReturn(new ArrayList<>());

        final String viewName = "manageUsers";
        mockMvc.perform(get("/admin/users").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("users"));

        verify(mockUserService, times(1)).findAll();
    }

    @Test
    public void testDeleteUser() throws Exception {

        final String viewName = "manageUsers";
        Long userId = 4L;

        when(mockUserService.userExists(userId)).thenReturn(true);
        when(mockUserService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/user/" + userId + "/delete").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("users"));

        verify(mockUserService, times(1)).userExists(userId);
        verify(mockUserService, times(1)).delete(userId);
        verify(mockUserService, times(1)).findAll();
    }

    @Test
    public void testDeleteUserWhenUserNotFound() throws Exception {
        final Long userId = 4L;

        mockMvc.perform(get("/admin/user/" + userId + "/delete").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());

        verify(mockUserService, times(1)).userExists(userId);
    }

    @Test
    public void testFlipEnabled() throws Exception {
        final String viewName = "manageUsers";
        Long userId = 4L;

        when(mockUserService.userExists(userId)).thenReturn(true);
        when(mockUserService.findById(userId)).thenReturn(new User());
        when(mockUserService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/user/4/enabled/flip").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("users"));

        verify(mockUserService, times(1)).userExists(4L);
        verify(mockUserService, times(1)).findById(4L);
        verify(mockUserService, times(1)).save(any(User.class), eq(false));
        verify(mockUserService, times(1)).findAll();
    }

    @Test
    public void testFlipEnabledWhenUserNotFound() throws Exception {
        final Long userId = 4L;

        mockMvc.perform(get("/admin/user/" + userId + "/enabled/flip").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());

        verify(mockUserService, times(1)).userExists(userId);
    }
}
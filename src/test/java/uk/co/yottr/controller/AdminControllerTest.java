package uk.co.yottr.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import uk.co.yottr.model.User;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.ConstantsForTests;

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

        final String viewName = "manageUsers";
        Long userId = 4L;

        when(mockUserService.userExists(userId)).thenReturn(true);
        final ArrayList<User> userList = new ArrayList<>();
        userList.add(new User());
        PageImpl<User> userPage = new PageImpl<>(userList);
        when(mockUserService.findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST)).thenReturn(userPage);

        mockMvc.perform(get("/admin/user/" + userId + "/delete").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("wrapper"));

        verify(mockUserService).userExists(userId);
        verify(mockUserService).delete(userId);
        verify(mockUserService).findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST);
    }

    @Test
    public void testDeleteUserWhenUserNotFound() throws Exception {
        final Long userId = 4L;

        mockMvc.perform(get("/admin/user/" + userId + "/delete").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());

        verify(mockUserService).userExists(userId);
    }

    @Test
    public void testFlipEnabled() throws Exception {
        final String viewName = "manageUsers";
        Long userId = 4L;

        when(mockUserService.userExists(userId)).thenReturn(true);
        when(mockUserService.findById(userId)).thenReturn(new User());
        final ArrayList<User> userList = new ArrayList<>();
        userList.add(new User());
        PageImpl<User> userPage = new PageImpl<>(userList);
        when(mockUserService.findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST)).thenReturn(userPage);

        mockMvc.perform(get("/admin/user/4/enabled/flip").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("wrapper"));

        verify(mockUserService).userExists(4L);
        verify(mockUserService).findById(4L);
        verify(mockUserService).save(any(User.class), eq(false));
        verify(mockUserService).findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST);
    }

    @Test
    public void testFlipEnabledWhenUserNotFound() throws Exception {
        final Long userId = 4L;

        mockMvc.perform(get("/admin/user/" + userId + "/enabled/flip").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());

        verify(mockUserService).userExists(userId);
    }
}
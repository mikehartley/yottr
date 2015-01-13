package uk.co.yottr.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;

import java.security.Principal;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.yottr.builder.UserBuilder.aUser;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class UserControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService mockUserService;

    @Autowired
    private ReferenceDataService mockReferenceDataService;

    @Before
    public void resetMockUserService() {
        reset(mockUserService);
        reset(mockReferenceDataService);
    }

    @After
    public void afterEachTest() {
        verifyNoMoreInteractions(mockUserService);
        verifyNoMoreInteractions(mockReferenceDataService);
    }

    @Test
    public void testGetSignupPage() throws Exception {
        mockMvc.perform(get("/signup").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("countries"));

        verify(mockReferenceDataService).countries();
    }

    @Test
    public void testSignupAction() throws Exception {
        final String userAttribute = "user";

        final String usernameProperty = "username";
        final String usernameValue = "usernameValue";

        final String passwordProperty = "password";
        final String passwordValue = "passwordValue";

        final String titleProperty = "title";
        final String titleValue = "titleValue";

        final String firstNameProperty = "firstName";
        final String firstNameValue = "firstNameValue";

        final String lastNameProperty = "lastName";
        final String lastNameValue = "lastNameValue";

        final String emailProperty = "email";
        final String emailValue = "email@Value";

        final String mobileProperty = "mobile";
        final String mobileValue = "mobileValue";

        final String countryProperty = "country";
        final String countryValue = Country.UK.name();

        final String postcodeProperty = "postcode";
        final String postcodeValue = "postcodeValue";

        final String aboutMeProperty = "aboutMe";
        final String aboutMeValue = "aboutMeValue";

        mockMvc.perform(post("/signup").contentType(MediaType.TEXT_HTML)
                .param(usernameProperty, usernameValue)
                .param(passwordProperty, passwordValue)
                .param(titleProperty, titleValue)
                .param(firstNameProperty, firstNameValue)
                .param(lastNameProperty, lastNameValue)
                .param(emailProperty, emailValue)
                .param(mobileProperty, mobileValue)
                .param(countryProperty, countryValue)
                .param(postcodeProperty, postcodeValue)
                .param(aboutMeProperty, aboutMeValue))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("signupSuccess"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(userAttribute))
                .andExpect(model().attribute(userAttribute, hasProperty(usernameProperty, equalTo(usernameValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(passwordProperty, equalTo(passwordValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(titleProperty, equalTo(titleValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(firstNameProperty, equalTo(firstNameValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(lastNameProperty, equalTo(lastNameValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(emailProperty, equalTo(emailValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(mobileProperty, equalTo(mobileValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(countryProperty, equalTo(Country.valueOf(countryValue)))))
                .andExpect(model().attribute(userAttribute, hasProperty(postcodeProperty, equalTo(postcodeValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(aboutMeProperty, equalTo(aboutMeValue))));

        verify(mockUserService).save(any(User.class), eq(true));
    }

    @Test
    public void testSignupActionWhenInError() throws Exception {
        final String userAttribute = "user";
        final String usernameProperty = "username";

        mockMvc.perform(post("/signup").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("signup"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(userAttribute))
                .andExpect(model().attribute(userAttribute, hasProperty(usernameProperty, nullValue())));

        verify(mockUserService, never()).save(any(User.class), eq(true));
    }

    @Test
    public void testGetMyDetailsPage() throws Exception {
        final String username = "jimbob";

        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        when(mockUserService.findByUsername(username)).thenReturn(new User());

        mockMvc.perform(get("/s/myDetails").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("myDetails"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("countries"));

        verify(mockUserService).findByUsername(username);
        verify(mockReferenceDataService).countries();
    }

    @Test
    public void testGetMyDetailsPageWhenUsernameNotFound() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        when(mockUserService.findByUsername(username)).thenReturn(null);

        mockMvc.perform(get("/s/myDetails").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist("user"));

        verify(mockUserService).findByUsername(username);
    }

    @Test
    public void testMyDetailsAction() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        when(mockUserService.findByUsername(username)).thenReturn(aUser().withUsername(username).build());

        final String userAttribute = "user";

        final String passwordProperty = "password";
        final String passwordValue = "passwordValue";

        final String titleProperty = "title";
        final String titleValue = "titleValue";

        final String firstNameProperty = "firstName";
        final String firstNameValue = "firstNameValue";

        final String lastNameProperty = "lastName";
        final String lastNameValue = "lastNameValue";

        final String emailProperty = "email";
        final String emailValue = "email@Value";

        final String mobileProperty = "mobile";
        final String mobileValue = "mobileValue";

        final String countryProperty = "country";
        final String countryValue = Country.UK.name();

        final String postcodeProperty = "postcode";
        final String postcodeValue = "postcodeValue";

        final String aboutMeProperty = "aboutMe";
        final String aboutMeValue = "aboutMeValue";

        mockMvc.perform(post("/s/myDetails").contentType(MediaType.TEXT_HTML).principal(mockPrincipal)
                .param(passwordProperty, passwordValue)
                .param(titleProperty, titleValue)
                .param(firstNameProperty, firstNameValue)
                .param(lastNameProperty, lastNameValue)
                .param(emailProperty, emailValue)
                .param(mobileProperty, mobileValue)
                .param(countryProperty, countryValue)
                .param(postcodeProperty, postcodeValue)
                .param(aboutMeProperty, aboutMeValue))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:myDetails?updated"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(userAttribute))
                .andExpect(model().attribute(userAttribute, hasProperty(passwordProperty, equalTo(passwordValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(titleProperty, equalTo(titleValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(firstNameProperty, equalTo(firstNameValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(lastNameProperty, equalTo(lastNameValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(emailProperty, equalTo(emailValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(mobileProperty, equalTo(mobileValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(countryProperty, equalTo(Country.valueOf(countryValue)))))
                .andExpect(model().attribute(userAttribute, hasProperty(postcodeProperty, equalTo(postcodeValue))))
                .andExpect(model().attribute(userAttribute, hasProperty(aboutMeProperty, equalTo(aboutMeValue))));

        verify(mockUserService).save(any(User.class), eq(true));
        verify(mockUserService).findByUsername(username);
    }

    @Test
    public void testDeleteMe() throws Exception {
        final String username = "jimbob";

        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = new User();
        when(mockUserService.findByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/s/deleteMe").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logout"))
                .andExpect(model().hasNoErrors());

        verify(mockUserService).findByUsername(username);
        verify(mockUserService).delete(user);
    }

    @Test
    public void testDeleteMeWhenUserNotFound() throws Exception {
        final String username = "who";

        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        when(mockUserService.findByUsername(username)).thenReturn(null);

        mockMvc.perform(get("/s/deleteMe").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logout"))
                .andExpect(model().hasNoErrors());

        verify(mockUserService).findByUsername(username);
    }
}
package uk.co.yottr.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.yottr.model.Boat;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.testconfig.ControllerTestConfig;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ControllerTestConfig.class})
@WebAppConfiguration
public class BoatControllerTest {

    public static final String PATH_TO_VIEWS = "/WEB-INF/views/";
    public static final String JSP_SUFFIX = ".jsp";
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    BoatService mockBoatService;

    @Before
    public void beforeEachTest() {
        assertNotNull("mock boat service", mockBoatService);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void afterEachTest() {
        verifyNoMoreInteractions(mockBoatService);
    }

    @Test
    public void testNewListingPage() throws Exception {
        final String viewName = "newListing";
        mockMvc.perform(get("/s/listings/new").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(forwardedUrl(urlForView(viewName)))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("boat"));
    }

    @Test
    public void testNewListingAction() throws Exception {
        final String viewName = "newListingSuccess";
        final String boatAttribute = "boat";

        final String manufacturerProperty = "manufacturer";
        final String manufacturerValue = "manufacturerValue";
        final String modelProperty = "model";
        final String modelValue = "modelValue";
        final String lengthProperty = "length";
        final String lengthValue = "55";
        final String hullTypeProperty = "hullType";
        final String hullTypeValue = "MONO";
        final String descriptionProperty = "description";
        final String descriptionValue = "descriptionValue";

        mockMvc.perform(post("/s/listings/new").contentType(MediaType.TEXT_HTML)
                    .param(manufacturerProperty, manufacturerValue)
                    .param(modelProperty, modelValue)
                    .param(lengthProperty, lengthValue)
                    .param(hullTypeProperty, hullTypeValue)
                    .param(descriptionProperty, descriptionValue))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(viewName))
                .andExpect(forwardedUrl(urlForView(viewName)))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(boatAttribute))
                .andExpect(model().attribute(boatAttribute, hasProperty(manufacturerProperty, equalTo(manufacturerValue))))
                .andExpect(model().attribute(boatAttribute, hasProperty(modelProperty, equalTo(modelValue))))
                .andExpect(model().attribute(boatAttribute, hasProperty(lengthProperty, equalTo(Integer.parseInt(lengthValue)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(hullTypeProperty, equalTo(Boat.HullType.valueOf(hullTypeValue)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(descriptionProperty, equalTo(descriptionValue))));

        verify(mockBoatService, times(1)).save(any(Boat.class));
    }

    @Test
    public void testListBoats() throws Exception {
        final String viewName = "boatList";
        mockMvc.perform(get("/s/listings/all").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(forwardedUrl(urlForView(viewName)))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("boats"));

        verify(mockBoatService, times(1)).findAll();
    }

    @Test
    public void testIndex() throws Exception {
        final String viewName = "index";

        mockMvc.perform(get("/").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(forwardedUrl(urlForView(viewName)))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(0));

        mockMvc.perform(get("/index").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(forwardedUrl(urlForView(viewName)))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(0));
    }

    private String urlForView(String viewName) {
        return PATH_TO_VIEWS + viewName + JSP_SUFFIX;
    }
}
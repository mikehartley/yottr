package uk.co.yottr.controller;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.co.yottr.model.Boat;
import uk.co.yottr.service.BoatService;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class BoatControllerTest extends AbstractControllerTest {

    @Autowired
    private BoatService mockBoatService;

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
    public void testSignupActionWhenInError() throws Exception {
        final String viewNameWhenInError = "newListing";
        final String boatAttribute = "boat";

        final String manufacturerProperty = "manufacturer";

        mockMvc.perform(post("/s/listings/new").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name(viewNameWhenInError))
                .andExpect(forwardedUrl(urlForView(viewNameWhenInError)))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(boatAttribute))
                .andExpect(model().attribute(boatAttribute, hasProperty(manufacturerProperty, nullValue())));

        verify(mockBoatService, never()).save(any(Boat.class));
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
}
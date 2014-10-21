package uk.co.yottr.controller;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.SailingStyle;
import uk.co.yottr.service.BoatService;

import java.time.LocalDate;

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
        final String unitsImperialProperty = "unitsImperial";
        final String unitsImperialValue = "true";
        final String hullTypeProperty = "hullType";
        final String hullTypeValue = "MONO";
        final String descriptionProperty = "description";
        final String descriptionValue = "descriptionValue";
        final String sailingStyleProperty = "sailingStyle";
        final String sailingStyleValue = "ALL";
        final String minimumQualificationByRankProperty = "minimumRequiredLevelByRank";
        final String minimumQualificationValue = "400";
        final String minimumQualificationProperty = "minimumRequiredLevel";
        final int minimumQualificationValueAsInt = Integer.parseInt(minimumQualificationValue);
        final String dateRelevantToProperty = "dateRelevantTo";
        final String dateRelevantToValue= "26/09/2014";

        mockMvc.perform(post("/s/listings/new").contentType(MediaType.TEXT_HTML)
                        .param(manufacturerProperty, manufacturerValue)
                        .param(modelProperty, modelValue)
                        .param(lengthProperty, lengthValue)
                        .param(unitsImperialProperty, unitsImperialValue)
                        .param(hullTypeProperty, hullTypeValue)
                        .param(descriptionProperty, descriptionValue)
                        .param(sailingStyleProperty, sailingStyleValue)
                        .param(minimumQualificationByRankProperty, minimumQualificationValue)
                        .param(dateRelevantToProperty, dateRelevantToValue)
        )
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(viewName))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(boatAttribute))
                .andExpect(model().attribute(boatAttribute, hasProperty(manufacturerProperty, equalTo(manufacturerValue))))
                .andExpect(model().attribute(boatAttribute, hasProperty(modelProperty, equalTo(modelValue))))
                .andExpect(model().attribute(boatAttribute, hasProperty(lengthProperty, equalTo(Integer.parseInt(lengthValue)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(unitsImperialProperty, equalTo(true))))
                .andExpect(model().attribute(boatAttribute, hasProperty(hullTypeProperty, equalTo(Boat.HullType.valueOf(hullTypeValue)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(descriptionProperty, equalTo(descriptionValue))))
                .andExpect(model().attribute(boatAttribute, hasProperty(sailingStyleProperty, equalTo(SailingStyle.valueOf(sailingStyleValue)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(minimumQualificationProperty, hasProperty("rank", equalTo(minimumQualificationValueAsInt)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(dateRelevantToProperty, equalTo(LocalDate.of(2014, 9, 26)))));

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
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(0));

        mockMvc.perform(get("/index").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(0));
    }
}
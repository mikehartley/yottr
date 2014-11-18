package uk.co.yottr.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.SailingStyle;
import uk.co.yottr.model.User;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.ConstantsForTests;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.UserBuilder.aUser;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class BoatControllerTest extends AbstractControllerTest {

    @Autowired
    private BoatService mockBoatService;

    @Autowired
    private UserService mockUserService;

    @Autowired
    private ReferenceDataService mockReferenceDataService;

    @Before
    public void resetMocks() {
        reset(mockBoatService);
        reset(mockUserService);
        reset(mockReferenceDataService);
    }

    @After
    public void afterEachTest() {
        verifyNoMoreInteractions(mockBoatService);
        verifyNoMoreInteractions(mockUserService);
        verifyNoMoreInteractions(mockReferenceDataService);
    }

    @Test
    public void testNewListingPage() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);

        final String viewName = "newListing";
        mockMvc.perform(get("/s/listings/new").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(4))
                .andExpect(model().attributeExists("boat"))
                .andExpect(model().attributeExists("ryaSailCruisingLevels"))
                .andExpect(model().attributeExists("sailingStyles"))
                .andExpect(model().attributeExists("hullTypes"));

        verify(mockUserService).findByUsername(username);
        verify(mockReferenceDataService).ryaSailCruisingLevels();
        verify(mockReferenceDataService).sailingStyles();
        verify(mockReferenceDataService).hullTypes();
    }

    @Test
    public void testMyListingsPage() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);

        final String viewName = "myListings";
        mockMvc.perform(get("/s/listings/mine").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name(viewName))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("boatListings"));

        verify(mockUserService).findByUsername(username);
    }

    @Test
    public void testNewListingAction() throws Exception {
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
                .andExpect(view().name("newListingSuccess"))
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
        final String boatAttribute = "boat";

        mockMvc.perform(post("/s/listings/new").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("newListing"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(boatAttribute))
                .andExpect(model().attribute(boatAttribute, hasProperty("manufacturer", nullValue())));

        verify(mockBoatService, never()).save(any(Boat.class));
    }

    @Test
    public void testListBoats() throws Exception {
        final ArrayList<Boat> boatList = new ArrayList<>();
        boatList.add(aBoat().withOwner(aUser().build()).build());
        final PageImpl<Boat> boatPage = new PageImpl<>(boatList);
        when(mockBoatService.findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST)).thenReturn(boatPage); // page and size come from fallback settings as found in config

        mockMvc.perform(get("/s/listings/all").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("boatList"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("wrapper"));

        verify(mockBoatService, times(1)).findAll(ConstantsForTests.DEFAULT_PAGE_REQUEST);
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

    @Test
    public void testMyListings() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat(aBoat()).build();
        when(mockUserService.findByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/s/listings/mine").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("myListings"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("boatListings"));

        verify(mockUserService).findByUsername(username);
    }

    @Test
    public void testFlipSuspended() throws Exception {
        final String boatReference = "ref1";
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);
        final Boat boat = user.getBoatListings().get(0);
        when(mockBoatService.findByReference(boatReference)).thenReturn(boat);

        mockMvc.perform(get("/s/listings/" + boatReference + "/suspended/flip").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("myListings"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("boatListings"));

        verify(mockBoatService).findByReference(boatReference);
        verify(mockBoatService).save(boat);
        verify(mockUserService).findByUsername(username);
    }

    @Test
    public void testFlipSuspendedWhenBoatNotFound() throws Exception {
        final String unknownBoatReference = "wtf";

        mockMvc.perform(get("/s/listings/" + unknownBoatReference + "/suspended/flip").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());

        verify(mockBoatService).findByReference(unknownBoatReference);
    }

    @Test
    public void testFlipSuspendedWhenBoatDoesntBelongToPrincipal() throws Exception {
        final String boatReference = "ref1";
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);
        final Boat boat = aBoat().withOwner(aUser().withUsername("someother").build()).build();
        when(mockBoatService.findByReference(boatReference)).thenReturn(boat);

        mockMvc.perform(get("/s/listings/" + boatReference + "/suspended/flip").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isNotFound());

        verify(mockBoatService).findByReference(boatReference);
        verify(mockUserService).findByUsername(username);
    }
}
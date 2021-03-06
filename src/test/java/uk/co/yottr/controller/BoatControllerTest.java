package uk.co.yottr.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.FinancialArrangement;
import uk.co.yottr.model.SailingPurpose;
import uk.co.yottr.model.User;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.ConstantsForTests;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.UserBuilder.aUser;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
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

        mockMvc.perform(get("/s/listings/new").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("newListing"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(6))
                .andExpect(model().attributeExists("boat"))
                .andExpect(model().attributeExists("allowedMoreListings"))
                .andExpect(model().attributeExists("ryaSailCruisingLevels"))
                .andExpect(model().attributeExists("sailingPurposes"))
                .andExpect(model().attributeExists("hullTypes"))
                .andExpect(model().attributeExists("financialArrangements"));

        verify(mockUserService).findByUsername(username);
        verify(mockReferenceDataService).ryaSailCruisingLevels();
        verify(mockReferenceDataService).sailingPurposes();
        verify(mockReferenceDataService).hullTypes();
        verify(mockReferenceDataService).financialArrangements();
    }

    @Test
    public void testMyListingsPage() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
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
    public void testNewListingAction() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);

        final String boatAttribute = "boat";

        final String titleProperty = "title";
        final String titleValue = "titleValue";
        final String makeAndModelProperty = "makeAndModel";
        final String makeAndModelValue = "makeAndModelValue";
        final String lengthProperty = "length";
        final String lengthValue = "55";
        final String unitsImperialProperty = "unitsImperial";
        final String unitsImperialValue = "true";
        final String hullTypeProperty = "hullType";
        final String hullTypeValue = "MONO";
        final String descriptionProperty = "description";
        final String descriptionValue = "descriptionValue";
        final String sailingPurposeProperty = "sailingPurpose";
        final String sailingPurposeValue = "CRUISING";
        final String minimumQualificationByRankProperty = "minimumRequiredLevelByRank";
        final String minimumQualificationValue = "400";
        final String minimumQualificationProperty = "minimumRequiredLevel";
        final int minimumQualificationValueAsInt = Integer.parseInt(minimumQualificationValue);
        final String dateRelevantToProperty = "dateRelevantTo";
        final String dateRelevantToValue= "26/09/2014";
        final String financialArrangementProperty = "financialArrangement";
        final String financialArrangementValue = FinancialArrangement.NO_CONTRIBUTION.getName();

        mockMvc.perform(post("/s/listings/new").contentType(MediaType.TEXT_HTML).principal(mockPrincipal)
                        .param(titleProperty, titleValue)
                        .param(makeAndModelProperty, makeAndModelValue)
                        .param(lengthProperty, lengthValue)
                        .param(unitsImperialProperty, unitsImperialValue)
                        .param(hullTypeProperty, hullTypeValue)
                        .param(descriptionProperty, descriptionValue)
                        .param(sailingPurposeProperty, sailingPurposeValue)
                        .param(minimumQualificationByRankProperty, minimumQualificationValue)
                        .param(dateRelevantToProperty, dateRelevantToValue)
                        .param(financialArrangementProperty, financialArrangementValue)
        )
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("newListingSuccess"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(boatAttribute))
                .andExpect(model().attribute(boatAttribute, hasProperty(makeAndModelProperty, equalTo(makeAndModelValue))))
                .andExpect(model().attribute(boatAttribute, hasProperty(lengthProperty, equalTo(Integer.parseInt(lengthValue)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(unitsImperialProperty, equalTo(true))))
                .andExpect(model().attribute(boatAttribute, hasProperty(hullTypeProperty, equalTo(Boat.HullType.valueOf(hullTypeValue)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(descriptionProperty, equalTo(descriptionValue))))
                .andExpect(model().attribute(boatAttribute, hasProperty(sailingPurposeProperty, equalTo(SailingPurpose.valueOf(sailingPurposeValue)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(minimumQualificationProperty, hasProperty("rank", equalTo(minimumQualificationValueAsInt)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(dateRelevantToProperty, equalTo(LocalDate.of(2014, 9, 26)))))
                .andExpect(model().attribute(boatAttribute, hasProperty(financialArrangementProperty, hasProperty("name", equalTo(financialArrangementValue)))))
        ;

        verify(mockUserService).findByUsername(username);
        verify(mockBoatService).save(any(Boat.class));
    }

    @Test
    public void testNewListingActionWhenAtMaxListings() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withMaxListings(0).build();
        when(mockUserService.findByUsername(username)).thenReturn(user);

        final String titleProperty = "title";
        final String titleValue = "titleValue";
        final String makeAndModelProperty = "makeAndModel";
        final String makeAndModelValue = "makeAndModelValue";
        final String lengthProperty = "length";
        final String lengthValue = "55";
        final String unitsImperialProperty = "unitsImperial";
        final String unitsImperialValue = "true";
        final String hullTypeProperty = "hullType";
        final String hullTypeValue = "MONO";
        final String descriptionProperty = "description";
        final String descriptionValue = "descriptionValue";
        final String sailingPurposeProperty = "sailingPurpose";
        final String sailingPurposeValue = "CRUISING";
        final String minimumQualificationByRankProperty = "minimumRequiredLevelByRank";
        final String minimumQualificationValue = "400";
        final String dateRelevantToProperty = "dateRelevantTo";
        final String dateRelevantToValue= "26/09/2014";
        final String financialArrangementProperty = "financialArrangement";
        final String financialArrangementValue = FinancialArrangement.NO_CONTRIBUTION.getName();

        mockMvc.perform(post("/s/listings/new").contentType(MediaType.TEXT_HTML).principal(mockPrincipal)
                        .param(titleProperty, titleValue)
                        .param(makeAndModelProperty, makeAndModelValue)
                        .param(lengthProperty, lengthValue)
                        .param(unitsImperialProperty, unitsImperialValue)
                        .param(hullTypeProperty, hullTypeValue)
                        .param(descriptionProperty, descriptionValue)
                        .param(sailingPurposeProperty, sailingPurposeValue)
                        .param(minimumQualificationByRankProperty, minimumQualificationValue)
                        .param(dateRelevantToProperty, dateRelevantToValue)
                        .param(financialArrangementProperty, financialArrangementValue)
        )
                .andExpect(status().isForbidden());

        verify(mockUserService).findByUsername(username);
    }

    @Test
    public void testListBoats() throws Exception {
        final ArrayList<Boat> boatList = new ArrayList<>();
        boatList.add(aBoat().withOwner(aUser().build()).build());
        final PageImpl<Boat> boatPage = new PageImpl<>(boatList);
        when(mockBoatService.findAllExcludingSuspended(ConstantsForTests.DEFAULT_PAGE_REQUEST)).thenReturn(boatPage); // page and size come from fallback settings as found in config

        mockMvc.perform(get("/s/listings/all").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("boatList"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("wrapper"));

        verify(mockBoatService).findAllExcludingSuspended(ConstantsForTests.DEFAULT_PAGE_REQUEST);
    }

    @Test
    public void testIndex() throws Exception {
        testIndexWithUrl("/");
        testIndexWithUrl("/index");
    }

    private void testIndexWithUrl(String url) throws Exception {

        reset(mockReferenceDataService);

        mockMvc.perform(get(url).contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(4))
                .andExpect(model().attributeExists("ryaSailCruisingLevels"))
                .andExpect(model().attributeExists("sailingPurposes"))
                .andExpect(model().attributeExists("hullTypes"))
                .andExpect(model().attributeExists("financialArrangements"));

        verify(mockReferenceDataService).ryaSailCruisingLevels();
        verify(mockReferenceDataService).sailingPurposes();
        verify(mockReferenceDataService).hullTypes();
        verify(mockReferenceDataService).financialArrangements();
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
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);
        final Boat boat = user.getBoatListings().get(0);

        mockMvc.perform(get("/s/listings/" + boat.getReference() + "/suspended/flip").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("myListings"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("boatListings"));

        verify(mockBoatService).save(boat);
        verify(mockUserService, times(2)).findByUsername(username);
    }

    @Test
    public void testFlipSuspendedWhenBoatDoesntBelongToPrincipal() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/s/listings/unknownref/suspended/flip").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isNotFound());

        verify(mockUserService).findByUsername(username);
    }

    @Test
    public void testDeleteListing() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);
        final Boat boat = user.getBoatListings().get(0);

        mockMvc.perform(get("/s/listings/" + boat.getReference() + "/delete").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("myListings"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("boatListings"));

        verify(mockBoatService).delete(boat);
        verify(mockUserService, times(2)).findByUsername(username);
    }

    @Test
    public void testDeleteListingWhenBoatDoesntBelongToPrincipal() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat(aBoat()).build();
        when(mockUserService.findByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/s/listings/unknownref/delete").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isNotFound());

        verify(mockUserService).findByUsername(username);
    }

    @Test
    public void testEditListingPage() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);
        final Boat boat = user.getBoatListings().get(0);

        mockMvc.perform(get("/s/listings/" + boat.getReference() + "/edit").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("editListing"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(4))
                .andExpect(model().attributeExists("boat"))
                .andExpect(model().attributeExists("ryaSailCruisingLevels"))
                .andExpect(model().attributeExists("sailingPurposes"))
                .andExpect(model().attributeExists("hullTypes"));

        verify(mockUserService).findByUsername(username);
        verify(mockReferenceDataService).ryaSailCruisingLevels();
        verify(mockReferenceDataService).sailingPurposes();
        verify(mockReferenceDataService).hullTypes();
    }

    @Test
    public void testEditListingAction() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);
        final Boat originalBoat = user.getBoatListings().get(0);
        final Boat savedBoat = new Boat(user);

        final String titleProperty = "title";
        final String titleValue = "titleValueEdited";
        final String makeAndModelProperty = "makeAndModel";
        final String makeAndModelValue = "makeAndModelValueEdited";
        final String lengthProperty = "length";
        final String lengthValue = "33";
        final String unitsImperialProperty = "unitsImperial";
        final String unitsImperialValue = "false";
        final String hullTypeProperty = "hullType";
        final String hullTypeValue = "MULTI";
        final String descriptionProperty = "description";
        final String descriptionValue = "descriptionValueEdited";
        final String sailingPurposeProperty = "sailingPurpose";
        final String sailingPurposeValue = "RACING";
        final String minimumQualificationByRankProperty = "minimumRequiredLevelByRank";
        final String minimumQualificationValue = "300";
        final int minimumQualificationValueAsInt = Integer.parseInt(minimumQualificationValue);
        final String dateRelevantToProperty = "dateRelevantTo";
        final String dateRelevantToValue= "26/09/2015";
        final String financialArrangementProperty = "financialArrangement";
        final String financialArrangementValue = FinancialArrangement.NO_CONTRIBUTION.getName();

        when(mockBoatService.save(any(Boat.class))).thenReturn(savedBoat);

        final String boatAttribute = "boat";

        mockMvc.perform(post("/s/listings/" + originalBoat.getReference() + "/edit").contentType(MediaType.TEXT_HTML).principal(mockPrincipal)
                        .param(titleProperty, titleValue)
                        .param(makeAndModelProperty, makeAndModelValue)
                        .param(lengthProperty, lengthValue)
                        .param(unitsImperialProperty, unitsImperialValue)
                        .param(hullTypeProperty, hullTypeValue)
                        .param(descriptionProperty, descriptionValue)
                        .param(sailingPurposeProperty, sailingPurposeValue)
                        .param(minimumQualificationByRankProperty, minimumQualificationValue)
                        .param(dateRelevantToProperty, dateRelevantToValue)
                        .param(financialArrangementProperty, financialArrangementValue)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("redirect:/s/listings/mine?updated"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(boatAttribute))
                .andExpect(model().attribute(boatAttribute, is(savedBoat)));

        ArgumentCaptor<Boat> boatArgumentCaptor = ArgumentCaptor.forClass(Boat.class);
        verify(mockBoatService).save(boatArgumentCaptor.capture());
        verify(mockUserService).findByUsername(username);

        final Boat boatThatWasSaved = boatArgumentCaptor.getValue();
        assertEquals(titleValue, boatThatWasSaved.getTitle());
        assertEquals(makeAndModelValue, boatThatWasSaved.getMakeAndModel());
        assertEquals(Integer.valueOf(lengthValue), boatThatWasSaved.getLength());
        assertEquals(Boolean.valueOf(unitsImperialValue), boatThatWasSaved.isUnitsImperial());
        assertEquals(Boolean.valueOf(unitsImperialValue), boatThatWasSaved.isUnitsImperial());
        assertEquals(Boat.HullType.valueOf(hullTypeValue), boatThatWasSaved.getHullType());
        assertEquals(descriptionValue, boatThatWasSaved.getDescription());
        assertEquals(SailingPurpose.valueOf(sailingPurposeValue), boatThatWasSaved.getSailingPurpose());
        assertEquals(minimumQualificationValueAsInt, boatThatWasSaved.getMinimumRequiredLevelByRank());
        assertEquals(LocalDate.of(2015, 9, 26), boatThatWasSaved.getDateRelevantTo());
    }
}
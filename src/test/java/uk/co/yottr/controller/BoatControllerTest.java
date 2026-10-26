package uk.co.yottr.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.form.ListingForm;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.FinancialArrangement;
import uk.co.yottr.model.Frequency;
import uk.co.yottr.model.RoleRequired;
import uk.co.yottr.model.SailingPurpose;
import uk.co.yottr.model.User;
import uk.co.yottr.model.VesselType;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.ConstantsForTests;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    public void resetMocks() {
        reset(mockBoatService);
        reset(mockUserService);
        reset(mockReferenceDataService);
    }

    @AfterEach
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
                .andExpect(model().size(9))
                .andExpect(model().attributeExists("boat"))
                .andExpect(model().attributeExists("allowedMoreListings"))
                .andExpect(model().attributeExists("ryaSailCruisingLevels"))
                .andExpect(model().attributeExists("sailingPurposes"))
                .andExpect(model().attributeExists("hullTypes"))
                .andExpect(model().attributeExists("financialArrangements"))
                .andExpect(model().attributeExists("vesselTypes"))
                .andExpect(model().attributeExists("rolesRequired"))
                .andExpect(model().attributeExists("frequencies"));

        verify(mockUserService).findByUsername(username);
        verifyListingFormReferenceDataFetched();
    }

    private void verifyListingFormReferenceDataFetched() {
        verify(mockReferenceDataService).ryaSailCruisingLevels();
        verify(mockReferenceDataService).sailingPurposes();
        verify(mockReferenceDataService).hullTypes();
        verify(mockReferenceDataService).financialArrangements();
        verify(mockReferenceDataService).vesselTypes();
        verify(mockReferenceDataService).rolesRequired();
        verify(mockReferenceDataService).frequencies();
    }

    @Test
    public void testMyListingsPage() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockBoatService.findListings(username)).thenReturn(user.getBoatListings());

        mockMvc.perform(get("/s/listings/mine").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("myListings"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("boatListings"));

        verify(mockBoatService).findListings(username);
    }

    @Test
    public void testNewListingAction() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();

        final String boatAttribute = "boat";
        // owned by another user: built for this user, it would count towards their listing limit before the request
        final Boat createdBoat = aBoat().withOwner(aUser().build()).build();
        when(mockBoatService.create(eq(username), any(ListingForm.class))).thenReturn(createdBoat);

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
                        .param("vesselType", "SAIL")
                        .param("yearBuilt", "1995")
                        .param("roleRequired", "CREW")
                        .param("frequency", "WEEKENDS")
        )
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("newListingSuccess"))
                .andExpect(model().size(1))
                .andExpect(model().attribute(boatAttribute, is(createdBoat)));

        final ArgumentCaptor<ListingForm> formCaptor = ArgumentCaptor.forClass(ListingForm.class);
        verify(mockBoatService).create(eq(username), formCaptor.capture());

        final ListingForm form = formCaptor.getValue();
        assertThat(form, hasProperty(titleProperty, equalTo(titleValue)));
        assertThat(form, hasProperty(makeAndModelProperty, equalTo(makeAndModelValue)));
        assertThat(form, hasProperty(lengthProperty, equalTo(Integer.parseInt(lengthValue))));
        assertThat(form, hasProperty(unitsImperialProperty, equalTo(true)));
        assertThat(form, hasProperty(hullTypeProperty, equalTo(Boat.HullType.valueOf(hullTypeValue))));
        assertThat(form, hasProperty(descriptionProperty, equalTo(descriptionValue)));
        assertThat(form, hasProperty(sailingPurposeProperty, equalTo(SailingPurpose.valueOf(sailingPurposeValue))));
        assertThat(form, hasProperty(minimumQualificationByRankProperty, equalTo(minimumQualificationValueAsInt)));
        assertThat(form, hasProperty(dateRelevantToProperty, equalTo(LocalDate.of(2014, 9, 26))));
        assertThat(form, hasProperty(financialArrangementProperty, equalTo(FinancialArrangement.FinancialArrangementEnum.NO_CONTRIBUTION)));
        assertThat(form, hasProperty("vesselType", equalTo(VesselType.SAIL)));
        assertThat(form, hasProperty("yearBuilt", equalTo(1995)));
        assertThat(form, hasProperty("roleRequired", equalTo(RoleRequired.CREW)));
        assertThat(form, hasProperty("frequency", equalTo(List.of(Frequency.FrequencyEnum.WEEKENDS))));
    }

    @Test
    public void testNewListingActionWhenAtMaxListings() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withMaxListings(0).build();
        when(mockUserService.findByUsername(username)).thenReturn(user);
        // BoatService enforces the limit
        when(mockBoatService.create(eq(username), any(ListingForm.class)))
                .thenThrow(new IllegalOperationException("User is already at listing limit."));

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
                        .param("vesselType", "SAIL")
                        .param("yearBuilt", "1995")
                        .param("roleRequired", "CREW")
                        .param("frequency", "WEEKENDS")
        )
                .andExpect(status().isForbidden());

        verify(mockBoatService).create(eq(username), any(ListingForm.class));
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
        when(mockBoatService.findListings(username)).thenReturn(user.getBoatListings());

        mockMvc.perform(get("/s/listings/mine").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("myListings"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("boatListings"));

        verify(mockBoatService).findListings(username);
    }

    @Test
    public void testFlipSuspended() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        final Boat boat = user.getBoatListings().get(0);
        when(mockBoatService.findListing(boat.getReference(), username)).thenReturn(Optional.of(boat));

        mockMvc.perform(post("/s/listings/" + boat.getReference() + "/suspended/flip").principal(mockPrincipal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/s/listings/mine"));

        verify(mockBoatService).findListing(boat.getReference(), username);
        verify(mockBoatService).flipSuspended(boat);
    }

    @Test
    public void testFlipSuspendedWhenBoatDoesntBelongToPrincipal() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        when(mockBoatService.findListing("unknownref", username)).thenReturn(Optional.empty());

        mockMvc.perform(post("/s/listings/unknownref/suspended/flip").principal(mockPrincipal))
                .andExpect(status().isNotFound());

        verify(mockBoatService).findListing("unknownref", username);
    }

    @Test
    public void testFlipSuspendedIsNotAGet() throws Exception {
        final Principal mockPrincipal = () -> "jimbob";

        mockMvc.perform(get("/s/listings/someref/suspended/flip").principal(mockPrincipal))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void testDeleteListing() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        final Boat boat = user.getBoatListings().get(0);
        when(mockBoatService.findListing(boat.getReference(), username)).thenReturn(Optional.of(boat));

        mockMvc.perform(post("/s/listings/" + boat.getReference() + "/delete").principal(mockPrincipal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/s/listings/mine"));

        verify(mockBoatService).findListing(boat.getReference(), username);
        verify(mockBoatService).delete(boat);
    }

    @Test
    public void testDeleteListingWhenBoatDoesntBelongToPrincipal() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        when(mockBoatService.findListing("unknownref", username)).thenReturn(Optional.empty());

        mockMvc.perform(post("/s/listings/unknownref/delete").principal(mockPrincipal))
                .andExpect(status().isNotFound());

        verify(mockBoatService).findListing("unknownref", username);
    }

    @Test
    public void testDeleteListingIsNotAGet() throws Exception {
        final Principal mockPrincipal = () -> "jimbob";

        mockMvc.perform(get("/s/listings/someref/delete").principal(mockPrincipal))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void testEditListingPage() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        final Boat boat = user.getBoatListings().get(0);
        when(mockBoatService.findListing(boat.getReference(), username)).thenReturn(Optional.of(boat));

        mockMvc.perform(get("/s/listings/" + boat.getReference() + "/edit").contentType(MediaType.TEXT_HTML).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("editListing"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(9))
                .andExpect(model().attribute("boat", allOf(
                        instanceOf(ListingForm.class),
                        hasProperty("title", equalTo(boat.getTitle())),
                        hasProperty("makeAndModel", equalTo(boat.getMakeAndModel())))))
                .andExpect(model().attribute("boatReference", equalTo(boat.getReference())))
                .andExpect(model().attributeExists("ryaSailCruisingLevels"))
                .andExpect(model().attributeExists("sailingPurposes"))
                .andExpect(model().attributeExists("hullTypes"))
                .andExpect(model().attributeExists("financialArrangements"))
                .andExpect(model().attributeExists("vesselTypes"))
                .andExpect(model().attributeExists("rolesRequired"))
                .andExpect(model().attributeExists("frequencies"));

        verify(mockBoatService).findListing(boat.getReference(), username);
        verifyListingFormReferenceDataFetched();
    }

    @Test
    public void testEditListingActionWithErrorsShowsFormAgain() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        when(mockUserService.findByUsername(username)).thenReturn(user);
        final Boat originalBoat = user.getBoatListings().get(0);

        // nothing but a title, so the other required fields are in error
        mockMvc.perform(post("/s/listings/" + originalBoat.getReference() + "/edit").contentType(MediaType.TEXT_HTML).principal(mockPrincipal)
                        .param("title", "titleValueEdited"))
                .andExpect(status().isOk())
                .andExpect(view().name("editListing"))
                .andExpect(model().attributeHasFieldErrors("boat", "makeAndModel", "vesselType", "yearBuilt", "roleRequired", "frequency"))
                .andExpect(model().attribute("boatReference", equalTo(originalBoat.getReference())))
                .andExpect(model().attributeExists("vesselTypes"))
                .andExpect(model().attributeExists("rolesRequired"))
                .andExpect(model().attributeExists("frequencies"));

        verifyListingFormReferenceDataFetched();
        verify(mockBoatService, never()).save(any(Boat.class));
    }

    @Test
    public void testEditListingAction() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        final Boat originalBoat = user.getBoatListings().get(0);
        when(mockBoatService.findListing(originalBoat.getReference(), username)).thenReturn(Optional.of(originalBoat));
        originalBoat.setSuspended(true);
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

        when(mockBoatService.update(eq(originalBoat), any(ListingForm.class))).thenReturn(savedBoat);

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
                        .param("vesselType", "SAIL")
                        .param("yearBuilt", "1995")
                        .param("roleRequired", "CREW")
                        .param("frequency", "WEEKENDS")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("redirect:/s/listings/mine?updated"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(boatAttribute))
                .andExpect(model().attribute(boatAttribute, is(savedBoat)));

        // the service applies the form to the listing it was given; BoatServiceTest checks what it changes
        ArgumentCaptor<ListingForm> formCaptor = ArgumentCaptor.forClass(ListingForm.class);
        verify(mockBoatService).update(eq(originalBoat), formCaptor.capture());
        verify(mockBoatService).findListing(originalBoat.getReference(), username);

        final ListingForm form = formCaptor.getValue();
        assertEquals(titleValue, form.getTitle());
        assertEquals(makeAndModelValue, form.getMakeAndModel());
        assertEquals(Integer.valueOf(lengthValue), form.getLength());
        assertEquals(Boolean.valueOf(unitsImperialValue), form.isUnitsImperial());
        assertEquals(Boat.HullType.valueOf(hullTypeValue), form.getHullType());
        assertEquals(descriptionValue, form.getDescription());
        assertEquals(SailingPurpose.valueOf(sailingPurposeValue), form.getSailingPurpose());
        assertEquals(minimumQualificationValueAsInt, form.getMinimumRequiredLevelByRank());
        assertEquals(LocalDate.of(2015, 9, 26), form.getDateRelevantTo());
        assertEquals(VesselType.SAIL, form.getVesselType());
        assertEquals(Integer.valueOf(1995), form.getYearBuilt());
        assertEquals(RoleRequired.CREW, form.getRoleRequired());
        assertEquals(List.of(Frequency.FrequencyEnum.WEEKENDS), form.getFrequency());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.valueOf(financialArrangementValue), form.getFinancialArrangement());
    }

    @Test
    public void testListingFormIgnoresFieldsThatAreNotOnIt() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).withBoat().build();
        final Boat originalBoat = user.getBoatListings().get(0);
        when(mockBoatService.findListing(originalBoat.getReference(), username)).thenReturn(Optional.of(originalBoat));
        when(mockBoatService.update(eq(originalBoat), any(ListingForm.class))).thenReturn(originalBoat);

        mockMvc.perform(post("/s/listings/" + originalBoat.getReference() + "/edit").contentType(MediaType.TEXT_HTML).principal(mockPrincipal)
                        .param("title", "titleValue")
                        .param("makeAndModel", "makeAndModelValue")
                        .param("length", "33")
                        .param("hullType", "MONO")
                        .param("description", "descriptionValue")
                        .param("sailingPurpose", "RACING")
                        .param("financialArrangement", "PAID")
                        .param("vesselType", "SAIL")
                        .param("yearBuilt", "1995")
                        .param("roleRequired", "CREW")
                        .param("frequency", "WEEKENDS")
                        // not on the form
                        .param("suspended", "false")
                        .param("owner.username", "someone-else")
                        .param("reference", "another-reference"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors());

        verify(mockBoatService).update(eq(originalBoat), any(ListingForm.class));
        verify(mockBoatService).findListing(originalBoat.getReference(), username);
        assertEquals(user, originalBoat.getOwner());
    }

    @Test
    public void testNewListingActionRejectsAnUnknownLevel() throws Exception {
        final String username = "jimbob";
        final Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        final User user = aUser().withUsername(username).build();
        when(mockUserService.findByUsername(username)).thenReturn(user);

        mockMvc.perform(post("/s/listings/new").contentType(MediaType.TEXT_HTML).principal(mockPrincipal)
                        .param("minimumRequiredLevelByRank", "150"))
                .andExpect(status().isOk())
                .andExpect(view().name("newListing"))
                .andExpect(model().attributeHasFieldErrors("boat", "minimumRequiredLevelKnown"));

        verify(mockUserService).findByUsername(username);
        verifyListingFormReferenceDataFetched();
        verify(mockBoatService, never()).create(any(), any());
    }
}
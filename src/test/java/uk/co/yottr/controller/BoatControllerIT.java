package uk.co.yottr.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.yottr.initialise.InitialiseDatabase;
import uk.co.yottr.model.*;
import uk.co.yottr.repository.FinancialArrangementRepository;
import uk.co.yottr.repository.FrequencyRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.UserBuilder.aUser;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * The listing forms against a real database, which the controller tests with mocked services can't catch:
 * an edit changes a listing that Hibernate is already managing.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import(TestcontainersConfig.class)
public class BoatControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoatService boatService;

    @Autowired
    private UserService userService;

    @Autowired
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Autowired
    private FinancialArrangementRepository financialArrangementRepository;

    @Autowired
    private FrequencyRepository frequencyRepository;

    private User owner;

    @BeforeEach
    public void initialiseReferenceData() {
        InitialiseDatabase.initialise(ryaSailCruisingLevelRepository);
        InitialiseDatabase.initialise(financialArrangementRepository);
        InitialiseDatabase.initialise(frequencyRepository);

        owner = userService.save(aUser().withUsername("owner" + secure().nextAlphanumeric(10)).build(), false);
    }

    @Test
    public void editListingSavesTheChanges() throws Exception {
        final Boat boat = boatService.save(aBoat().withOwner(owner).withSuspended(true).build());
        final Principal principal = owner::getUsername;

        mockMvc.perform(post("/s/listings/" + boat.getReference() + "/edit").contentType(MediaType.APPLICATION_FORM_URLENCODED).principal(principal)
                        .param("title", "Edited title")
                        .param("makeAndModel", "Sigma 33")
                        .param("length", "33")
                        .param("unitsImperial", "true")
                        .param("hullType", "MONO")
                        .param("vesselType", "POWER")
                        .param("yearBuilt", "1988")
                        .param("description", "Edited description")
                        .param("sailingPurpose", "RACING")
                        .param("roleRequired", "SKIPPER")
                        .param("frequency", "WEEKENDS", "TRIP")
                        .param("dateRelevantTo", "14/06/2027")
                        .param("minimumRequiredLevelByRank", String.valueOf(RyaSailCruisingLevel.DAY_SKIPPER.getRank()))
                        .param("financialArrangement", "PAID"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/s/listings/mine?updated"));

        final Boat edited = boatService.findByReference(boat.getReference());
        assertEquals("Edited title", edited.getTitle());
        assertEquals(VesselType.POWER, edited.getVesselType());
        assertEquals(Integer.valueOf(1988), edited.getYearBuilt());
        assertEquals(SailingPurpose.RACING, edited.getSailingPurpose());
        assertEquals(RoleRequired.SKIPPER, edited.getRoleRequired());
        assertEquals(List.of("TRIP", "WEEKENDS"), edited.getFrequency().stream().map(Frequency::getName).sorted().toList());
        assertEquals(LocalDate.of(2027, 6, 14), edited.getDateRelevantTo());
        assertEquals(RyaSailCruisingLevel.DAY_SKIPPER.getRank(), edited.getMinimumRequiredLevelByRank());
        assertEquals("PAID", edited.getFinancialArrangement().getName());
        assertTrue(edited.isSuspended(), "editing must not change whether the listing is suspended");
    }
}

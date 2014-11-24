package uk.co.yottr.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.initialise.InitialiseDatabase;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingStyle;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;
import uk.co.yottr.testconfig.IntegrationTestConfig;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.*;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.UserBuilder.aUser;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationTestConfig.class)
public class BoatServiceIT {

    @Autowired
    private BoatService boatService;

    @Autowired
    private UserService userService;

    @Autowired
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    private User owner;

    @Before
    public void initialiseReferenceData() {

        if (ryaSailCruisingLevelRepository.findByRank(RyaSailCruisingLevel.NONE.getRank()) == null) {
            InitialiseDatabase.initialiseRyaSailCruisingLevels(ryaSailCruisingLevelRepository);
        }

        owner = userService.save(aUser().build(), false);
    }

    @After
    public void clearData() {
        for (Boat boat : boatService.findAll(new PageRequest(0, 9999))) {
            boatService.delete(boat);
        }
    }

    @Test
    public void canCreateFindByReferenceAndDelete() throws Exception {

        final String uniqueDescription = "some description with unique number : "  + new Random().nextLong();

        Boat boat = aBoat().withOwner(owner).withDescription(uniqueDescription).build();

        final Boat savedBoat = boatService.save(boat);

        final Boat foundByReference = boatService.findByReference(boat.getReference());
        assertNotNull("could not find boat by reference", foundByReference);
        assertEquals("description", uniqueDescription, foundByReference.getDescription());

        boatService.delete(savedBoat);

        assertNull("boat was not deleted", boatService.findByReference(boat.getReference()));
    }

    @Test
    public void canFindAllExcludingSuspended() throws Exception {
        final Boat liveBoat = boatService.save(aBoat().withSuspended(false).withOwner(owner).build());
        boatService.save(aBoat().withSuspended(true).withOwner(owner).build());

        final Page<Boat> resultFromService = boatService.findAllExcludingSuspended(new PageRequest(0, 10));

        assertEquals("total elements in result", 1, resultFromService.getTotalElements());
        assertEquals("element in result", liveBoat.getReference(), resultFromService.iterator().next().getReference());
    }

    @Test
    public void allFieldsPersist() throws Exception {

        final String description = randomAlphanumeric(50);
        final Boat.HullType hullType = Boat.HullType.MONO;
        final Integer length = new Random().nextInt(99) + 3;
        final boolean unitsImperial = true;
        final String manufacturer = randomAlphanumeric(15);
        final String model = randomAlphanumeric(10);
        final RyaSailCruisingLevel minLevel = new RyaSailCruisingLevel(RyaSailCruisingLevel.Level.COASTAL_SKIPPER);
        final SailingStyle sailingStyle = SailingStyle.RACING;
        final LocalDate now = LocalDate.now();
        final LocalDate dateRelevantTo = LocalDate.now();
        final User owner = createUser();

        Boat boat = new Boat(owner);
        boat.setDescription(description);
        boat.setHullType(hullType);
        boat.setLength(length);
        boat.setUnitsImperial(unitsImperial);
        boat.setManufacturer(manufacturer);
        boat.setModel(model);
        boat.setMinimumRequiredLevel(minLevel);
        boat.setSailingStyle(sailingStyle);
        boat.setDateRelevantTo(dateRelevantTo);

        final Boat savedBoat = boatService.save(boat);

        assertEquals("description", description, savedBoat.getDescription());
        assertEquals("hullType", hullType, savedBoat.getHullType());
        assertEquals("length", length, savedBoat.getLength());
        assertEquals("unitsImperial", unitsImperial, savedBoat.isUnitsImperial());
        assertEquals("manufacturer", manufacturer, savedBoat.getManufacturer());
        assertEquals("model", model, savedBoat.getModel());
        assertNotNull("reference", savedBoat.getReference());
        assertEquals("date posted", now.atStartOfDay(), savedBoat.getDatePosted().atStartOfDay());
        assertEquals("minimum level", minLevel.getRank(), savedBoat.getMinimumRequiredLevel().getRank());
        assertEquals("sailing style", sailingStyle, savedBoat.getSailingStyle());
        assertEquals("date relevant to", dateRelevantTo, savedBoat.getDateRelevantTo());
        assertEquals("owner", owner, savedBoat.getOwner());
    }

    @Test
    public void canSaveTwoBoats() {
        boatService.save(aBoat().withOwner(owner).withDescription("1").build());
        boatService.save(aBoat().withOwner(owner).withDescription("2").build());
        final Iterator<Boat> boatIterator = boatService.findAll(new PageRequest(0, 10)).iterator();
        assertEquals("1", boatIterator.next().getDescription());
        assertEquals("2", boatIterator.next().getDescription());
        assertFalse(boatIterator.hasNext());
    }

    private User createUser() {
        final String username = randomAlphabetic(10);
        final User user = aUser().withUsername(username).build();
        return userService.save(user, false);
    }
}
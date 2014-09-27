package uk.co.yottr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.initialise.InitialiseDatabase;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingStyle;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;
import uk.co.yottr.testconfig.IntegrationTestConfig;

import java.time.LocalDate;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.*;

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
    RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Before
    public void initialiseReferenceData() {

        if (ryaSailCruisingLevelRepository.findByRank(RyaSailCruisingLevel.NONE.getRank()) == null) {

            InitialiseDatabase.initialiseRyaSailCruisingLevels(ryaSailCruisingLevelRepository);
        }
    }

    @Test
    public void canCreateFindByReferenceAndDelete() throws Exception {

        final String uniqueDescription = "some description with unique number : "  + new Random().nextLong();

        Boat boat = createValidBoat(uniqueDescription);

        final Boat savedBoat = boatService.save(boat);

        final Boat foundByReference = boatService.findByReference(boat.getReference());
        assertNotNull("could not find boat by reference", foundByReference);
        assertEquals("description", uniqueDescription, foundByReference.getDescription());

        boatService.delete(savedBoat);

        assertNull("boat was not deleted", boatService.findByReference(boat.getReference()));
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

        Boat boat = new Boat();
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
    }

    @Test
    public void canSaveTwoBoats() {
        boatService.save(createValidBoat("1"));
        boatService.save(createValidBoat("2"));
        assertTrue(boatService.findAll().size() >= 2);
    }

    private Boat createValidBoat(String description) {
        Boat boat = new Boat();
        boat.setDescription(description);
        boat.setHullType(Boat.HullType.MONO);
        boat.setLength(36);
        boat.setUnitsImperial(true);
        boat.setManufacturer("Halberg Rassy");
        boat.setModel("HR36");
        boat.setSailingStyle(SailingStyle.ALL);
        return boat;
    }
}
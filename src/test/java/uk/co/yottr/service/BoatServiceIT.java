package uk.co.yottr.service;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.testconfig.IntegrationTestConfig;

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

    @Test
    public void canCreateFindByReferenceAndDelete() throws Exception {

        final String uniqueDescription = "some description with unique number : "  + new Random().nextLong();

        Boat boat = createBoat(uniqueDescription);

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
        final LocalDate now = new LocalDate(DateTimeZone.UTC);
        final LocalDate dateRelevantTo = new LocalDate(DateTimeZone.UTC);
        final RyaSailCruisingLevel minLevel = new RyaSailCruisingLevel(RyaSailCruisingLevel.Level.COASTAL_SKIPPER);

        Boat boat = new Boat();
        boat.setDescription(description);
        boat.setHullType(hullType);
        boat.setLength(length);
        boat.setUnitsImperial(unitsImperial);
        boat.setManufacturer(manufacturer);
        boat.setModel(model);
        boat.setDateRelevantTo(dateRelevantTo);
        boat.setMinimumRequiredLevel(minLevel);

        final Boat savedBoat = boatService.save(boat);

        assertEquals("description", description, savedBoat.getDescription());
        assertEquals("hullType", hullType, savedBoat.getHullType());
        assertEquals("length", length, savedBoat.getLength());
        assertEquals("unitsImperial", unitsImperial, savedBoat.isUnitsImperial());
        assertEquals("manufacturer", manufacturer, savedBoat.getManufacturer());
        assertEquals("model", model, savedBoat.getModel());
        assertNotNull("reference", savedBoat.getReference());
        assertEquals("date posted", now.toDateTimeAtStartOfDay(), savedBoat.getDatePosted().toDateTimeAtStartOfDay());
        assertEquals("date relevant to", dateRelevantTo, savedBoat.getDateRelevantTo());
        assertEquals("minimum level", minLevel, savedBoat.getMinimumRequiredLevel());
    }

    private Boat createBoat(String description) {
        Boat boat = new Boat();
        boat.setDescription(description);
        boat.setHullType(Boat.HullType.MONO);
        boat.setLength(36);
        boat.setUnitsImperial(true);
        boat.setManufacturer("Halberg Rassy");
        boat.setModel("HR36");
        return boat;
    }
}
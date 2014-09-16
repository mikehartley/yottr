package uk.co.yottr.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.model.Boat;
import uk.co.yottr.testconfig.TestConfig;

import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.*;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
public class BoatServiceIntegrationTest {

    @Autowired
    private BoatService boatService;

    @Test
    public void canCreateUpdateAndDelete() throws Exception {

        final String uniqueDescription = "some description with unique number : "  + new Random().nextLong();

        Boat boat = createBoat(uniqueDescription);

        final Boat savedBoat = boatService.save(boat);

        final List<Boat> allBoats = boatService.findAll();

        final int indexOfSavedBoat = allBoats.lastIndexOf(savedBoat);
        assertTrue("boat did not appear in find all", indexOfSavedBoat > -1);

        final Boat boatFromFindAll = allBoats.get(indexOfSavedBoat);
        assertEquals("boat.description from found boat", uniqueDescription, boatFromFindAll.getDescription());

        boatService.delete(savedBoat);
        assertFalse("find all should not contain the deleted boat", boatService.findAll().contains(savedBoat));
    }

    @Test
    public void allFieldsPersist() throws Exception {

        final String description = randomAlphanumeric(50);
        final Boat.HullType hullType = Boat.HullType.MONO;
        final Integer length = new Random().nextInt(99);
        final boolean unitsImperial = true;
        final String manufacturer = randomAlphanumeric(15);
        final String model = randomAlphanumeric(10);

        Boat boat = new Boat();
        boat.setDescription(description);
        boat.setHullType(hullType);
        boat.setLength(length);
        boat.setUnitsImperial(unitsImperial);
        boat.setManufacturer(manufacturer);
        boat.setModel(model);

        final Boat savedBoat = boatService.save(boat);

        assertEquals("description", description, savedBoat.getDescription());
        assertEquals("hullType", hullType, savedBoat.getHullType());
        assertEquals("length", length, savedBoat.getLength());
        assertEquals("unitsImperial", unitsImperial, savedBoat.isUnitsImperial());
        assertEquals("manufacturer", manufacturer, savedBoat.getManufacturer());
        assertEquals("model", model, savedBoat.getModel());
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
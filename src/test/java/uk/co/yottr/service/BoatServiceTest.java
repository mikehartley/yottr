package uk.co.yottr.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.model.Boat;

import java.util.List;
import java.util.Random;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring-context-test.xml")
public class BoatServiceTest {

    @Autowired
    private BoatService boatService;

    @Test
    public void canCreateUpdateAndDelete() throws Exception {

        final String uniqueDescription = "some description with unique number : "  + new Random().nextLong();

        Boat boat = createBoat(uniqueDescription);

        final Boat savedBoat = boatService.save(boat);

        final List<Boat> allBoats = boatService.findAll();

        final int indexOfSavedBoat = allBoats.lastIndexOf(savedBoat);
        Assert.assertTrue("boat did not appear in find all", indexOfSavedBoat > -1);

        final Boat boatFromFindAll = allBoats.get(indexOfSavedBoat);
        Assert.assertEquals("boat.description from found boat", uniqueDescription, boatFromFindAll.getDesc());

        boatService.delete(savedBoat);
        Assert.assertFalse("find all should not contain the deleted boat", boatService.findAll().contains(savedBoat));
    }

    private Boat createBoat(String description) {
        Boat boat = new Boat();
        boat.setDesc(description);
        boat.setHullType(Boat.HullType.MONO);
        boat.setLength(36);
        boat.setUnitsImperial(true);
        boat.setManufacturer("Halberg Rassy");
        boat.setModel("HR36");
        return boat;
    }
}
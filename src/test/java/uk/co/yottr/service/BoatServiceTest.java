package uk.co.yottr.service;

import org.junit.Assert;
import org.junit.Before;
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

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void canCreateUpdateAndDelete() throws Exception {

        final String uniqueDescription = "some description with unique number : "  + new Random().nextLong();

        Boat boat = new Boat();
        boat.setDesc(uniqueDescription);
        boat.setHullType(Boat.HullType.MONO);
        boat.setLength(36);
        boat.setUnitsImperial(true);
        boat.setManufacturer("Halberg Rassy");
        boat.setModel("HR36");

        final Boat savedBoat = boatService.save(boat);

        final List<Boat> allBoats = boatService.findAll();
        Assert.assertFalse("find all boats should return a non-empty result", allBoats.isEmpty());

        final Boat boatFromFindAll = allBoats.get(allBoats.lastIndexOf(savedBoat));
        Assert.assertEquals("last boat in results - unexpected description", uniqueDescription, boatFromFindAll.getDesc());

        //TODO test delete boat
    }
}
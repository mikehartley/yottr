package uk.co.yottr.service;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.model.Boat;

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

        Boat boat = new Boat();
        boat.setDesc("some description");
        boat.setHullType(Boat.HullType.MONO);
        boat.setLength(36);
        boat.setUnitsImperial(true);
        boat.setManufacturer("Halberg Rassy");
        boat.setModel("HR36");

        boatService.save(boat);

        boatService.findAll();

        Assert.fail("test not yet complete - get coding!");
    }
}
package uk.co.yottr.initialise;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.co.yottr.config.AppConfig;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.security.Roles;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * This initialises the database and shouldn't be run as part of the normal set of tests,
 * and so should be left in a disabled state unless required.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
@WebAppConfiguration
public class InitialiseDatabase {

    private boolean ENABLED = false; // this should be left as false unless you're initialising the database

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoatRepository boatRepository;

    @Before
    public void ensureRepositoryHasBeenInjected() {
        Assert.assertNotNull("check spring configuration : userRepository was not injected", userRepository);
        Assert.assertNotNull("check spring configuration : boatRepository was not injected", boatRepository);
    }

    @Test
    public void setupAdminUser() {
        if (!ENABLED) return;

        User user = new User();

        user.setUsername("mike");
        user.setPassword("lucidlucid123");
        user.addRole(Roles.ADMIN.name());
        user.addRole(Roles.CREW.name());
        user.addRole(Roles.FREE.name());
        user.setEnabled(true);
        user.setTitle("Mr");
        user.setFirstName("Mike");
        user.setLastName("H");
        user.setEmail("m@yottr.co.uk");
        user.setAboutMe("Nothing to see here.");
        user.setCountry(User.Country.UK);
        user.setMobile("07973 000000");
        user.setPostcode("DY8 1AA");

        userRepository.save(user);
    }

    @Test
    public void addBoat() {
        if (!ENABLED) return;

        Boat boat = new Boat();

        boat.setManufacturer("Halberg Rassay");
        boat.setModel("HR50");
        boat.setLength(50);
        boat.setHullType(Boat.HullType.MONO);
        boat.setDescription("You want to sail around the world? You need to look elsewhere, my tub is purely a gin palace.");

        boatRepository.save(boat);
    }
}

package uk.co.yottr.initialise;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.co.yottr.config.AppConfig;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingStyle;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.security.Role;

import java.time.LocalDate;

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
        user.setPassword(new BCryptPasswordEncoder().encode("aph3xtwIn"));
        user.addRole(Role.ADMIN);
        user.addRole(Role.CREW);
        user.addRole(Role.FREE);
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
        boat.setUnitsImperial(false);
        boat.setHullType(Boat.HullType.MONO);
        boat.setDescription("You want to sail around the world? You need to look elsewhere, my tub is purely a gin palace.");
        boat.setSailingStyle(SailingStyle.CRUISING);
        boat.setDateRelevantTo(LocalDate.of(2075, 3, 10));
        boat.setMinimumRequiredLevel(RyaSailCruisingLevel.YACHTMASTER_COASTAL);

        boatRepository.save(boat);
    }
}

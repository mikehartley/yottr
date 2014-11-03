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
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.security.Role;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

    private static final boolean ENABLED = false; // this should be left as false unless you're initialising the database

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Before
    public void ensureRepositoryHasBeenInjected() {
        Assert.assertNotNull("check spring configuration : userRepository was not injected", userRepository);
        Assert.assertNotNull("check spring configuration : boatRepository was not injected", boatRepository);
        Assert.assertNotNull("check spring configuration : ryaSailCruisingLevelRepository was not injected", ryaSailCruisingLevelRepository);
    }

    @Test
    public void init() {
        if (!ENABLED) return;

        initialiseRyaSailCruisingLevels(ryaSailCruisingLevelRepository);
        setupAdminUser("mike");
        addBoat("Halberg Rassay");
    }

    @Test
    public void addLotsOfBoats() {
        if (!ENABLED) return;

        for (int i = 0; i < 20; i++) {
            addBoat("Rust Bucket " + i);
        }
    }

    public static void initialiseRyaSailCruisingLevels(RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository) {
        final List<RyaSailCruisingLevel.Level> levels = Arrays.asList(RyaSailCruisingLevel.Level.values());
        for (RyaSailCruisingLevel.Level level : levels) {
            ryaSailCruisingLevelRepository.save(new RyaSailCruisingLevel(level));
        }
    }

    private void setupAdminUser(String username) {
        User user = new User();

        user.setUsername(username);
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

    private void addBoat(String manufacturer) {
        Boat boat = new Boat();

        boat.setManufacturer(manufacturer);
        boat.setModel("HR50");
        boat.setLength(50);
        boat.setUnitsImperial(false);
        boat.setHullType(Boat.HullType.MONO);
        boat.setDescription("You want to sail around the world? You need to look elsewhere, my tub is purely a gin palace.");
        boat.setSailingStyle(SailingStyle.CRUISING);
        boat.setDateRelevantTo(LocalDate.of(2075, 3, 10));

        final RyaSailCruisingLevel level = ryaSailCruisingLevelRepository.findByRank(RyaSailCruisingLevel.YACHTMASTER_COASTAL.getRank());
        boat.setMinimumRequiredLevel(level);

        boatRepository.save(boat);
    }
}

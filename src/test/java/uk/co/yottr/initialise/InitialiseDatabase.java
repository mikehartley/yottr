package uk.co.yottr.initialise;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import uk.co.yottr.model.*;
import uk.co.yottr.repository.*;
import uk.co.yottr.security.Role;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * This initialises the database and shouldn't be run as part of the normal set of tests,
 * and so should be left in a disabled state unless required.
 *
 * It writes to the local database from compose.yaml. The users' password is given on the command line,
 * so none is kept in the source:
 *   ./mvnw test -Dtest=InitialiseDatabase -Dyottr.initialise=true -Dyottr.admin.password=...
 */

@SpringBootTest(properties = "spring.docker.compose.skip.in-tests=false")
public class InitialiseDatabase {

    private static final boolean ENABLED = Boolean.getBoolean("yottr.initialise"); // this should be left as false unless you're initialising the database

    private static final String PASSWORD_PROPERTY = "yottr.admin.password";

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Autowired
    private FinancialArrangementRepository financialArrangementRepository;

    @Autowired
    private FrequencyRepository frequencyRepository;

    @BeforeEach
    public void ensureRepositoryHasBeenInjected() {
        Assertions.assertNotNull(userRepository, "check spring configuration : userRepository was not injected");
        Assertions.assertNotNull(boatRepository, "check spring configuration : boatRepository was not injected");
        Assertions.assertNotNull(ryaSailCruisingLevelRepository, "check spring configuration : ryaSailCruisingLevelRepository was not injected");
        Assertions.assertNotNull(financialArrangementRepository, "check spring configuration : financialArrangementRepository was not injected");
        Assertions.assertNotNull(frequencyRepository, "check spring configuration : frequencyRepository was not injected");
    }

    @Test
    public void init() {
        if (!ENABLED) return;

        final String password = System.getProperty(PASSWORD_PROPERTY, "");
        Assertions.assertTrue(password.length() >= 8, "give the users' password, of 8 or more characters, with -D" + PASSWORD_PROPERTY + "=...");

        // the reference data comes from the Flyway migrations
        final User adminUser = setupUserWithRoles("mike", password, Role.ADMIN, Role.FREE, Role.CREW);
        updateListingsLimit(adminUser, 50);
        addBoatsWithOwner(adminUser, 15);

        final User userOne = setupUserWithRoles("UserOne", password, Role.CREW, Role.FREE);
        addBoatsWithOwner(userOne, 3);
    }

    private void updateListingsLimit(User user, int listingsLimit) {
        user.setMaxListings(listingsLimit);
        userRepository.save(user);
    }

    private void addBoatsWithOwner(User owner, int numberOfBoats) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.executeWithoutResult(status -> {

            for (int i = 0; i < numberOfBoats; i++) {
                addBoat(owner.getUsername() + "'s Rust Bucket " + i, owner);
            }

        });
    }

    private User setupUserWithRoles(String username, String password, Role... roles) {
        User user = new User();

        user.setUsername(username);
        user.setPassword(new BCryptPasswordEncoder().encode(password));

        for (Role role : roles) {
            user.addRole(role);
        }

        user.setEnabled(true);
        user.setTitle("Mr");
        user.setFirstName("Mike");
        user.setLastName("H");
        user.setEmail("m@yottr.co.uk");
        user.setAboutMe("Nothing to see here.");
        user.setCountry(Country.UK);
        user.setMobile("07973 000000");
        user.setPostcode("DY8 1AA");

        return userRepository.save(user);
    }

    private void addBoat(String makeAndModel, User owner) {
        Boat boat = new Boat(userRepository.findByUsername(owner.getUsername()));

        boat.setTitle("A Super " + makeAndModel);
        boat.setMakeAndModel(makeAndModel);
        boat.setLength(50);
        boat.setUnitsImperial(false);
        boat.setHullType(Boat.HullType.MONO);
        boat.setDescription("You want to sail around the world? You need to look elsewhere, my tub is purely a gin palace.");
        boat.setSailingPurpose(SailingPurpose.CRUISING);
        boat.setDateRelevantTo(LocalDate.of(2075, 3, 10));
        boat.setFinancialArrangement(FinancialArrangement.FOOD_ONLY);
        boat.setFrequency(Arrays.asList(Frequency.HOLIDAYS, Frequency.WEEKENDS));
        boat.setYearBuilt(1998);
        boat.setVesselType(VesselType.SAIL);
        boat.setRoleRequired(RoleRequired.CREW);

        final int rank = RyaSailCruisingLevel.YACHTMASTER_COASTAL.getRank();
        final RyaSailCruisingLevel level = ryaSailCruisingLevelRepository.findByRank(rank);
        boat.setMinimumRequiredLevel(level);

        boatRepository.save(boat);
    }
}

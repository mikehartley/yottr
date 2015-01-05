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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import uk.co.yottr.config.AppConfig;
import uk.co.yottr.model.*;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.FinancialArrangementRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;
import uk.co.yottr.repository.UserRepository;
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
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
@WebAppConfiguration
public class InitialiseDatabase {

    private static final boolean ENABLED = false; // this should be left as false unless you're initialising the database

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

    @Before
    public void ensureRepositoryHasBeenInjected() {
        Assert.assertNotNull("check spring configuration : userRepository was not injected", userRepository);
        Assert.assertNotNull("check spring configuration : boatRepository was not injected", boatRepository);
        Assert.assertNotNull("check spring configuration : ryaSailCruisingLevelRepository was not injected", ryaSailCruisingLevelRepository);
    }

    @Test
    public void init() {
        if (!ENABLED) return;

        initialise(ryaSailCruisingLevelRepository);
        initialise(financialArrangementRepository);

        final User adminUser = setupUserWithRoles("mike", Role.ADMIN, Role.FREE, Role.CREW);
        updateListingsLimit(adminUser, 50);
        addBoatsWithOwner(adminUser, 15);

        final User userOne = setupUserWithRoles("UserOne", Role.CREW, Role.FREE);
        addBoatsWithOwner(userOne, 3);
    }

    private void updateListingsLimit(User user, int listingsLimit) {
        user.setMaxListings(listingsLimit);
        userRepository.save(user);
    }

    private void addBoatsWithOwner(User owner, int numberOfBoats) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                for (int i = 0; i < numberOfBoats; i++) {
                    addBoat(owner.getUsername() + "'s Rust Bucket " + i, owner);
                }

            }
        });
    }

    public static void initialise(RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository) {
        if (ryaSailCruisingLevelRepository.findAll().iterator().hasNext()) return;

        final List<RyaSailCruisingLevel.Level> levels = Arrays.asList(RyaSailCruisingLevel.Level.values());
        for (RyaSailCruisingLevel.Level level : levels) {
            ryaSailCruisingLevelRepository.save(new RyaSailCruisingLevel(level));
        }
    }

    public static void initialise(FinancialArrangementRepository financialArrangementRepository) {
        if (financialArrangementRepository.findAll().iterator().hasNext()) return;

        final List<FinancialArrangement.FinancialArrangementEnum> arrangements = Arrays.asList(FinancialArrangement.FinancialArrangementEnum.values());
        for (FinancialArrangement.FinancialArrangementEnum arrangement : arrangements) {
            financialArrangementRepository.save(new FinancialArrangement(arrangement));
        }
    }

    private User setupUserWithRoles(String username, Role... roles) {
        User user = new User();

        user.setUsername(username);
        user.setPassword(new BCryptPasswordEncoder().encode("aph3xtwIn"));

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

        final int rank = RyaSailCruisingLevel.YACHTMASTER_COASTAL.getRank();
        final RyaSailCruisingLevel level = ryaSailCruisingLevelRepository.findByRank(rank);
        boat.setMinimumRequiredLevel(level);

        boatRepository.save(boat);
    }
}

package uk.co.yottr.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.initialise.InitialiseDatabase;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.IntegrationTestConfig;

import static org.junit.Assert.assertEquals;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.UserBuilder.aUser;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationTestConfig.class)
public class BoatRepositoryTest {

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private BoatService boatService;

    @Autowired
    private UserService userService;

    @Autowired
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Autowired
    private FinancialArrangementRepository financialArrangementRepository;

    private User owner;

    @Before
    public void initialiseReferenceData() {

        InitialiseDatabase.initialise(ryaSailCruisingLevelRepository);
        InitialiseDatabase.initialise(financialArrangementRepository);

        owner = userService.save(aUser().build(), false);
    }

    @After
    public void clearData() {
        for (Boat boat : boatRepository.findAll(new PageRequest(0, 9999))) {
            boatRepository.delete(boat);
        }
    }

    @Test
    public void testFind() throws Exception {
        Boat boat = aBoat().withOwner(owner).build();
        boatService.save(boat);

        final Page<Boat> foundBoatPages = boatRepository.find(boat.getReference(), new PageRequest(0, 10));
        assertEquals(1, foundBoatPages.getTotalElements());

        final Boat boatFromFind = foundBoatPages.getContent().get(0);
        assertEquals(boat.getReference(), boatFromFind.getReference());
    }
}
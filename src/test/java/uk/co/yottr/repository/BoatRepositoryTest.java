package uk.co.yottr.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.initialise.InitialiseDatabase;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.UserService;
import uk.co.yottr.testconfig.TestcontainersConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.UserBuilder.aUser;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@SpringBootTest
@Import(TestcontainersConfig.class)
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

    @BeforeEach
    public void initialiseReferenceData() {

        InitialiseDatabase.initialise(ryaSailCruisingLevelRepository);
        InitialiseDatabase.initialise(financialArrangementRepository);

        owner = userService.save(aUser().build(), false);
    }

    @AfterEach
    public void clearData() {
        for (Boat boat : boatRepository.findAll(PageRequest.of(0, 9999))) {
            boatRepository.delete(boat);
        }
    }

    @Test
    public void testFind() throws Exception {
        Boat boat = aBoat().withOwner(owner).build();
        boatService.save(boat);

        final Page<Boat> foundBoatPages = boatRepository.find(boat.getReference(), PageRequest.of(0, 10));
        assertEquals(1, foundBoatPages.getTotalElements());

        final Boat boatFromFind = foundBoatPages.getContent().get(0);
        assertEquals(boat.getReference(), boatFromFind.getReference());
    }
}
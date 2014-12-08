package uk.co.yottr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.initialise.InitialiseDatabase;
import uk.co.yottr.repository.FinancialArrangementRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;
import uk.co.yottr.testconfig.IntegrationTestConfig;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationTestConfig.class)
public class ReferenceDataServiceIT {

    @Autowired
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Autowired
    private FinancialArrangementRepository financialArrangementRepository;

    @Before
    public void deleteAllBeforeEachTest() {
        ryaSailCruisingLevelRepository.deleteAll();
        financialArrangementRepository.deleteAll();
    }

    @Test
    public void ryaSailCruisingLevels() {
        InitialiseDatabase.initialise(ryaSailCruisingLevelRepository);
        assertEquals(8, ryaSailCruisingLevelRepository.findAll().spliterator().getExactSizeIfKnown());
    }

    @Test
    public void financialArrangements() {
        InitialiseDatabase.initialise(financialArrangementRepository);
        assertEquals(5, financialArrangementRepository.findAll().spliterator().getExactSizeIfKnown());
    }
}
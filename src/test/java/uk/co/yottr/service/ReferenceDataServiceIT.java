package uk.co.yottr.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.initialise.InitialiseDatabase;
import uk.co.yottr.repository.FinancialArrangementRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;
import uk.co.yottr.testconfig.TestcontainersConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@SpringBootTest
@Import(TestcontainersConfig.class)
public class ReferenceDataServiceIT {

    @Autowired
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Autowired
    private FinancialArrangementRepository financialArrangementRepository;

    @BeforeEach
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
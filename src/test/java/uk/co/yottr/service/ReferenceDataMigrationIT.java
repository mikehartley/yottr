package uk.co.yottr.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.model.FinancialArrangement;
import uk.co.yottr.model.Frequency;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.repository.FinancialArrangementRepository;
import uk.co.yottr.repository.FrequencyRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * The reference data that the V2 migration loads must match the enums the code uses.
 */
@SpringBootTest
@Import(TestcontainersConfig.class)
public class ReferenceDataMigrationIT {

    @Autowired
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Autowired
    private FinancialArrangementRepository financialArrangementRepository;

    @Autowired
    private FrequencyRepository frequencyRepository;

    @Test
    public void ryaSailCruisingLevels() {
        final Map<Integer, String> expected = Arrays.stream(RyaSailCruisingLevel.Level.values())
                .collect(Collectors.toMap(level -> level.rank, level -> level.displayName));
        final Map<Integer, String> stored = StreamSupport.stream(ryaSailCruisingLevelRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(RyaSailCruisingLevel::getRank, RyaSailCruisingLevel::getDisplayName));

        assertEquals(expected, stored);
    }

    @Test
    public void financialArrangements() {
        final Map<String, String> expected = Arrays.stream(FinancialArrangement.FinancialArrangementEnum.values())
                .collect(Collectors.toMap(Enum::name, FinancialArrangement.FinancialArrangementEnum::getDisplayName));
        final Map<String, String> stored = StreamSupport.stream(financialArrangementRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(FinancialArrangement::getName, FinancialArrangement::getDisplayName));

        assertEquals(expected, stored);
    }

    @Test
    public void frequencies() {
        final Map<String, String> expected = Arrays.stream(Frequency.FrequencyEnum.values())
                .collect(Collectors.toMap(Enum::name, Frequency.FrequencyEnum::getDisplayName));
        final Map<String, String> stored = StreamSupport.stream(frequencyRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(Frequency::getName, Frequency::getDisplayName));

        assertEquals(expected, stored);
    }
}

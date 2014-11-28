package uk.co.yottr.service;

import org.junit.Before;
import org.junit.Test;
import uk.co.yottr.model.*;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class ReferenceDataServiceTest {

    private ReferenceDataService referenceDataService;

    @Before
    public void setup() {
        referenceDataService = new ReferenceDataServiceImpl();
    }

    @Test
    public void testRyaSailCruisingLevelsRank() throws Exception {
        final List<RyaSailCruisingLevel> cruisingLevels = referenceDataService.ryaSailCruisingLevels();

        assertEquals(RyaSailCruisingLevel.NONE.getRank(), cruisingLevels.get(0).getRank());
        assertTrue(cruisingLevels.get(0).getRank() < cruisingLevels.get(1).getRank());
        assertTrue(cruisingLevels.get(1).getRank() < cruisingLevels.get(2).getRank());
        assertTrue(cruisingLevels.get(2).getRank() < cruisingLevels.get(3).getRank());
        assertTrue(cruisingLevels.get(3).getRank() < cruisingLevels.get(4).getRank());
        assertTrue(cruisingLevels.get(4).getRank() < cruisingLevels.get(5).getRank());
        assertTrue(cruisingLevels.get(5).getRank() < cruisingLevels.get(6).getRank());
        assertTrue(cruisingLevels.get(6).getRank() < cruisingLevels.get(7).getRank());
        assertEquals(RyaSailCruisingLevel.YACHTMASTER_OCEAN.getRank(), cruisingLevels.get(7).getRank());
    }

    @Test
    public void ryaSailCruisingLevelsDisplayName() {
        final List<RyaSailCruisingLevel> cruisingLevels = referenceDataService.ryaSailCruisingLevels();

        assertEquals(RyaSailCruisingLevel.NONE.getDisplayName(), cruisingLevels.get(0).getDisplayName());
        assertEquals(RyaSailCruisingLevel.START_YACHTING.getDisplayName(), cruisingLevels.get(1).getDisplayName());
        assertEquals(RyaSailCruisingLevel.COMPETENT_CREW.getDisplayName(), cruisingLevels.get(2).getDisplayName());
        assertEquals(RyaSailCruisingLevel.DAY_SKIPPER.getDisplayName(), cruisingLevels.get(3).getDisplayName());
        assertEquals(RyaSailCruisingLevel.COASTAL_SKIPPER.getDisplayName(), cruisingLevels.get(4).getDisplayName());
        assertEquals(RyaSailCruisingLevel.YACHTMASTER_COASTAL.getDisplayName(), cruisingLevels.get(5).getDisplayName());
        assertEquals(RyaSailCruisingLevel.YACHTMASTER_OFFSHORE.getDisplayName(), cruisingLevels.get(6).getDisplayName());
        assertEquals(RyaSailCruisingLevel.YACHTMASTER_OCEAN.getDisplayName(), cruisingLevels.get(7).getDisplayName());
        assertEquals("list size", 8, cruisingLevels.size());
    }

    @Test
    public void testSailingStyles() throws Exception {
        final List<SailingStyle> sailingStyles = referenceDataService.sailingStyles();

        assertEquals(SailingStyle.CRUISING, sailingStyles.get(0));
        assertEquals(SailingStyle.RACING, sailingStyles.get(1));
        assertEquals(SailingStyle.ALL, sailingStyles.get(2));
    }
    
    @Test
    public void testHullTypes() throws Exception {
        final List<Boat.HullType> hullTypes = referenceDataService.hullTypes();

        assertEquals(Boat.HullType.MONO, hullTypes.get(0));
        assertEquals(Boat.HullType.MULTI, hullTypes.get(1));
    }

    @Test
    public void testCountries() throws Exception {
        final List<Country> countries = referenceDataService.countries();

        assertEquals(Country.UK.getDisplayName(), countries.get(0).getDisplayName());
        assertEquals(Country.OTHER.getDisplayName(), countries.get(1).getDisplayName());
    }

    @Test
    public void testFinancialArrangements() throws Exception {
        final List<FinancialArrangement> financialArrangements = referenceDataService.financialArrangements();

        assertEquals(FinancialArrangement.FREE.getDisplayName(), financialArrangements.get(0).getDisplayName());
        assertEquals(FinancialArrangement.PAY_THEM_COST.getDisplayName(), financialArrangements.get(1).getDisplayName());
        assertEquals(FinancialArrangement.PAY_THEM_COMMERCIAL.getDisplayName(), financialArrangements.get(2).getDisplayName());
        assertEquals(FinancialArrangement.PAY_ME_COST.getDisplayName(), financialArrangements.get(3).getDisplayName());
        assertEquals(FinancialArrangement.PAY_ME_COMMERCIAL.getDisplayName(), financialArrangements.get(4).getDisplayName());
    }
}
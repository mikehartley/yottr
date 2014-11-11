package uk.co.yottr.service;

import org.junit.Before;
import org.junit.Test;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingStyle;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class ReferenceDataServiceImplTest {

    private ReferenceDataService referenceDataService;

    @Before
    public void setup() {
        referenceDataService = new ReferenceDataServiceImpl();
    }

    @Test
    public void testRyaSailCruisingLevels() throws Exception {
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
}
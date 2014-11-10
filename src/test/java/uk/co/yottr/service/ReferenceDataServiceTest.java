package uk.co.yottr.service;

import org.junit.Before;
import org.junit.Test;
import uk.co.yottr.model.RyaSailCruisingLevel;

import java.util.List;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class ReferenceDataServiceTest {

    private ReferenceDataServiceImpl referenceDataService;

    @Before
    public void setUp() throws Exception {
        referenceDataService = new ReferenceDataServiceImpl();
    }

    @Test
    public void ryaSailCruisingLevels() {
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
}

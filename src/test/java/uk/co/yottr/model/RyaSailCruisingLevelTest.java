package uk.co.yottr.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class RyaSailCruisingLevelTest {

    @Test
    public void checkRankIsCorrectlyOrdered() {
        assertTrue(RyaSailCruisingLevel.Level.NONE.rank < RyaSailCruisingLevel.Level.START_YACHTING.rank);
        assertTrue(RyaSailCruisingLevel.Level.START_YACHTING.rank < RyaSailCruisingLevel.Level.COMPETENT_CREW.rank);
        assertTrue(RyaSailCruisingLevel.Level.COMPETENT_CREW.rank < RyaSailCruisingLevel.Level.DAY_SKIPPER.rank);
        assertTrue(RyaSailCruisingLevel.Level.DAY_SKIPPER.rank < RyaSailCruisingLevel.Level.COASTAL_SKIPPER.rank);
        assertTrue(RyaSailCruisingLevel.Level.COASTAL_SKIPPER.rank < RyaSailCruisingLevel.Level.YACHTMASTER_COASTAL.rank);
        assertTrue(RyaSailCruisingLevel.Level.YACHTMASTER_COASTAL.rank < RyaSailCruisingLevel.Level.YACHTMASTER_OFFSHORE.rank);
        assertTrue(RyaSailCruisingLevel.Level.YACHTMASTER_OFFSHORE.rank < RyaSailCruisingLevel.Level.YACHTMASTER_OCEAN.rank);
    }

    @Test
    public void checkConstantNone() {
        assertEquals(RyaSailCruisingLevel.Level.NONE.rank, RyaSailCruisingLevel.NONE.getRank());
        assertEquals(RyaSailCruisingLevel.Level.NONE.displayName, RyaSailCruisingLevel.NONE.getDisplayName());
    }

    @Test
    public void checkConstantStartYachting() {
        assertEquals(RyaSailCruisingLevel.Level.START_YACHTING.rank, RyaSailCruisingLevel.START_YACHTING.getRank());
        assertEquals(RyaSailCruisingLevel.Level.START_YACHTING.displayName, RyaSailCruisingLevel.START_YACHTING.getDisplayName());
    }

    @Test
    public void checkConstantCompetentCrew() {
        assertEquals(RyaSailCruisingLevel.Level.COMPETENT_CREW.rank, RyaSailCruisingLevel.COMPETENT_CREW.getRank());
        assertEquals(RyaSailCruisingLevel.Level.COMPETENT_CREW.displayName, RyaSailCruisingLevel.COMPETENT_CREW.getDisplayName());
    }

    @Test
    public void checkConstantDaySkipper() {
        assertEquals(RyaSailCruisingLevel.Level.DAY_SKIPPER.rank, RyaSailCruisingLevel.DAY_SKIPPER.getRank());
        assertEquals(RyaSailCruisingLevel.Level.DAY_SKIPPER.displayName, RyaSailCruisingLevel.DAY_SKIPPER.getDisplayName());
    }

    @Test
    public void checkConstantCoastalSkipper() {
        assertEquals(RyaSailCruisingLevel.Level.COASTAL_SKIPPER.rank, RyaSailCruisingLevel.COASTAL_SKIPPER.getRank());
        assertEquals(RyaSailCruisingLevel.Level.COASTAL_SKIPPER.displayName, RyaSailCruisingLevel.COASTAL_SKIPPER.getDisplayName());
    }

    @Test
    public void checkConstantYachtmasterCoastal() {
        assertEquals(RyaSailCruisingLevel.Level.YACHTMASTER_COASTAL.rank, RyaSailCruisingLevel.YACHTMASTER_COASTAL.getRank());
        assertEquals(RyaSailCruisingLevel.Level.YACHTMASTER_COASTAL.displayName, RyaSailCruisingLevel.YACHTMASTER_COASTAL.getDisplayName());
    }

    @Test
    public void checkConstantYachtmasterOffshore() {
        assertEquals(RyaSailCruisingLevel.Level.YACHTMASTER_OFFSHORE.rank, RyaSailCruisingLevel.YACHTMASTER_OFFSHORE.getRank());
        assertEquals(RyaSailCruisingLevel.Level.YACHTMASTER_OFFSHORE.displayName, RyaSailCruisingLevel.YACHTMASTER_OFFSHORE.getDisplayName());
    }

    @Test
    public void checkConstantYachtmasterOcean() {
        assertEquals(RyaSailCruisingLevel.Level.YACHTMASTER_OCEAN.rank, RyaSailCruisingLevel.YACHTMASTER_OCEAN.getRank());
        assertEquals(RyaSailCruisingLevel.Level.YACHTMASTER_OCEAN.displayName, RyaSailCruisingLevel.YACHTMASTER_OCEAN.getDisplayName());
    }
}
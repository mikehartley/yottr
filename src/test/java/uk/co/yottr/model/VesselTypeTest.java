package uk.co.yottr.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class VesselTypeTest {
    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("Sail", VesselType.SAIL.getDisplayName());
        assertEquals("Power", VesselType.POWER.getDisplayName());
    }
}
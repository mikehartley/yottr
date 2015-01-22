package uk.co.yottr.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class SailingPurposeTest {
    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("Cruising", SailingPurpose.CRUISING.getDisplayName());
        assertEquals("Racing", SailingPurpose.RACING.getDisplayName());
        assertEquals("Long Term", SailingPurpose.LONG_TERM.getDisplayName());
        assertEquals("Delivery", SailingPurpose.DELIVERY.getDisplayName());
        assertEquals("Course", SailingPurpose.COURSE.getDisplayName());
    }
}
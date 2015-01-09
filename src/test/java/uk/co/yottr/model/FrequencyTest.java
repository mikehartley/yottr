package uk.co.yottr.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class FrequencyTest {

    @Test
    public void testGetName() throws Exception {
        assertEquals("WEEKDAYS", Frequency.WEEKDAYS.getName());
        assertEquals("WEEKENDS", Frequency.WEEKENDS.getName());
        assertEquals("HOLIDAYS", Frequency.HOLIDAYS.getName());
        assertEquals("LONG_TERM", Frequency.LONG_TERM.getName());
        assertEquals("TRIP", Frequency.TRIP.getName());
    }

    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("Weekdays", Frequency.WEEKDAYS.getDisplayName());
        assertEquals("Weekends", Frequency.WEEKENDS.getDisplayName());
        assertEquals("Holidays", Frequency.HOLIDAYS.getDisplayName());
        assertEquals("Long Term", Frequency.LONG_TERM.getDisplayName());
        assertEquals("Trip", Frequency.TRIP.getDisplayName());
    }
}
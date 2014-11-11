package uk.co.yottr.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class CountryTest {

    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("UK", Country.UK.getDisplayName());
        assertEquals("Other", Country.OTHER.getDisplayName());
    }
}
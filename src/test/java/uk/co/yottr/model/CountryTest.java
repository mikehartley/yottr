package uk.co.yottr.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class CountryTest {

    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("UK", Country.UK.getDisplayName());
        assertEquals("Other", Country.OTHER.getDisplayName());
    }
}
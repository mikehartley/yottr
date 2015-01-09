package uk.co.yottr.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class SmokingTest {
    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("No", Smoking.NO.getDisplayName());
        assertEquals("Yes - outside only", Smoking.YES_OUTSIDE.getDisplayName());
        assertEquals("Yes - inside and outside", Smoking.YES_ANYWHERE.getDisplayName());
    }
}
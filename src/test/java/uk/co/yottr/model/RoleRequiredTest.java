package uk.co.yottr.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class RoleRequiredTest {

    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("Crew", RoleRequired.CREW.getDisplayName());
        assertEquals("Mate", RoleRequired.MATE.getDisplayName());
        assertEquals("Skipper", RoleRequired.SKIPPER.getDisplayName());
        assertEquals("Don't matter", RoleRequired.DOESNT_MATTER.getDisplayName());
    }
}
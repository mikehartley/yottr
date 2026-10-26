package uk.co.yottr.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleRequiredTest {

    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("Crew", RoleRequired.CREW.getDisplayName());
        assertEquals("Mate", RoleRequired.MATE.getDisplayName());
        assertEquals("Skipper", RoleRequired.SKIPPER.getDisplayName());
        assertEquals("Don't matter", RoleRequired.DOESNT_MATTER.getDisplayName());
    }
}
package uk.co.yottr.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VesselTypeTest {
    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("Sail", VesselType.SAIL.getDisplayName());
        assertEquals("Power", VesselType.POWER.getDisplayName());
    }
}
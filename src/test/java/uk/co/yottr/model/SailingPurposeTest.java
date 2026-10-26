package uk.co.yottr.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
package uk.co.yottr.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountryTest {

    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("UK", Country.UK.getDisplayName());
        assertEquals("Other", Country.OTHER.getDisplayName());
    }
}
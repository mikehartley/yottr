package uk.co.yottr.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SmokingTest {
    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("No", Smoking.NO.getDisplayName());
        assertEquals("Yes - outside only", Smoking.YES_OUTSIDE.getDisplayName());
        assertEquals("Yes - inside and outside", Smoking.YES_ANYWHERE.getDisplayName());
    }
}
package uk.co.yottr.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class CourseLevelTest {
    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("Start Yachting", CourseLevel.START_YACHTING.getDisplayName());
        assertEquals("Competent Crew", CourseLevel.COMP_CREW.getDisplayName());
        assertEquals("Day Skipper", CourseLevel.DAY_SKIPPER.getDisplayName());
        assertEquals("Coastal Skipper Practical Week", CourseLevel.COASTAL_SKIPPER_WK.getDisplayName());
        assertEquals("Yachtmaster Coastal", CourseLevel.YACHTMASTER_COASTAL.getDisplayName());
        assertEquals("Yachtmaster Offshore", CourseLevel.YACHTMASTER_OFFSHORE.getDisplayName());
        assertEquals("Yachtmaster Ocean", CourseLevel.YACHTMASTER_OCEAN.getDisplayName());
    }
}
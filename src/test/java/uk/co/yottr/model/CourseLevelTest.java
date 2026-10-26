package uk.co.yottr.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
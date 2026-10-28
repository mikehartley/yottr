package uk.co.yottr.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LikeTest {

    @Test
    public void likeWildcardsInTheSearchAreMatchedAsThemselves() {
        assertEquals("100\\% crew\\_wanted", Like.escape("100% crew_wanted"));
        assertEquals("back\\\\slash", Like.escape("back\\slash"));
        assertEquals("plain", Like.escape("plain"));
    }

    @Test
    public void thePatternFindsTheTextAnywhereIgnoringCase() {
        assertEquals("%solent\\_crew%", Like.containing("Solent_Crew"));
    }
}

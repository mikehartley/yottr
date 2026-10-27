package uk.co.yottr.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoatSpecificationsTest {

    @Test
    public void likeWildcardsInTheSearchAreMatchedAsThemselves() {
        assertEquals("100\\% crew\\_wanted", BoatSpecifications.escapeLike("100% crew_wanted"));
        assertEquals("back\\\\slash", BoatSpecifications.escapeLike("back\\slash"));
        assertEquals("plain", BoatSpecifications.escapeLike("plain"));
    }
}

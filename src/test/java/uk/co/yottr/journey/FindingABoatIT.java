package uk.co.yottr.journey;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Crew finding a boat, and getting in touch with its owner.
 */
class FindingABoatIT extends Journey {

    @Test
    void crewFindABoatAndHowToGetInTouchWithItsOwner() {
        final Person alice = member("alice");
        final Person dave = member("dave");
        alice.signsIn();
        alice.postsAListingFor("Moody 31");
        alice.postsAListingFor("Westerly Konsort");
        dave.signsIn();

        assertThat(dave.searchesTheBoatsFor("moody")).contains("Moody 31").doesNotContain("Westerly Konsort");

        dave.opensTheListingFor("Moody 31");
        assertThat(dave.currentMenuItem()).isEqualTo("Boats");
        assertThat(dave.sees()).contains("Get in touch", alice.username() + "@example.test", "07700 900123");
    }

    @Test
    void anOwnerSeesTheirOwnListingAsTheirs() {
        final Person alice = member("alice");
        alice.signsIn();
        alice.postsAListingFor("Moody 31");

        alice.opensTheListingFor("Moody 31");
        assertThat(alice.sees()).contains("Your listing").doesNotContain("Get in touch");
    }
}

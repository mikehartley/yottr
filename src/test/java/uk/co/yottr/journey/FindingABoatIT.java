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
    void crewFindABoatByWhereItSails() {
        final Person alice = member("alice");
        final Person dave = member("dave");
        alice.signsIn();
        alice.postsAListingFrom("Moody 31", "SOUTH_COAST", "Hamble");
        alice.postsAListingFrom("Westerly Konsort", "WEST_SCOTLAND", "Oban");
        dave.signsIn();

        assertThat(dave.searchesTheBoatsIn("West of Scotland")).contains("Westerly Konsort").doesNotContain("Moody 31");
        assertThat(dave.searchesTheBoatsFor("hamble")).contains("Moody 31").doesNotContain("Westerly Konsort");
        assertThat(dave.cardOnAllBoats("Westerly Konsort")).contains("Oban, West of Scotland");
    }

    @Test
    void anOwnerSeesTheirOwnListingAsTheirs() {
        final Person alice = member("alice");
        alice.signsIn();
        alice.postsAListingFor("Moody 31");

        alice.opensTheListingFor("Moody 31");
        assertThat(alice.sees()).contains("Your listing").doesNotContain("Get in touch");
    }

    @Test
    void crewSaveABoatAndSeeWhenItIsNoLongerAvailable() {
        final Person alice = member("alice");
        final Person dave = member("dave");
        alice.signsIn();
        alice.postsAListingFor("Sadler 34");
        dave.signsIn();

        dave.savesTheListingFor("Sadler 34");
        assertThat(dave.savedBoats()).containsExactly("Sadler 34");
        assertThat(dave.currentMenuItem()).isEqualTo("Saved");

        alice.suspendsTheirListing("Sadler 34");
        assertThat(dave.savedBoat("Sadler 34")).contains("No longer available");

        alice.reEnablesTheirListing("Sadler 34");
        assertThat(dave.savedBoat("Sadler 34")).doesNotContain("No longer available");

        dave.removesTheSavedBoat("Sadler 34");
        assertThat(dave.savedBoats()).isEmpty();
    }
}

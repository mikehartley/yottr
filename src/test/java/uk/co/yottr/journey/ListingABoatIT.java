package uk.co.yottr.journey;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Owners listing their boats, and what crew see of them.
 */
class ListingABoatIT extends Journey {

    @Test
    void anOwnerPostsEditsAndDeletesTheirListings() {
        final Person alice = member("alice");
        alice.signsIn();

        alice.postsAListingFor("Sigma 33");
        assertThat(alice.sees()).contains("Boat Saved Successfully.");
        alice.postsAListingFor("Contessa 32");
        assertThat(alice.myListings()).containsExactlyInAnyOrder("Sigma 33", "Contessa 32");
        assertThat(alice.currentMenuItem()).isEqualTo("My Listings");

        alice.changesTheMakeOfTheirListing("Sigma 33", "Sigma 38");
        assertThat(alice.myListings()).containsExactlyInAnyOrder("Sigma 38", "Contessa 32");

        alice.deletesTheirListing("Sigma 38");
        assertThat(alice.myListings()).containsExactly("Contessa 32");
    }

    @Test
    void crewCantFindASuspendedListingUntilItsOwnerReEnablesIt() {
        final Person alice = member("alice");
        final Person dave = member("dave");
        alice.signsIn();
        dave.signsIn();
        alice.postsAListingFor("Westerly Fulmar");

        alice.suspendsTheirListing("Westerly Fulmar");
        assertThat(alice.myListing("Westerly Fulmar")).contains("Suspended");
        assertThat(dave.searchesTheBoatsFor("Westerly Fulmar")).doesNotContain("Westerly Fulmar");

        alice.reEnablesTheirListing("Westerly Fulmar");
        assertThat(dave.searchesTheBoatsFor("Westerly Fulmar")).contains("Westerly Fulmar");
    }

    @Test
    void anOwnerAtTheirLimitCanPostAnotherOnceAnAdministratorRaisesIt() {
        final Person dave = member("dave", 0);
        final Person ada = administrator("ada");
        dave.signsIn();

        dave.startsANewListing();
        assertThat(dave.sees()).contains("You have reached the limit");

        ada.signsIn();
        ada.changesTheListingLimitOf(dave, 1);

        dave.postsAListingFor("Hurley 22");
        assertThat(dave.myListings()).containsExactly("Hurley 22");
    }
}

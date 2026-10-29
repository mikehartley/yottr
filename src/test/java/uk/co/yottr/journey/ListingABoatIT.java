package uk.co.yottr.journey;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.yottr.builder.TestPictures.jpeg;

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
    void anOwnerAddsPicturesThatCrewSeeWhileTheListingIsUp() {
        final Person alice = member("alice");
        final Person dave = member("dave");
        alice.signsIn();
        dave.signsIn();
        alice.postsAListingFor("Nicholson 32");

        alice.addsPicturesToTheirListing("Nicholson 32", jpeg(800, 600), jpeg(600, 800));
        assertThat(alice.sees()).contains("Added 2 pictures.");

        assertThat(dave.seesAPictureOnTheCardFor("Nicholson 32")).isTrue();
        dave.opensTheListingFor("Nicholson 32");
        final List<String> pictures = dave.picturesOnTheListing();
        assertThat(pictures).hasSize(2);
        assertThat(dave.fetches(pictures.get(1))).isEqualTo("image/jpeg");
        assertThat(dave.pictureShown()).isEqualTo("1 of 2");
        dave.showsTheNextPicture();
        assertThat(dave.pictureShown()).isEqualTo("2 of 2");
        dave.showsTheNextPicture();
        assertThat(dave.pictureShown()).as("round to the first").isEqualTo("1 of 2");
        dave.picksPicture(2);
        assertThat(dave.pictureShown()).isEqualTo("2 of 2");

        // dave's browser keeps the picture it has for a day, so this is the one it hasn't fetched
        alice.suspendsTheirListing("Nicholson 32");
        assertThat(dave.fetches(pictures.get(0))).isEqualTo("404");
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

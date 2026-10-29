package uk.co.yottr.journey;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Members looking after their own account.
 */
class MyAccountIT extends Journey {

    @Test
    void aMemberChangesTheirPasswordAndSignsInWithTheNewOne() {
        final Person alice = member("alice");
        alice.signsIn();

        alice.changesTheirPasswordTo("a-brand-new-password");
        assertThat(alice.currentMenuItem()).isEqualTo("My Details");
        alice.signsOut();

        alice.signsInWith(Person.PASSWORD);
        assertThat(alice.isSignedIn()).as("signed in with the old password").isFalse();
        alice.signsIn();
        assertThat(alice.isSignedIn()).as("signed in with the new password").isTrue();
    }

    @Test
    void aMemberWhoClosesTheirAccountTakesTheirListingsWithThem() {
        final Person alice = member("alice");
        final Person dave = member("dave");
        alice.signsIn();
        alice.postsAListingFor("Rival 34");
        dave.signsIn();
        assertThat(dave.searchesTheBoatsFor("Rival 34")).contains("Rival 34");

        alice.closesTheirAccount();
        assertThat(alice.sees()).contains("You have signed out.");
        alice.signsIn();
        assertThat(alice.isSignedIn()).isFalse();

        assertThat(dave.searchesTheBoatsFor("Rival 34")).doesNotContain("Rival 34");
    }

    @Test
    void aMemberSeesOnlyWhatsRelevantToCrewOrOwnersAsTheySay() {
        final Person dave = member("dave");
        dave.signsIn();
        assertThat(dave.menu()).contains("Saved", "Crewing", "My Listings", "Find crew", "Add a boat");

        dave.saysTheyAre(true, false);
        assertThat(dave.menu()).contains("Boats", "Saved", "Crewing", "My Details").doesNotContain("My Listings", "Find crew", "Add a boat");
        assertThat(dave.sees()).contains("My crew profile");

        dave.saysTheyAre(false, true);
        assertThat(dave.menu()).contains("Boats", "My Listings", "Find crew", "Add a boat").doesNotContain("Saved", "Crewing");
        assertThat(dave.sees()).doesNotContain("My crew profile");

        dave.saysTheyAre(false, false);
        assertThat(dave.sees()).contains("Tick at least one.");
    }
}

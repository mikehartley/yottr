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
}

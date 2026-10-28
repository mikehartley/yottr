package uk.co.yottr.journey;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Administrators looking after the members.
 */
class LookingAfterMembersIT extends Journey {

    @Test
    void anAdministratorFindsAMemberAndCorrectsTheirDetails() {
        final Person alice = member("alice");
        member("bob");
        final Person ada = administrator("ada");
        ada.signsIn();

        assertThat(ada.findsMembers(alice.username())).containsExactly(alice.username());
        assertThat(ada.currentMenuItem()).isEqualTo("Members");

        ada.changesTheLastNameOf(alice, "Fairweather");
        assertThat(ada.sees()).contains("Saved the details of " + alice.username());
        ada.findsMembers(alice.username());
        assertThat(ada.sees()).contains("alice Fairweather");
    }

    @Test
    void aDisabledMemberIsSignedOutAndTheirListingsDisappearUntilTheyAreEnabled() {
        final Person alice = member("alice");
        final Person dave = member("dave");
        final Person ada = administrator("ada");
        alice.signsIn();
        alice.postsAListingFor("Nicholson 32");
        dave.signsIn();
        ada.signsIn();

        ada.disables(alice);
        alice.refreshes();
        assertThat(alice.sees()).contains("You have been signed out because your account was disabled or deleted.");
        alice.signsIn();
        assertThat(alice.isSignedIn()).isFalse();
        assertThat(dave.searchesTheBoatsFor("Nicholson 32")).doesNotContain("Nicholson 32");

        ada.enables(alice);
        alice.signsIn();
        assertThat(alice.isSignedIn()).isTrue();
        assertThat(dave.searchesTheBoatsFor("Nicholson 32")).contains("Nicholson 32");
    }
}

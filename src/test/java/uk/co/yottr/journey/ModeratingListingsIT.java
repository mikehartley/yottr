package uk.co.yottr.journey;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Administrators taking down listings that shouldn't be on the site.
 */
class ModeratingListingsIT extends Journey {

    @Test
    void aWithdrawnListingIsHiddenUntilAnAdministratorPutsItBack() {
        final Person alice = member("alice");
        final Person dave = member("dave");
        final Person ada = administrator("ada");
        alice.signsIn();
        alice.postsAListingFor("Hallberg-Rassy 42");
        dave.signsIn();
        ada.signsIn();

        ada.opensTheListingFor("Hallberg-Rassy 42");
        ada.withdrawsTheListing();
        assertThat(ada.sees()).contains("Withdrawn by an administrator");

        assertThat(dave.searchesTheBoatsFor("Hallberg-Rassy 42")).doesNotContain("Hallberg-Rassy 42");
        // suspending and re-enabling is the owner's own switch, and doesn't undo a withdrawal
        alice.suspendsTheirListing("Hallberg-Rassy 42");
        alice.reEnablesTheirListing("Hallberg-Rassy 42");
        assertThat(alice.myListing("Hallberg-Rassy 42")).contains("Withdrawn by an administrator");
        assertThat(dave.searchesTheBoatsFor("Hallberg-Rassy 42")).doesNotContain("Hallberg-Rassy 42");

        ada.opensTheListingOf(alice, "Hallberg-Rassy 42");
        ada.putsTheListingBack();
        assertThat(dave.searchesTheBoatsFor("Hallberg-Rassy 42")).contains("Hallberg-Rassy 42");
    }

    @Test
    void anAdministratorDeletesAListing() {
        final Person alice = member("alice");
        final Person ada = administrator("ada");
        alice.signsIn();
        alice.postsAListingFor("Contessa 26");
        ada.signsIn();

        ada.opensTheListingFor("Contessa 26");
        ada.deletesTheListing();
        assertThat(ada.sees()).contains("Deleted the listing");

        assertThat(alice.myListings()).doesNotContain("Contessa 26");
    }
}

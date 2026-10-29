package uk.co.yottr.journey;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Administrators keeping the site running.
 */
class RunningTheSiteIT extends Journey {

    private static final String CONTROLLERS = "uk.co.yottr.controller";

    @Test
    void anAdministratorTurnsOnDebugLoggingForOnePackageAndBackOff() {
        final Person ada = administrator("ada");
        ada.signsIn();

        ada.setsTheLogLevel(CONTROLLERS, "DEBUG");
        assertThat(ada.logLevelOf(CONTROLLERS)).isEqualTo(List.of("DEBUG", "DEBUG"));

        ada.setsTheLogLevel(CONTROLLERS, "");
        assertThat(ada.logLevelOf(CONTROLLERS).get(0)).as("its own level").isEqualTo("-");
    }

    @Test
    void anAdministratorBlocksARangeOfAddressesButNotTheirOwn() {
        final Person ada = administrator("ada");
        ada.signsIn();

        assertThat(ada.blocks("203.0.113.0/24", "Spam listings")).isEmpty();
        assertThat(ada.blockedAddresses()).contains("203.0.113.0/24", "Spam listings");

        final String own = ada.ownAddressOnTheBlockedPage();
        assertThat(ada.blocks(own, "")).contains("your own address");

        ada.unblocks("203.0.113.0/24");
        assertThat(ada.blockedAddresses()).contains("Nothing is blocked");
    }
}

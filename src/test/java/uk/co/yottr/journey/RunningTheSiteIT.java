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
}

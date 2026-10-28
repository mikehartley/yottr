package uk.co.yottr.journey;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Visitors joining the site, and members signing in and out.
 */
class JoiningIT extends Journey {

    @Test
    void aVisitorRegistersThenSignsInAndOut() {
        final Person carol = visitor("carol");

        carol.registers();
        assertThat(carol.sees()).contains("welcome to Yottr");

        carol.signsInWith("not-the-password");
        assertThat(carol.sees()).contains("Invalid username and password.");
        assertThat(carol.isSignedIn()).isFalse();

        carol.signsIn();
        assertThat(carol.isSignedIn()).isTrue();

        carol.signsOut();
        assertThat(carol.sees()).contains("You have signed out.");
        assertThat(carol.isSignedIn()).isFalse();
    }
}

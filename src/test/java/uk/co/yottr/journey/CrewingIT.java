package uk.co.yottr.journey;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Members joining the crew of a boat they sail on.
 */
class CrewingIT extends Journey {

    @Test
    void crewJoinABoatOnceItsOwnerAccepts() {
        final Person alice = member("alice");
        final Person dave = member("dave");
        final Person erin = member("erin");
        alice.signsIn();
        alice.postsAListingFor("Moody 31");
        dave.signsIn();
        erin.signsIn();

        dave.asksToJoinTheCrewOf("Moody 31");
        assertThat(dave.crewPanelOf("Moody 31")).contains("You've asked to join the crew");
        assertThat(dave.boatsTheyCrewOn()).contains("Moody 31", "Asked to join");
        erin.asksToJoinTheCrewOf("Moody 31");

        assertThat(alice.crewPanelOf("Moody 31")).contains("Asking to join", dave.name() + " Sailor", erin.name() + " Sailor");
        alice.answersCrew("Moody 31", dave, "Accept");
        alice.answersCrew("Moody 31", erin, "Turn down");

        assertThat(dave.crewPanelOf("Moody 31")).contains("You're in the crew");
        assertThat(dave.boatsTheyCrewOn()).contains("Moody 31", "In the crew since");
        assertThat(erin.crewPanelOf("Moody 31")).contains("Ask to join the crew").doesNotContain(dave.name());

        alice.answersCrew("Moody 31", dave, "Remove");
        assertThat(dave.boatsTheyCrewOn()).doesNotContain("Moody 31");
    }

    @Test
    void anOwnerCantJoinTheirOwnCrew() {
        final Person alice = member("alice");
        alice.signsIn();
        alice.postsAListingFor("Moody 31");

        assertThat(alice.crewPanelOf("Moody 31")).contains("No crew yet").doesNotContain("Ask to join");
    }
}

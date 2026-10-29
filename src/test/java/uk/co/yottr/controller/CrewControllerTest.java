package uk.co.yottr.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.yottr.model.Boat;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.CrewService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.yottr.builder.UserBuilder.aUser;

public class CrewControllerTest extends AbstractControllerTest {

    @Autowired
    private BoatService mockBoatService;

    @Autowired
    private CrewService mockCrewService;

    private Boat boat;
    private String crewPath;

    @BeforeEach
    public void createAListing() {
        reset(mockBoatService, mockCrewService);
        boat = aUser().withUsername("alice").withBoat().build().getBoatListings().get(0);
        crewPath = "/s/listings/" + boat.getReference() + "/crew";
    }

    @AfterEach
    public void afterEachTest() {
        verifyNoMoreInteractions(mockBoatService, mockCrewService);
    }

    @Test
    public void aMemberWhoCanSeeTheListingAsksToJoin() throws Exception {
        when(mockBoatService.findListingToView(boat.getReference(), "dave", false)).thenReturn(Optional.of(boat));

        mockMvc.perform(post(crewPath).principal(() -> "dave"))
                .andExpect(redirectedUrl("/s/listings/" + boat.getReference() + "#crew"));

        verify(mockBoatService).findListingToView(boat.getReference(), "dave", false);
        verify(mockCrewService).ask(boat, "dave");
    }

    @Test
    public void aListingThatCantBeSeenCantBeAskedToJoin() throws Exception {
        when(mockBoatService.findListingToView(boat.getReference(), "dave", false)).thenReturn(Optional.empty());

        mockMvc.perform(post(crewPath).principal(() -> "dave")).andExpect(status().isNotFound());

        verify(mockBoatService).findListingToView(boat.getReference(), "dave", false);
    }

    @Test
    public void theOwnerAcceptsAndRemovesCrew() throws Exception {
        when(mockBoatService.findListing(boat.getReference(), "alice")).thenReturn(Optional.of(boat));

        mockMvc.perform(post(crewPath + "/7/accept").principal(() -> "alice"))
                .andExpect(redirectedUrl("/s/listings/" + boat.getReference() + "#crew"));
        mockMvc.perform(post(crewPath + "/7/remove").principal(() -> "alice"))
                .andExpect(redirectedUrl("/s/listings/" + boat.getReference() + "#crew"));

        verify(mockBoatService, times(2)).findListing(boat.getReference(), "alice");
        verify(mockCrewService).accept(boat, 7L);
        verify(mockCrewService).remove(boat, 7L);
    }

    @Test
    public void onlyTheOwnerAnswersTheirCrew() throws Exception {
        when(mockBoatService.findListing(boat.getReference(), "dave")).thenReturn(Optional.empty());

        mockMvc.perform(post(crewPath + "/7/accept").principal(() -> "dave")).andExpect(status().isNotFound());
        mockMvc.perform(post(crewPath + "/7/remove").principal(() -> "dave")).andExpect(status().isNotFound());

        verify(mockBoatService, times(2)).findListing(boat.getReference(), "dave");
    }
}

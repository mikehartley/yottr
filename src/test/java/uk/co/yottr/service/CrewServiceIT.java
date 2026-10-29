package uk.co.yottr.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.CrewMember;
import uk.co.yottr.model.User;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.junit.jupiter.api.Assertions.*;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.UserBuilder.aUser;

@SpringBootTest
@Import(TestcontainersConfig.class)
public class CrewServiceIT {

    @Autowired
    private CrewService crewService;

    @Autowired
    private BoatService boatService;

    @Autowired
    private UserService userService;

    private User owner;
    private User dave;
    private Boat boat;

    @BeforeEach
    public void createAListingAndCrew() {
        owner = userService.save(aUser().withUsername("owner" + secure().nextAlphanumeric(10)).build(), false);
        dave = userService.save(aUser().withUsername("dave" + secure().nextAlphanumeric(10)).build(), false);
        boat = boatService.save(aBoat().withOwner(owner).build());
    }

    @Test
    public void aMemberAsksAndIsInTheCrewOnceAccepted() {
        crewService.ask(boat, dave.getUsername());
        crewService.ask(boat, dave.getUsername());

        final List<CrewMember> crew = crewService.crewOf(boat);
        assertEquals(1, crew.size(), "asking twice is asking once");
        assertFalse(crew.get(0).isOnBoard());
        assertEquals(dave.getUsername(), crew.get(0).getMember().getUsername());

        crewService.accept(boat, crew.get(0).getId());

        assertTrue(crewService.placeIn(boat, dave.getUsername()).orElseThrow().isOnBoard());
        assertNotNull(crewService.placeIn(boat, dave.getUsername()).orElseThrow().getJoinedOn());
        assertEquals(List.of(boat.getReference()), crewService.crewing(dave.getUsername()).stream().map(place -> place.getBoat().getReference()).toList());
    }

    @Test
    public void anOwnerCantJoinTheirOwnCrew() {
        assertThrows(IllegalOperationException.class, () -> crewService.ask(boat, owner.getUsername()));
    }

    @Test
    public void aMemberLeavesOrTheOwnerRemovesThem() {
        crewService.ask(boat, dave.getUsername());
        crewService.leave(boat, dave.getUsername());
        assertTrue(crewService.crewOf(boat).isEmpty());

        crewService.ask(boat, dave.getUsername());
        crewService.remove(boat, crewService.crewOf(boat).get(0).getId());
        assertTrue(crewService.crewOf(boat).isEmpty());
    }

    @Test
    public void anOwnerCanOnlyAnswerTheirOwnBoatsCrew() {
        final Boat other = boatService.save(aBoat().withOwner(owner).build());
        crewService.ask(other, dave.getUsername());
        final Long place = crewService.crewOf(other).get(0).getId();

        assertThrows(ResourceNotFoundException.class, () -> crewService.accept(boat, place));
        assertThrows(ResourceNotFoundException.class, () -> crewService.remove(boat, place));
        assertFalse(crewService.crewOf(other).get(0).isOnBoard());
    }

    @Test
    public void deletingTheListingOrTheMemberEndsTheirPlace() {
        crewService.ask(boat, dave.getUsername());
        boatService.delete(boatService.findByReference(boat.getReference()));
        assertTrue(crewService.crewing(dave.getUsername()).isEmpty());

        final Boat another = boatService.save(aBoat().withOwner(owner).build());
        crewService.ask(another, dave.getUsername());
        userService.delete(userService.findMember(dave.getUsername()));
        assertTrue(crewService.crewOf(another).isEmpty());
    }
}

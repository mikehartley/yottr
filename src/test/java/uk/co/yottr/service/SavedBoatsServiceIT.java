package uk.co.yottr.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.UserBuilder.aUser;

@SpringBootTest
@Import(TestcontainersConfig.class)
public class SavedBoatsServiceIT {

    @Autowired
    private SavedBoatsService savedBoatsService;

    @Autowired
    private BoatService boatService;

    @Autowired
    private UserService userService;

    private User owner;
    private User crew;

    @BeforeEach
    public void createMembers() {
        owner = userService.save(aUser().withUsername("owner" + secure().nextAlphanumeric(10)).build(), false);
        crew = userService.save(aUser().withUsername("crew" + secure().nextAlphanumeric(10)).build(), false);
    }

    @Test
    public void aMemberSavesAndUnsavesListings() {
        final Boat first = boatService.save(aBoat().withOwner(owner).build());
        final Boat second = boatService.save(aBoat().withOwner(owner).build());

        savedBoatsService.save(crew.getUsername(), first.getReference());
        savedBoatsService.save(crew.getUsername(), second.getReference());
        savedBoatsService.save(crew.getUsername(), first.getReference());
        assertEquals(Set.of(first.getReference(), second.getReference()), savedBoatsService.savedReferences(crew.getUsername()));

        savedBoatsService.unsave(crew.getUsername(), first.getReference());
        assertEquals(Set.of(second.getReference()), savedBoatsService.savedReferences(crew.getUsername()));
        assertTrue(savedBoatsService.savedReferences(owner.getUsername()).isEmpty(), "each member has their own");
    }

    @Test
    public void savedListingsComeWithTheirOwnersAndIncludeHiddenOnes() {
        final Boat boat = boatService.save(aBoat().withOwner(owner).withSuspended(true).build());
        savedBoatsService.save(crew.getUsername(), boat.getReference());

        final List<Boat> saved = savedBoatsService.savedBoats(crew.getUsername());

        assertEquals(1, saved.size());
        // outside the service's transaction, so this fails if the owner wasn't loaded
        assertFalse(saved.get(0).isVisible(), "suspended, and still there to say so");
    }

    @Test
    public void aDeletedListingGoesFromEveryonesSavedBoats() {
        final Boat boat = boatService.save(aBoat().withOwner(owner).build());
        savedBoatsService.save(crew.getUsername(), boat.getReference());

        boatService.delete(boatService.findAnyListing(boat.getReference()).orElseThrow());

        assertTrue(savedBoatsService.savedReferences(crew.getUsername()).isEmpty());
    }

    @Test
    public void deletingAMemberDeletesTheirListingsFromOthersSavedBoatsAndTheirOwnSavedBoats() {
        final Boat boat = boatService.save(aBoat().withOwner(owner).build());
        savedBoatsService.save(crew.getUsername(), boat.getReference());
        final Boat crewsBoat = boatService.save(aBoat().withOwner(crew).build());
        savedBoatsService.save(owner.getUsername(), crewsBoat.getReference());

        userService.delete(userService.findMember(owner.getUsername()));

        assertTrue(savedBoatsService.savedReferences(crew.getUsername()).isEmpty());
    }
}

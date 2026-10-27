package uk.co.yottr.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.form.ListingForm;
import uk.co.yottr.model.*;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.junit.jupiter.api.Assertions.*;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.UserBuilder.aUser;

@SpringBootTest
@Import(TestcontainersConfig.class)
public class BoatServiceIT {

    @Autowired
    private BoatService boatService;

    @Autowired
    private UserService userService;

    private User owner;

    @BeforeEach
    public void createOwner() {
        owner = userService.save(aUser().build(), false);
    }

    @AfterEach
    public void clearData() {
        for (Boat boat : boatService.findAll(PageRequest.of(0, 9999))) {
            boatService.delete(boat);
        }
    }

    @Test
    public void canCreateFindByReferenceAndDelete() throws Exception {

        final String uniqueDescription = "some description with unique number : "  + new Random().nextLong();

        Boat boat = aBoat().withOwner(owner).withDescription(uniqueDescription).build();

        final Boat savedBoat = boatService.save(boat);

        final Boat foundByReference = boatService.findByReference(boat.getReference());
        assertNotNull(foundByReference, "could not find boat by reference");
        assertEquals(uniqueDescription, foundByReference.getDescription(), "description");

        boatService.delete(savedBoat);

        assertNull(boatService.findByReference(boat.getReference()), "boat was not deleted");
    }

    @Test
    public void canFindAllExcludingSuspended() throws Exception {
        final Boat liveBoat = boatService.save(aBoat().withSuspended(false).withOwner(owner).build());
        boatService.save(aBoat().withSuspended(true).withOwner(owner).build());

        final Page<Boat> resultFromService = boatService.findAllExcludingSuspended(PageRequest.of(0, 10));

        assertEquals(1, resultFromService.getTotalElements(), "total elements in result");
        assertEquals(liveBoat.getReference(), resultFromService.iterator().next().getReference(), "element in result");
    }

    @Test
    public void allFieldsPersist() throws Exception {

        final String title = secure().nextAlphanumeric(50);
        final String description = secure().nextAlphanumeric(50);
        final Boat.HullType hullType = Boat.HullType.MONO;
        final Integer length = new Random().nextInt(99) + 3;
        final boolean unitsImperial = true;
        final String makeAndModel = secure().nextAlphanumeric(15);
        final RyaSailCruisingLevel minLevel = new RyaSailCruisingLevel(RyaSailCruisingLevel.Level.COASTAL_SKIPPER);
        final SailingPurpose sailingPurpose = SailingPurpose.RACING;
        final LocalDate now = LocalDate.now();
        final LocalDate whenFrom = LocalDate.of(2009, 8, 8);
        final LocalDate whenTo = LocalDate.of(2010, 9, 9);
        final LocalDate dateRelevantTo = LocalDate.of(2010, 10, 10);
        final User owner = createUser();
        final FinancialArrangement financialArrangement = FinancialArrangement.COMMERCIAL;
        final Integer numberOfCrewWanted = 5;
        final Collection<Frequency> frequencies = Arrays.asList(Frequency.HOLIDAYS);
        final Integer yearBuilt = 2015;
        final VesselType vesselType = VesselType.SAIL;
        final RoleRequired roleRequired = RoleRequired.SKIPPER;
        final TravelExpenses travelExpenses = TravelExpenses.NEGOTIABLE;

        Boat boat = new Boat(owner);
        boat.setTitle(title);
        boat.setDescription(description);
        boat.setHullType(hullType);
        boat.setLength(length);
        boat.setUnitsImperial(unitsImperial);
        boat.setMakeAndModel(makeAndModel);
        boat.setMinimumRequiredLevel(minLevel);
        boat.setSailingPurpose(sailingPurpose);
        boat.setWhenFrom(whenFrom);
        boat.setWhenTo(whenTo);
        boat.setDateRelevantTo(dateRelevantTo);
        boat.setFinancialArrangement(financialArrangement);
        boat.setNumberOfCrewWanted(numberOfCrewWanted);
        boat.setFrequency(frequencies);
        boat.setYearBuilt(yearBuilt);
        boat.setVesselType(vesselType);
        boat.setRoleRequired(roleRequired);
        boat.setTravelExpenses(travelExpenses);

        final Boat savedBoat = boatService.save(boat);

        assertEquals(title, savedBoat.getTitle(), "title");
        assertEquals(description, savedBoat.getDescription(), "description");
        assertEquals(hullType, savedBoat.getHullType(), "hullType");
        assertEquals(length, savedBoat.getLength(), "length");
        assertEquals(unitsImperial, savedBoat.isUnitsImperial(), "unitsImperial");
        assertEquals(makeAndModel, savedBoat.getMakeAndModel(), "makeAndModel");
        assertNotNull(savedBoat.getReference(), "reference");
        assertEquals(now, savedBoat.getFirstPosted(), "first posted");
        assertEquals(now, savedBoat.getLastUpdated(), "last updated");
        assertEquals(minLevel.getRank(), savedBoat.getMinimumRequiredLevel().getRank(), "minimum level");
        assertEquals(sailingPurpose, savedBoat.getSailingPurpose(), "sailing style");
        assertEquals(whenFrom, savedBoat.getWhenFrom(), "when from");
        assertEquals(whenTo, savedBoat.getWhenTo(), "when to");
        assertEquals(dateRelevantTo, savedBoat.getDateRelevantTo(), "date relevant to");
        assertEquals(owner, savedBoat.getOwner(), "owner");
        assertEquals(financialArrangement, savedBoat.getFinancialArrangement(), "financialArrangement");
        assertEquals(numberOfCrewWanted, savedBoat.getNumberOfCrewWanted(), "number of crew wanted");
        assertEquals(frequencies, savedBoat.getFrequency(), "frequency");
        assertEquals(yearBuilt, savedBoat.getYearBuilt(), "yearBuilt");
        assertEquals(vesselType, savedBoat.getVesselType(), "vesselType");
        assertEquals(roleRequired, savedBoat.getRoleRequired(), "roleRequired");
        assertEquals(travelExpenses, savedBoat.getTravelExpenses(), "travelExpenses");
    }

    @Test
    public void canSaveTwoBoats() {
        boatService.save(aBoat().withOwner(owner).withDescription("1").build());
        boatService.save(aBoat().withOwner(owner).withDescription("2").build());
        final Iterator<Boat> boatIterator = boatService.findAll(PageRequest.of(0, 10)).iterator();
        assertEquals("1", boatIterator.next().getDescription());
        assertEquals("2", boatIterator.next().getDescription());
        assertFalse(boatIterator.hasNext());
    }

    @Test
    public void deletingAMemberDeletesTheirListings() {
        final User member = createUser();
        final String reference = boatService.save(aBoat().withOwner(member).build()).getReference();

        userService.delete(member.getId());

        assertFalse(userService.userExists(member.getId()), "the member is deleted");
        assertNull(boatService.findByReference(reference), "their listing is deleted with them");
    }

    @Test
    public void deletingAMemberLoadedEarlierDeletesTheirListings() {
        final User member = createUser();
        final String reference = boatService.save(aBoat().withOwner(member).build()).getReference();

        userService.delete(userService.findByUsername(member.getUsername()));

        assertFalse(userService.userExists(member.getId()), "the member is deleted");
        assertNull(boatService.findByReference(reference), "their listing is deleted with them");
    }

    @Test
    public void twoListingsPostedAtOnceCannotBothPassTheLimit() throws Exception {
        final User member = userService.save(aUser().withUsername(secure().nextAlphabetic(10)).withMaxListings(1).build(), false);
        final CountDownLatch start = new CountDownLatch(1);
        final ExecutorService executor = Executors.newFixedThreadPool(2);

        final Callable<Boolean> post = () -> {
            start.await();
            try {
                boatService.create(member.getUsername(), aListingForm());
                return true;
            } catch (IllegalOperationException atLimit) {
                return false;
            }
        };
        final Future<Boolean> first = executor.submit(post);
        final Future<Boolean> second = executor.submit(post);
        start.countDown();

        final List<Boolean> posted = List.of(first.get(30, TimeUnit.SECONDS), second.get(30, TimeUnit.SECONDS));
        executor.shutdown();

        assertEquals(1, posted.stream().filter(Boolean::booleanValue).count(), "exactly one post succeeds: " + posted);
        assertEquals(1, userService.findByUsername(member.getUsername()).getBoatListings().size(), "listings saved");
    }

    private static ListingForm aListingForm() {
        final ListingForm form = new ListingForm();
        form.setTitle("Crew wanted");
        form.setMakeAndModel("Sigma 33");
        form.setLength(33);
        form.setVesselType(VesselType.SAIL);
        form.setYearBuilt(1987);
        form.setDescription("Relaxed weekend cruising.");
        form.setSailingPurpose(SailingPurpose.CRUISING);
        form.setRoleRequired(RoleRequired.CREW);
        form.setFrequency(List.of(Frequency.FrequencyEnum.WEEKENDS));
        form.setFinancialArrangement(FinancialArrangement.FinancialArrangementEnum.SHARED_FIXED);
        return form;
    }

    private User createUser() {
        final String username = secure().nextAlphabetic(10);
        final User user = aUser().withUsername(username).build();
        return userService.save(user, false);
    }
}
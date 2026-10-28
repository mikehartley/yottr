package uk.co.yottr.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Import;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.form.ListingForm;
import uk.co.yottr.form.BoatSearch;
import uk.co.yottr.model.*;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    public void visibleListingsLeaveOutSuspendedOnes() throws Exception {
        final Boat liveBoat = boatService.save(aBoat().withSuspended(false).withOwner(owner).build());
        boatService.save(aBoat().withSuspended(true).withOwner(owner).build());

        final Page<Boat> resultFromService = boatService.search(new BoatSearch(), PageRequest.of(0, 10));

        assertEquals(1, resultFromService.getTotalElements(), "total elements in result");
        assertEquals(liveBoat.getReference(), resultFromService.iterator().next().getReference(), "element in result");
    }

    @Test
    public void visibleListingsLeaveOutThoseOfDisabledMembers() throws Exception {
        final Boat liveBoat = boatService.save(aBoat().withOwner(owner).build());
        final User disabledOwner = aUser().build();
        disabledOwner.setEnabled(false);
        boatService.save(aBoat().withOwner(userService.save(disabledOwner, false)).build());

        final Page<Boat> resultFromService = boatService.search(new BoatSearch(), PageRequest.of(0, 10));

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

        userService.delete(userService.findByUsername(member.getUsername()).orElseThrow());

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
        assertEquals(1, userService.findByUsername(member.getUsername()).orElseThrow().getBoatListings().size(), "listings saved");
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

    @Test
    public void aListingToViewComesWithItsOwner() throws Exception {
        final Boat boat = boatService.save(aBoat().withOwner(owner).build());

        final Boat found = boatService.findListingToView(boat.getReference(), "someone else", false).orElseThrow();

        // outside the service's transaction, so these fail if the owner, or the eager frequencies, weren't loaded
        assertEquals(owner.getEmail(), found.getOwner().getEmail());
        assertFalse(found.getFrequency().isEmpty(), "frequencies");
    }

    @Test
    public void otherMembersCannotViewASuspendedListing() throws Exception {
        final Boat boat = boatService.save(aBoat().withOwner(owner).withSuspended(true).build());

        assertTrue(boatService.findListingToView(boat.getReference(), "someone else", false).isEmpty(), "hidden from others");
        assertTrue(boatService.findListingToView(boat.getReference(), owner.getUsername(), false).isPresent(), "seen by its owner");
    }

    @Test
    public void searchFindsTextInTheTitleMakeOrDescriptionIgnoringCase() throws Exception {
        final Boat byTitle = boatService.save(aBoat().withOwner(owner).build());
        byTitle.setTitle("Weekends in the SOLENT");
        boatService.save(byTitle);
        final Boat byMake = boatService.save(aBoat().withOwner(owner).build());
        byMake.setMakeAndModel("Solent Sprinter");
        boatService.save(byMake);
        final Boat byDescription = boatService.save(aBoat().withOwner(owner).withDescription("Based on the solent.").build());
        boatService.save(aBoat().withOwner(owner).withDescription("Clyde cruising").build());

        assertEquals(Set.of(byTitle.getReference(), byMake.getReference(), byDescription.getReference()), references(search(s -> s.setText("Solent"))));
    }

    @Test
    public void searchFindsAWholeReference() throws Exception {
        final Boat boat = boatService.save(aBoat().withOwner(owner).build());
        boatService.save(aBoat().withOwner(owner).build());

        assertEquals(Set.of(boat.getReference()), references(search(s -> s.setText(boat.getReference()))));
    }

    @Test
    public void searchNarrowsByPurposeWhenCostsAndLevel() throws Exception {
        final Boat match = aBoat().withOwner(owner).withMinimumRequiredLevel(RyaSailCruisingLevel.DAY_SKIPPER).build();
        match.setSailingPurpose(SailingPurpose.CRUISING);
        match.setFrequency(new ArrayList<>(List.of(Frequency.WEEKENDS)));
        match.setFinancialArrangement(FinancialArrangement.SHARED_FIXED);
        final String matchReference = boatService.save(match).getReference();
        // the defaults: a delivery, in the holidays, with no contribution, needing Coastal Skipper
        boatService.save(aBoat().withOwner(owner).build());

        assertEquals(Set.of(matchReference), references(search(s -> s.setPurpose(SailingPurpose.CRUISING))));
        assertEquals(Set.of(matchReference), references(search(s -> s.setWhen(Frequency.FrequencyEnum.WEEKENDS))));
        assertEquals(Set.of(matchReference), references(search(s -> s.setCosts(FinancialArrangement.FinancialArrangementEnum.SHARED_FIXED))));
        assertEquals(Set.of(matchReference), references(search(s -> s.setMyLevel(RyaSailCruisingLevel.DAY_SKIPPER.getRank()))),
                "a Day Skipper can't join a boat that needs a Coastal Skipper");
        assertEquals(2, search(s -> s.setMyLevel(RyaSailCruisingLevel.COASTAL_SKIPPER.getRank())).getTotalElements(), "a Coastal Skipper can join both");
    }

    @Test
    public void aListingWithSeveralFrequenciesIsFoundOnce() throws Exception {
        final Boat boat = aBoat().withOwner(owner).build();
        boat.setFrequency(new ArrayList<>(List.of(Frequency.WEEKENDS, Frequency.HOLIDAYS)));
        boatService.save(boat);

        final Page<Boat> found = search(s -> s.setWhen(Frequency.FrequencyEnum.HOLIDAYS));

        assertEquals(1, found.getTotalElements(), "counted once");
        assertEquals(1, found.getContent().size(), "listed once");
    }

    @Test
    public void searchTextWithLikeWildcardsMatchesThemLiterally() throws Exception {
        final Boat percent = boatService.save(aBoat().withOwner(owner).withDescription("100% fun").build());
        boatService.save(aBoat().withOwner(owner).withDescription("1000 miles of fun").build());

        assertEquals(Set.of(percent.getReference()), references(search(s -> s.setText("100%"))));
    }

    @Test
    public void searchNarrowsByHowRecentlyAListingWasPosted() throws Exception {
        final Boat today = boatService.save(aBoat().withOwner(owner).build());
        final Boat twentyDaysAgo = postedDaysAgo(boatService.save(aBoat().withOwner(owner).build()), 20);
        postedDaysAgo(boatService.save(aBoat().withOwner(owner).build()), 100);

        assertEquals(Set.of(today.getReference()), references(search(s -> s.setPostedWithin(7))));
        assertEquals(Set.of(today.getReference(), twentyDaysAgo.getReference()), references(search(s -> s.setPostedWithin(30))));
    }

    @Test
    public void searchFindsListingsWhoseDatesOverlapTheSearchersOpenEndsIncluded() throws Exception {
        final Boat june = sailing(LocalDate.of(2027, 6, 1), LocalDate.of(2027, 6, 14));
        final Boat untilMay = sailing(null, LocalDate.of(2027, 5, 1));
        final Boat fromJuly = sailing(LocalDate.of(2027, 7, 1), null);
        final Boat anyTime = sailing(null, null);

        assertEquals(Set.of(june.getReference(), anyTime.getReference()), references(search(s -> {
            s.setSailingFrom(LocalDate.of(2027, 6, 10));
            s.setSailingTo(LocalDate.of(2027, 6, 20));
        })));
        assertEquals(Set.of(fromJuly.getReference(), anyTime.getReference()),
                references(search(s -> s.setSailingFrom(LocalDate.of(2027, 7, 5)))));
        assertEquals(Set.of(untilMay.getReference(), anyTime.getReference()),
                references(search(s -> s.setSailingTo(LocalDate.of(2027, 4, 1)))));
        assertEquals(Set.of(june.getReference(), anyTime.getReference()), references(search(s -> {
            s.setSailingFrom(LocalDate.of(2027, 6, 14));
            s.setSailingTo(LocalDate.of(2027, 6, 14));
        })), "the last day counts");
    }

    @Test
    public void searchByLengthIsInFeetWhicheverUnitsTheListingUses() throws Exception {
        final Boat feet33 = ofLength(33, true);
        final Boat metres10 = ofLength(10, false);
        final Boat metres12 = ofLength(12, false);
        final Boat feet45 = ofLength(45, true);

        assertEquals(Set.of(feet33.getReference(), metres12.getReference(), feet45.getReference()),
                references(search(s -> s.setMinLength(33))), "10 m is 32.8 ft");
        assertEquals(Set.of(feet33.getReference(), metres10.getReference()), references(search(s -> s.setMaxLength(33))));
        assertEquals(Set.of(metres12.getReference()), references(search(s -> {
            s.setMinLength(34);
            s.setMaxLength(40);
        })), "12 m is 39.4 ft");
    }

    private Boat postedDaysAgo(Boat boat, int days) {
        jdbcTemplate.update("update boats set first_posted = ? where reference = ?", LocalDate.now().minusDays(days), boat.getReference());
        return boat;
    }

    private Boat sailing(LocalDate from, LocalDate to) {
        final Boat boat = aBoat().withOwner(owner).build();
        boat.setWhenFrom(from);
        boat.setWhenTo(to);
        return boatService.save(boat);
    }

    private Boat ofLength(int length, boolean feet) {
        final Boat boat = aBoat().withOwner(owner).build();
        boat.setLength(length);
        boat.setUnitsImperial(feet);
        return boatService.save(boat);
    }

    @Test
    public void searchLeavesOutListingsMembersCannotSee() throws Exception {
        final Boat suspended = boatService.save(aBoat().withOwner(owner).withSuspended(true).withDescription("hidden gem").build());
        final Boat withdrawn = aBoat().withOwner(owner).withDescription("hidden gem").build();
        withdrawn.setWithdrawn(true);
        boatService.save(withdrawn);

        assertTrue(references(search(s -> s.setText("hidden gem"))).isEmpty(),
                "suspended " + suspended.getReference() + ", withdrawn " + withdrawn.getReference());
    }

    @Test
    public void anAdministratorCanWithdrawAndDeleteAnyListing() throws Exception {
        final Boat boat = boatService.save(aBoat().withOwner(owner).build());

        boatService.flipWithdrawn(boatService.findAnyListing(boat.getReference()).orElseThrow());
        assertTrue(boatService.findAnyListing(boat.getReference()).orElseThrow().isWithdrawn(), "withdrawn");
        assertTrue(boatService.findListingToView(boat.getReference(), "someone else", false).isEmpty(), "hidden from others");
        assertTrue(boatService.findListingToView(boat.getReference(), "an administrator", true).isPresent(), "seen by administrators");

        boatService.delete(boatService.findAnyListing(boat.getReference()).orElseThrow());
        assertTrue(boatService.findAnyListing(boat.getReference()).isEmpty(), "deleted");
    }

    private Page<Boat> search(Consumer<BoatSearch> criteria) {
        final BoatSearch search = new BoatSearch();
        criteria.accept(search);
        return boatService.search(search, PageRequest.of(0, 30));
    }

    private static Set<String> references(Page<Boat> page) {
        return page.stream().map(Boat::getReference).collect(Collectors.toSet());
    }
}

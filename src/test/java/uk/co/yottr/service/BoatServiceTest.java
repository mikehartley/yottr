package uk.co.yottr.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.co.yottr.builder.BoatBuilder;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.form.ListingForm;
import uk.co.yottr.model.*;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.FinancialArrangementRepository;
import uk.co.yottr.repository.FrequencyRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;
import uk.co.yottr.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.co.yottr.builder.UserBuilder.aUser;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class BoatServiceTest {

    private BoatService boatService;

    @Mock
    private BoatRepository boatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Mock
    private FrequencyRepository frequencyRepository;

    @Mock
    private FinancialArrangementRepository financialArrangementRepository;

    @Mock
    private TemporalProvider temporalProvider;

    private User owner;

    @BeforeEach
    public void init() {
        reset(boatRepository);
        reset(ryaSailCruisingLevelRepository);
        reset(temporalProvider);

        boatService = new BoatService(boatRepository, userRepository, ryaSailCruisingLevelRepository, frequencyRepository,
                financialArrangementRepository, temporalProvider);

        owner = aUser().withUsername("owner").withMaxListings(2).build();
    }

    private ListingForm aListingForm() {
        final ListingForm form = new ListingForm();
        form.setTitle("Crew wanted");
        form.setMakeAndModel("Sigma 33");
        form.setLength(33);
        form.setUnitsImperial(false);
        form.setHullType(Boat.HullType.MULTI);
        form.setVesselType(VesselType.SAIL);
        form.setYearBuilt(1987);
        form.setDescription("Relaxed weekend cruising.");
        form.setSailingPurpose(SailingPurpose.RACING);
        form.setRoleRequired(RoleRequired.MATE);
        form.setFrequency(List.of(Frequency.FrequencyEnum.WEEKENDS, Frequency.FrequencyEnum.TRIP));
        form.setDateRelevantTo(LocalDate.of(2027, 6, 14));
        form.setMinimumRequiredLevelByRank(RyaSailCruisingLevel.DAY_SKIPPER.getRank());
        form.setFinancialArrangement(FinancialArrangement.FinancialArrangementEnum.PAID);
        return form;
    }

    private void givenStoredReferenceData() {
        when(ryaSailCruisingLevelRepository.findByRank(RyaSailCruisingLevel.DAY_SKIPPER.getRank())).thenReturn(RyaSailCruisingLevel.DAY_SKIPPER);
        when(frequencyRepository.findAllById(List.of("WEEKENDS", "TRIP"))).thenReturn(List.of(Frequency.WEEKENDS, Frequency.TRIP));
        when(financialArrangementRepository.findById("PAID")).thenReturn(Optional.of(FinancialArrangement.PAID));
        when(boatRepository.save(any(Boat.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void assertMatchesTheForm(Boat boat) {
        assertEquals("Crew wanted", boat.getTitle());
        assertEquals("Sigma 33", boat.getMakeAndModel());
        assertEquals(Integer.valueOf(33), boat.getLength());
        assertEquals(false, boat.isUnitsImperial());
        assertEquals(Boat.HullType.MULTI, boat.getHullType());
        assertEquals(VesselType.SAIL, boat.getVesselType());
        assertEquals(Integer.valueOf(1987), boat.getYearBuilt());
        assertEquals("Relaxed weekend cruising.", boat.getDescription());
        assertEquals(SailingPurpose.RACING, boat.getSailingPurpose());
        assertEquals(RoleRequired.MATE, boat.getRoleRequired());
        assertEquals(List.of(Frequency.WEEKENDS, Frequency.TRIP), boat.getFrequency());
        assertEquals(LocalDate.of(2027, 6, 14), boat.getDateRelevantTo());
        assertSame(RyaSailCruisingLevel.DAY_SKIPPER, boat.getMinimumRequiredLevel());
        assertSame(FinancialArrangement.PAID, boat.getFinancialArrangement());
    }

    @Test
    public void createUsesTheStoredReferenceData() {
        givenStoredReferenceData();
        when(userRepository.findByUsernameForUpdate("owner")).thenReturn(owner);
        when(boatRepository.countByOwner(owner)).thenReturn(1L);

        final Boat created = boatService.create("owner", aListingForm());

        assertMatchesTheForm(created);
        assertSame(owner, created.getOwner());
        assertTrue(owner.getBoatListings().contains(created), "the owner's listings include the new one");
    }

    @Test
    public void createRefusesAMemberAtTheirListingLimit() {
        when(userRepository.findByUsernameForUpdate("owner")).thenReturn(owner);
        when(boatRepository.countByOwner(owner)).thenReturn(2L);

        assertThrows(IllegalOperationException.class, () -> boatService.create("owner", aListingForm()));

        verify(boatRepository, never()).save(any(Boat.class));
    }

    @Test
    public void createLocksTheMemberBeforeCountingTheirListings() {
        givenStoredReferenceData();
        when(userRepository.findByUsernameForUpdate("owner")).thenReturn(owner);

        boatService.create("owner", aListingForm());

        final InOrder inOrder = inOrder(userRepository, boatRepository);
        inOrder.verify(userRepository).findByUsernameForUpdate("owner");
        inOrder.verify(boatRepository).countByOwner(owner);
    }

    @Test
    public void flipSuspendedSuspendsAndReEnables() {
        when(boatRepository.save(any(Boat.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ryaSailCruisingLevelRepository.findByRank(RyaSailCruisingLevel.COASTAL_SKIPPER.getRank())).thenReturn(RyaSailCruisingLevel.COASTAL_SKIPPER);
        final Boat boat = BoatBuilder.aBoat().withOwner(owner).withSuspended(false).build();

        assertTrue(boatService.flipSuspended(boat).isSuspended());
        assertFalse(boatService.flipSuspended(boat).isSuspended());
    }

    @Test
    public void updateChangesOnlyTheFieldsOnTheForm() {
        givenStoredReferenceData();
        final Boat boat = BoatBuilder.aBoat().withOwner(owner).withSuspended(true).build();
        final String reference = boat.getReference();

        final Boat updated = boatService.update(boat, aListingForm());

        assertSame(boat, updated);
        assertMatchesTheForm(updated);
        assertEquals(reference, updated.getReference(), "reference");
        assertSame(owner, updated.getOwner(), "owner");
        assertTrue(updated.isSuspended(), "editing must not change whether the listing is suspended");
    }

    @Test
    public void testSave() throws Exception {
        final RyaSailCruisingLevel coastalSkipperLevel = RyaSailCruisingLevel.COASTAL_SKIPPER;
        final LocalDate firstDay2015 = LocalDate.ofYearDay(2015, 1);
        Boat boat = BoatBuilder.aBoat().withOwner(owner)
                .withMinimumRequiredLevel(coastalSkipperLevel)
                .build();
        ArgumentCaptor<Boat> boatCaptor = ArgumentCaptor.forClass(Boat.class);
        when(ryaSailCruisingLevelRepository.findByRank(coastalSkipperLevel.getRank())).thenReturn(coastalSkipperLevel);
        when(temporalProvider.today()).thenReturn(firstDay2015);

        boatService.save(boat);

        verify(ryaSailCruisingLevelRepository).findByRank(coastalSkipperLevel.getRank());
        verify(boatRepository).save(boatCaptor.capture());

        final Boat boatThatWasSaved = boatCaptor.getValue();
        assertEquals(coastalSkipperLevel, boatThatWasSaved.getMinimumRequiredLevel());
        assertEquals(firstDay2015, boatThatWasSaved.getLastUpdated());

        verifyNoMoreInteractions(boatRepository);
        verifyNoMoreInteractions(ryaSailCruisingLevelRepository);
    }

    @Test
    public void testFindAll() throws Exception {
        final Pageable pageable = mock(Pageable.class);
        final PageImpl<Boat> page = new PageImpl<>(new ArrayList<>());
        when(boatRepository.findAll(pageable)).thenReturn(page);

        final Page<Boat> pageFromService = boatService.findAll(pageable);
        assertSame(page, pageFromService, "page from service");
    }

    @Test
    public void testDelete() throws Exception {
        Boat boat = BoatBuilder.aBoat().withOwner(owner).build();

        boatService.delete(boat);

        verify(boatRepository).delete(boat);
    }

    @Test
    public void testFindByReference() throws Exception {
        String reference = "xyz";
        Boat boat = BoatBuilder.aBoat().withOwner(owner).build();
        when(boatRepository.findByReference(reference)).thenReturn(boat);

        final Boat boatFromService = boatService.findByReference(reference);

        assertSame(boat, boatFromService, "boat from service");
    }

    @Test
    public void testFindAllExcludingSuspended() throws Exception {
        final Pageable pageable = mock(Pageable.class);
        final ArrayList<Boat> boatList = new ArrayList<>();
        final PageImpl<Boat> page = new PageImpl<>(boatList);
        final boolean suspended = false;
        when(boatRepository.findBySuspended(suspended, pageable)).thenReturn(page);

        final Page<Boat> resultFromService = boatService.findAllExcludingSuspended(pageable);

        assertSame(page, resultFromService, "result from service");
    }
}
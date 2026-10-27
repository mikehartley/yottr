package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.form.ListingForm;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.FinancialArrangement;
import uk.co.yottr.model.Frequency;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.FinancialArrangementRepository;
import uk.co.yottr.repository.FrequencyRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;
import uk.co.yottr.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoatService {

    private BoatRepository boatRepository;
    private UserRepository userRepository;
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;
    private FrequencyRepository frequencyRepository;
    private FinancialArrangementRepository financialArrangementRepository;
    private TemporalProvider temporalProvider;

    @Autowired
    public BoatService(BoatRepository boatRepository,
                       UserRepository userRepository,
                       RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository,
                       FrequencyRepository frequencyRepository,
                       FinancialArrangementRepository financialArrangementRepository,
                       TemporalProvider temporalProvider) {
        this.boatRepository = boatRepository;
        this.userRepository = userRepository;
        this.ryaSailCruisingLevelRepository = ryaSailCruisingLevelRepository;
        this.frequencyRepository = frequencyRepository;
        this.financialArrangementRepository = financialArrangementRepository;
        this.temporalProvider = temporalProvider;
    }

    @Transactional
    public Boat save(Boat boat) {
        final RyaSailCruisingLevel level = ryaSailCruisingLevelRepository.findByRank(boat.getMinimumRequiredLevelByRank());
        boat.setMinimumRequiredLevel(level); // this needs to be non-transient, hence the look-up
        boat.setLastUpdated(temporalProvider.today());
        return boatRepository.save(boat);
    }

    /**
     * A new listing for the member, from the new listing form.
     *
     * @throws IllegalOperationException if the member already has as many listings as they are allowed
     */
    @Transactional
    public Boat create(String ownerUsername, ListingForm listingForm) {
        // Locking the member's row until the listing is saved means two posts at once can't both pass the limit.
        final User owner = userRepository.findByUsernameForUpdate(ownerUsername);

        if (boatRepository.countByOwner(owner) >= owner.getMaxListings()) {
            throw new IllegalOperationException("User is already at listing limit.");
        }

        final Boat boat = new Boat(owner);
        applyForm(listingForm, boat);
        return save(boat);
    }

    /**
     * The member's listing with this reference, if they have one.
     */
    @Transactional(readOnly = true)
    public Optional<Boat> findListing(String reference, String ownerUsername) {
        return boatRepository.findByReferenceAndOwnerUsername(reference, ownerUsername);
    }

    @Transactional(readOnly = true)
    public List<Boat> findListings(String ownerUsername) {
        return boatRepository.findByOwnerUsernameOrderByReference(ownerUsername);
    }

    @Transactional
    public Boat flipSuspended(Boat boat) {
        boat.setSuspended(!boat.isSuspended());
        return save(boat);
    }

    /**
     * Changes a listing to match the edit form.
     */
    @Transactional
    public Boat update(Boat boat, ListingForm listingForm) {
        applyForm(listingForm, boat);
        return save(boat);
    }

    private void applyForm(ListingForm listingForm, Boat boat) {
        // Look up the stored reference data before changing the boat: an existing boat is managed by Hibernate
        // and is flushed before each query, so it must never refer to anything unsaved.
        final RyaSailCruisingLevel level = ryaSailCruisingLevelRepository.findByRank(listingForm.getMinimumRequiredLevelByRank());
        final List<Frequency> frequencies = new ArrayList<>();
        frequencyRepository.findAllById(listingForm.getFrequency().stream().map(Enum::name).toList()).forEach(frequencies::add);
        final FinancialArrangement financialArrangement = financialArrangementRepository.findById(listingForm.getFinancialArrangement().name())
                .orElseThrow(() -> new IllegalStateException("No financial arrangement " + listingForm.getFinancialArrangement()));

        listingForm.copyTo(boat);
        boat.setMinimumRequiredLevel(level);
        boat.setFrequency(frequencies);
        boat.setFinancialArrangement(financialArrangement);
    }

    @Transactional(readOnly = true)
    public Page<Boat> findAll(Pageable pageable) {
        return boatRepository.findAll(pageable);
    }

    @Transactional
    public void delete(Boat boat) {
        boatRepository.delete(boat);
    }

    @Transactional(readOnly = true)
    public Boat findByReference(String reference) {
        return boatRepository.findByReference(reference);
    }

    /**
     * The listings members can see: not suspended by their owner, and not belonging to a disabled account.
     */
    @Transactional(readOnly = true)
    public Page<Boat> findAllVisible(Pageable pageable) {
        return boatRepository.findBySuspendedFalseAndOwnerEnabledTrue(pageable);
    }
}

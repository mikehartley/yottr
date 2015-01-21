package uk.co.yottr.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import uk.co.yottr.validator.YearBuilt;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Entity
@Table(name = "boats")
public class Boat {

    private static final String REQUIRED_ERROR_MSG = "required";
    public static final String DATE_PATTERN = "dd/MM/yyyy";

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @Column(name = "title", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min=2, max=64)
    private String title;

    @Column(name = "makeAndModel", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
	@Size(min=1, max=60)
    private String makeAndModel;

    @Column(name = "length", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Min(3)
    @Max(999)
    private Integer length;

    @Column(name = "units_imperial", nullable = false)
    private boolean isUnitsImperial = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "smoking", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    private Smoking smoking = Smoking.NO;

    @Column(name = "year_built", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @YearBuilt
    private Integer yearBuilt;

    public enum HullType {
        MONO, MULTI
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "hull_type", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    private HullType hullType = HullType.MONO;

    @Enumerated(EnumType.STRING)
    @Column(name = "vessel_type", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    private VesselType vesselType;

    @Column(name = "description", nullable = false)
    @NotEmpty(message = REQUIRED_ERROR_MSG)
    private String description;

    @Column(name = "first_posted", nullable = false)
    private LocalDate firstPosted;

    @Column(name = "last_updated", nullable = false)
    private LocalDate lastUpdated;

    @DateTimeFormat(pattern = DATE_PATTERN)
    @Column(name = "date_relevant_to")
    private LocalDate dateRelevantTo;

    @DateTimeFormat(pattern = DATE_PATTERN)
    @Column(name = "when_from")
    private LocalDate whenFrom; //TODO validation - cross field

    @DateTimeFormat(pattern = DATE_PATTERN)
    @Column(name = "when_to")
    private LocalDate whenTo; //TODO validation - cross field

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "min_required_level")
    @NotNull(message = REQUIRED_ERROR_MSG)
    private RyaSailCruisingLevel minimumRequiredLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_required", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    private RoleRequired roleRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "sailing_purpose", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    private SailingPurpose sailingPurpose;

    @ManyToMany(fetch = FetchType.EAGER)
    @Column(name = "frequency", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 1, message = "must have at least one")
    private Collection<Frequency> frequency = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "financial_arrangement")
    @NotNull(message = REQUIRED_ERROR_MSG)
    private FinancialArrangement financialArrangement;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_expenses", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    private TravelExpenses travelExpenses = TravelExpenses.PAID_BY_CREW;

    @Column(name = "number_wanted")
    @Min(1)
    @Max(99)
    private Integer numberOfCrewWanted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "suspended", nullable = false)
    private boolean suspended;

    /**
     * For Hibernate.
     */
    Boat() {
        this.reference = System.currentTimeMillis() + "-" + new Random().nextInt(100);
        this.firstPosted = LocalDate.now();
        this.lastUpdated = LocalDate.now();
        this.minimumRequiredLevel = new RyaSailCruisingLevel(RyaSailCruisingLevel.Level.NONE);
    }

    /**
     * Public constructor with boat owner.
     * @param owner the user responsible for posting the listing.
     */
    public Boat(User owner) {
        this();
        setOwner(owner);
    }

    public String getReference() {
        return reference;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMakeAndModel() {
        return makeAndModel;
    }

    public void setMakeAndModel(String makeAndModel) {
        this.makeAndModel = makeAndModel;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public boolean isUnitsImperial() {
        return isUnitsImperial;
    }

    public void setUnitsImperial(boolean isUnitsImperial) {
        this.isUnitsImperial = isUnitsImperial;
    }

    public HullType getHullType() {
        return hullType;
    }

    public void setHullType(HullType hullType) {
        this.hullType = hullType;
    }

    public VesselType getVesselType() {
        return vesselType;
    }

    public void setVesselType(VesselType vesselType) {
        this.vesselType = vesselType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getFirstPosted() {
        return firstPosted;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDate getDateRelevantTo() {
        return dateRelevantTo;
    }

    public void setDateRelevantTo(LocalDate dateRelevantTo) {
        this.dateRelevantTo = dateRelevantTo;
    }

    public LocalDate getWhenFrom() {
        return whenFrom;
    }

    public void setWhenFrom(LocalDate whenFrom) {
        this.whenFrom = whenFrom;
    }

    public LocalDate getWhenTo() {
        return whenTo;
    }

    public void setWhenTo(LocalDate whenTo) {
        this.whenTo = whenTo;
    }

    public RyaSailCruisingLevel getMinimumRequiredLevel() {
        return minimumRequiredLevel;
    }

    public void setMinimumRequiredLevel(RyaSailCruisingLevel minimumRequiredLevel) {
        this.minimumRequiredLevel = minimumRequiredLevel;
    }

    public int getMinimumRequiredLevelByRank() {
        return minimumRequiredLevel.getRank();
    }

    public void setMinimumRequiredLevelByRank(int rank) {
        for (RyaSailCruisingLevel.Level level : Arrays.asList(RyaSailCruisingLevel.Level.values())) {
            if (rank == level.rank) {
                this.minimumRequiredLevel = new RyaSailCruisingLevel(level);
            }
        }
        // if there's no match then nothing gets set, but this would only happen if someone
        // has been messing about with the request parameters
    }

    public RoleRequired getRoleRequired() {
        return roleRequired;
    }

    public void setRoleRequired(RoleRequired roleRequired) {
        this.roleRequired = roleRequired;
    }

    public SailingPurpose getSailingPurpose() {
        return sailingPurpose;
    }

    public void setSailingPurpose(SailingPurpose sailingPurpose) {
        this.sailingPurpose = sailingPurpose;
    }

    public Collection<Frequency> getFrequency() {
        return frequency;
    }

    public void setFrequency(Collection<Frequency> frequency) {
        this.frequency = frequency;
    }

    public FinancialArrangement getFinancialArrangement() {
        return financialArrangement;
    }

    public void setFinancialArrangement(FinancialArrangement financialArrangement) {
        this.financialArrangement = financialArrangement;
    }

    public TravelExpenses getTravelExpenses() {
        return travelExpenses;
    }

    public void setTravelExpenses(TravelExpenses travelExpenses) {
        this.travelExpenses = travelExpenses;
    }

    public Integer getNumberOfCrewWanted() {
        return numberOfCrewWanted;
    }

    public void setNumberOfCrewWanted(Integer numberOfCrewWanted) {
        this.numberOfCrewWanted = numberOfCrewWanted;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
        owner.getBoatListings().add(this);
    }

    public Smoking getSmoking() {
        return smoking;
    }

    public void setSmoking(Smoking smoking) {
        this.smoking = smoking;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public Integer getYearBuilt() {
        return yearBuilt;
    }
}

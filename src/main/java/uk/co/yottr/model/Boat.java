package uk.co.yottr.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Entity
@Table(name = "boats")
public class Boat {

    private static final String REQUIRED_ERROR_MSG = "required";

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

    public enum HullType {
        MONO, MULTI;
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "hull_type", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    private HullType hullType = HullType.MONO;

    @Column(name = "description", nullable = false)
    @NotEmpty(message = REQUIRED_ERROR_MSG)
    private String description;

    @Column(name = "first_posted", nullable = false)
    private LocalDate firstPosted;

    @Column(name = "last_updated", nullable = false)
    private LocalDate lastUpdated;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "date_relevant_to")
    private LocalDate dateRelevantTo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "min_required_level")
    @NotNull(message = REQUIRED_ERROR_MSG)
    private RyaSailCruisingLevel minimumRequiredLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "sailing_purpose", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    private SailingPurpose sailingPurpose;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "financial_arrangement")
    @NotNull(message = REQUIRED_ERROR_MSG)
    private FinancialArrangement financialArrangement;

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

    public SailingPurpose getSailingPurpose() {
        return sailingPurpose;
    }

    public void setSailingPurpose(SailingPurpose sailingPurpose) {
        this.sailingPurpose = sailingPurpose;
    }

    public FinancialArrangement getFinancialArrangement() {
        return financialArrangement;
    }

    public void setFinancialArrangement(FinancialArrangement financialArrangement) {
        this.financialArrangement = financialArrangement;
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
}

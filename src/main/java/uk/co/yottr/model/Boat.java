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
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
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

    @Column(name = "reference", nullable = false)
    private String reference;

    @Column(name = "manufacturer", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
	@Size(min=1, max=30)
    private String manufacturer;

    @Column(name = "model", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min=1, max=30)
    private String model;

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

    @Column(name = "date_posted", nullable = false)
    private LocalDate datePosted;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "date_relevant_to")
    private LocalDate dateRelevantTo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "min_required_level")
    @NotNull(message = REQUIRED_ERROR_MSG)
    private RyaSailCruisingLevel minimumRequiredLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "sailing_style", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    private SailingStyle sailingStyle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "suspended", nullable = false)
    private boolean suspended;

    /**
     * For Hibernate.
     */
    Boat() {
        this.reference = System.currentTimeMillis() + "-" + new Random().nextInt(100);
        this.datePosted = LocalDate.now();
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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public LocalDate getDatePosted() {
        return datePosted;
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

    public SailingStyle getSailingStyle() {
        return sailingStyle;
    }

    public void setSailingStyle(SailingStyle sailingStyle) {
        this.sailingStyle = sailingStyle;
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

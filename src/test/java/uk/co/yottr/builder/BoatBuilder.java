package uk.co.yottr.builder;

import uk.co.yottr.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BoatBuilder {

    private User owner;
    private String title = "Great Title";
    private Boat.HullType hullType = Boat.HullType.MONO;
    private int length = 36;
    private boolean unitsImperial = true;
    private String makeAndModel = "Halberg Rassy HR36";
    private SailingPurpose sailingPurpose = SailingPurpose.DELIVERY;
    private String description = "Default Boat";
    private RyaSailCruisingLevel minimumRequiredLevel = RyaSailCruisingLevel.COASTAL_SKIPPER;
    private FinancialArrangement financialArrangement = FinancialArrangement.NO_CONTRIBUTION;
    private boolean suspended;
    private LocalDate lastUpdated;
    private List<Frequency> frequencies = new ArrayList<>();
    private Integer yearBuilt = 2015;
    private VesselType vesselType = VesselType.SAIL;
    private RoleRequired roleRequired = RoleRequired.MATE;
    // a delivery goes somewhere
    private SailingRegion fromRegion = SailingRegion.SOUTH_COAST;
    private String fromPlace = "Hamble";
    private SailingRegion toRegion = SailingRegion.WEST_COUNTRY;
    private String toPlace = "Falmouth";

    {
        frequencies.add(Frequency.HOLIDAYS);
    }

    private BoatBuilder() {
    }

    public static BoatBuilder aBoat() {
        return new BoatBuilder();
    }

    public BoatBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public BoatBuilder withMinimumRequiredLevel(RyaSailCruisingLevel level) {
        this.minimumRequiredLevel = level;
        return this;
    }

    public BoatBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public BoatBuilder from(SailingRegion region, String place) {
        this.fromRegion = region;
        this.fromPlace = place;
        return this;
    }

    public BoatBuilder to(SailingRegion region, String place) {
        this.toRegion = region;
        this.toPlace = place;
        return this;
    }

    public BoatBuilder withSuspended(boolean suspended) {
        this.suspended = suspended;
        return this;
    }

    public BoatBuilder withLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public Boat build() {

        if (owner == null) {
            throw new IllegalStateException("Can't build a boat without an owner");
        }

        final Boat boat = new Boat(owner);
        boat.setTitle(title);
        boat.setHullType(hullType);
        boat.setLength(length);
        boat.setUnitsImperial(unitsImperial);
        boat.setMakeAndModel(makeAndModel);
        boat.setSailingPurpose(sailingPurpose);
        boat.setDescription(description);
        boat.setMinimumRequiredLevel(minimumRequiredLevel);
        boat.setFinancialArrangement(financialArrangement);
        boat.setSuspended(suspended);
        boat.setLastUpdated(lastUpdated);
        boat.setFrequency(frequencies);
        boat.setYearBuilt(yearBuilt);
        boat.setVesselType(vesselType);
        boat.setRoleRequired(roleRequired);
        boat.setFromRegion(fromRegion);
        boat.setFromPlace(fromPlace);
        boat.setToRegion(toRegion);
        boat.setToPlace(toPlace);

        return boat;
    }
}

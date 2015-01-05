package uk.co.yottr.builder;

import uk.co.yottr.model.*;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

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

    public BoatBuilder withSuspended(boolean suspended) {
        this.suspended = suspended;
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

        return boat;
    }
}

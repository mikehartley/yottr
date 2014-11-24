package uk.co.yottr.builder;

import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingStyle;
import uk.co.yottr.model.User;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class BoatBuilder {

    private User owner;
    private Boat.HullType hullType = Boat.HullType.MONO;
    private int length = 36;
    private boolean unitsImperial = true;
    private String manufacturer = "Halberg Rassy";
    private String model = "HR36";
    private SailingStyle sailingStyle = SailingStyle.ALL;
    private String description = "Default Boat";
    private RyaSailCruisingLevel minimumRequiredLevel = RyaSailCruisingLevel.COASTAL_SKIPPER;
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
        boat.setHullType(hullType);
        boat.setLength(length);
        boat.setUnitsImperial(unitsImperial);
        boat.setManufacturer(manufacturer);
        boat.setModel(model);
        boat.setSailingStyle(sailingStyle);
        boat.setDescription(description);
        boat.setMinimumRequiredLevel(minimumRequiredLevel);
        boat.setSuspended(suspended);

        return boat;
    }
}

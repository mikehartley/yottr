package uk.co.yottr.builder;

import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingStyle;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class BoatBuilder {

    private Boat boat;

    private BoatBuilder() {
        Boat boat = new Boat();
        boat.setHullType(Boat.HullType.MONO);
        boat.setLength(36);
        boat.setUnitsImperial(true);
        boat.setManufacturer("Halberg Rassy");
        boat.setModel("HR36");
        boat.setSailingStyle(SailingStyle.ALL);
        boat.setDescription("Default Boat");
        boat.setMinimumRequiredLevel(RyaSailCruisingLevel.COASTAL_SKIPPER);
        this.boat = boat;
    }

    public static BoatBuilder aBoat() {
        return new BoatBuilder();
    }

    public BoatBuilder withDescription(String description) {
        boat.setDescription(description);
        return this;
    }

    public BoatBuilder withMinimumRequiredLevel(RyaSailCruisingLevel level) {
        boat.setMinimumRequiredLevel(level);
        return this;
    }

    public Boat build() {
        return boat;
    }
}

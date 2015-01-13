package uk.co.yottr.model;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public enum VesselType {

    SAIL("Sail"),
    POWER("Power");

    private String displayName;

    private VesselType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

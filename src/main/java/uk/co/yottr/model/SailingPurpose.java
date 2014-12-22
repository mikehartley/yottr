package uk.co.yottr.model;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public enum SailingPurpose {
    CRUISING("Cruising"),
    RACING("Racing"),
    DELIVERY("Delivery"),
    LONG_TERM("Long Term"),
    PROFESSIONAL("Professional");

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }

    SailingPurpose(String displayName) {
        this.displayName = displayName;
    }
}

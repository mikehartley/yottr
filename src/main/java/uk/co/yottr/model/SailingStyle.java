package uk.co.yottr.model;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public enum SailingStyle {
    CRUISING("Cruising"),
    RACING("Racing"),
    ALL("Both cruising and racing");

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }

    SailingStyle(String displayName) {
        this.displayName = displayName;
    }
}

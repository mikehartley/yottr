package uk.co.yottr.model;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * Defines the purpose of the sailing.
 */
public enum SailingPurpose {
    /**Cruising*/
    CRUISING("Cruising"),
    /**Racing*/
    RACING("Racing"),
    /**Boat delivery*/
    DELIVERY("Delivery"),
    /**An extended period of sailing such as an ocean crossing, or expedition.*/
    LONG_TERM("Long Term"),
    /**Where the owner wants to pay someone to come on board in a professional capacity..*/
    PROFESSIONAL("Professional");

    private final String displayName;

    private SailingPurpose(String displayName) {
        this.displayName = displayName;
    }

    /**
     * A displayable name for the enum value.
     * @return a human readable string.
     */
    public String getDisplayName() {
        return displayName;
    }
}

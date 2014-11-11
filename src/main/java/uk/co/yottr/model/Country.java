package uk.co.yottr.model;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public enum Country {

    UK("UK"),
    OTHER("Other");

    private String displayName;

    private Country(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

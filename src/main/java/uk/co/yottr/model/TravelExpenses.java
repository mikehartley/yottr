package uk.co.yottr.model;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public enum TravelExpenses {
    PAID_BY_CREW("Paid by crew"),
    PAID_BY_OWNER("Paid by owner"),
    NEGOTIABLE("Negotiable");

    private final String displayName;

    private TravelExpenses(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

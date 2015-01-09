package uk.co.yottr.model;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public enum Smoking {
    NO("No"),
    YES_OUTSIDE("Yes - outside only"),
    YES_ANYWHERE("Yes - inside and outside");

    private String displayName;

    private Smoking(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

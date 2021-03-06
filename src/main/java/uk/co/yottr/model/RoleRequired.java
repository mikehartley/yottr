package uk.co.yottr.model;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public enum RoleRequired implements DisplayableName {

    CREW("Crew"),
    MATE("Mate"),
    SKIPPER("Skipper"),
    DOESNT_MATTER("Don't matter");

    private String displayName;

    private RoleRequired(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}

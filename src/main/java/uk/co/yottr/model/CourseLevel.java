package uk.co.yottr.model;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public enum CourseLevel implements DisplayableName {

    START_YACHTING("Start Yachting"),
    COMP_CREW("Competent Crew"),
    DAY_SKIPPER("Day Skipper"),
    COASTAL_SKIPPER_WK("Coastal Skipper Practical Week"),
    YACHTMASTER_COASTAL("Yachtmaster Coastal"),
    YACHTMASTER_OFFSHORE("Yachtmaster Offshore"),
    YACHTMASTER_OCEAN("Yachtmaster Ocean");

    private final String displayName;

    private CourseLevel(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}

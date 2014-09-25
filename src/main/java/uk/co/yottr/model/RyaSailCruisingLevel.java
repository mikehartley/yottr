package uk.co.yottr.model;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * Created by mike on 25/09/14.
 */
public enum RyaSailCruisingLevel {
    NONE(0, "None"),
    START_YACHTING(100, "Start Yachting"),
    COMPETENT_CREW(200, "Competent Crew"),
    DAY_SKIPPER(300, "Day Skipper"),
    COASTAL_SKIPPER(400, "Coastal Skipper"),
    YACHTMASTER_COASTAL(500, "Yachtmaster Coastal"),
    YACHTMASTER_OFFSHORE(600, "Yachtmaster Offshore"),
    YACHTMASTER_OCEAN(700, "Yachtmaster Ocean");

    private int rank;
    private String displayName;

    RyaSailCruisingLevel(int rank, String displayName) {
        this.rank = rank;
        this.displayName = displayName;
    }

    public int getRank() {
        return rank;
    }

    public String getDisplayName() {
        return displayName;
    }
}

package uk.co.yottr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * Created by mike on 25/09/14.
 */
@Entity
public class RyaSailCruisingLevel {
    public enum Level {
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

        Level(int rank, String displayName) {
            this.rank = rank;
            this.displayName = displayName;
        }
    }

    @Id
    @Column(name = "rank")
    private int rank;

    @Column(name = "display_name", unique = true, nullable = false)
    private String displayName;

    private RyaSailCruisingLevel() {
        // required for JPA
    }

    public RyaSailCruisingLevel(Level level) {
        this.rank = level.rank;
        this.displayName = level.displayName;
    }

    public int getRank() {
        return rank;
    }

    public String getDisplayName() {
        return displayName;
    }
}

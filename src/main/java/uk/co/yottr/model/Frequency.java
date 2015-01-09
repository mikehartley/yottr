package uk.co.yottr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Entity
@Table(name = "frequency")
public class Frequency {

    public enum FrequencyEnum {

        WEEKDAYS("Weekdays"),
        WEEKENDS("Weekends"),
        HOLIDAYS("Holidays"),
        LONG_TERM("Long Term"),
        TRIP("Trip");

        private String displayName;

        private FrequencyEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static Frequency WEEKDAYS =  new Frequency(FrequencyEnum.WEEKDAYS);
    public static Frequency WEEKENDS =  new Frequency(FrequencyEnum.WEEKENDS);
    public static Frequency HOLIDAYS =  new Frequency(FrequencyEnum.HOLIDAYS);
    public static Frequency LONG_TERM =  new Frequency(FrequencyEnum.LONG_TERM);
    public static Frequency TRIP =  new Frequency(FrequencyEnum.TRIP);

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "display_name")
    private String displayName;

    private Frequency() {
        // for JPA only
    }

    public Frequency(FrequencyEnum frequency) {
        if (frequency == null) {
            throw new IllegalArgumentException("cannot construct me with null argument");
        }

        this.name = frequency.name();
        this.displayName = frequency.getDisplayName();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}

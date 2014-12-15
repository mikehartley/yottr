package uk.co.yottr.model;

// This will have to be a multiple select option (many-many)

// Two options here... either encapsulate everything in one list:

    /* The odd day here and there
    *  Some weekends
    *  Every weekend
    *  Any day of the week
    *  About a week
    *  About two weeks
    *  About a month
    *  Many months
    *  Years
    *  */

// or split frequency and duration into two lists:

    /* FREQUENCY
    * No regular pattern
    * Every day
    * Every week
    * Most weeks
    * Monthly
    * Single trip
    * */

    /* DURATION
    * Hours
    * Days
    * Weeks
    * Months
    * Full time
    * */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Entity
@Table(name = "time_commitment")
public class TimeCommitment {

    public enum TimeCommitmentEnum {

        FREE("Free"),
        PAY_THEM_COST("I Pay Them At Cost"),
        PAY_THEM_COMMERCIAL("I Pay Them Commercial Rates"),
        PAY_ME_COST("They Pay Me At Cost"),
        PAY_ME_COMMERCIAL("They Pay Me Commercial Rates");

        private String displayName;

        private TimeCommitmentEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static TimeCommitment FREE =  new TimeCommitment(TimeCommitmentEnum.FREE);

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "display_name")
    private String displayName;

    private TimeCommitment() {
        // for JPA only
    }

    public TimeCommitment(TimeCommitmentEnum timeCommitment) {
        if (timeCommitment == null) {
            throw new IllegalArgumentException("cannot construct me with null argument");
        }

        this.name = timeCommitment.name();
        this.displayName = timeCommitment.getDisplayName();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}

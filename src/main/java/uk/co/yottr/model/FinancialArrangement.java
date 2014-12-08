package uk.co.yottr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Entity
public class FinancialArrangement {

    public enum FinancialArrangementEnum {
        FREE("Free"),
        PAY_THEM_COST("I Pay Them At Cost"),
        PAY_THEM_COMMERCIAL("I Pay Them Commercial Rates"),
        PAY_ME_COST("They Pay Me At Cost"),
        PAY_ME_COMMERCIAL("They Pay Me Commercial Rates");

        private String displayName;

        private FinancialArrangementEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "display_name")
    private String displayName;

    private FinancialArrangement() {
        // for JPA only
    }

    public FinancialArrangement(FinancialArrangementEnum financialArrangementValue) {
        if (financialArrangementValue == null) {
            throw new IllegalArgumentException("cannot construct me with null argument");
        }

        this.name = financialArrangementValue.name();
        this.displayName = financialArrangementValue.getDisplayName();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}

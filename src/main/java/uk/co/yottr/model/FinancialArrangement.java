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
@Table(name = "financial_arrangement")
public class FinancialArrangement {

    public enum FinancialArrangementEnum {
        NO_CONTRIBUTION("No Contribution Required"),
        FOOD_ONLY("Food Only (No Boat Costs)"),
        SHARED_FIXED("Shared Fixed"),
        SHARED_VARIABLE("Shared Variable"),
        COMMERCIAL("Commercial"),
        PAID("Paid Position");

        private String displayName;

        private FinancialArrangementEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static FinancialArrangement NO_CONTRIBUTION =  new FinancialArrangement(FinancialArrangementEnum.NO_CONTRIBUTION);
    public static FinancialArrangement FOOD_ONLY =  new FinancialArrangement(FinancialArrangementEnum.FOOD_ONLY);
    public static FinancialArrangement SHARED_FIXED =  new FinancialArrangement(FinancialArrangementEnum.SHARED_FIXED);
    public static FinancialArrangement SHARED_VARIABLE =  new FinancialArrangement(FinancialArrangementEnum.SHARED_VARIABLE);
    public static FinancialArrangement COMMERCIAL =  new FinancialArrangement(FinancialArrangementEnum.COMMERCIAL);
    public static FinancialArrangement PAID =  new FinancialArrangement(FinancialArrangementEnum.PAID);

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

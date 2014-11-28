package uk.co.yottr.model;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public enum FinancialArrangement {
    FREE("Free"),
    PAY_THEM_COST("I Pay Them At Cost"),
    PAY_THEM_COMMERCIAL("I Pay Them Commercial Rates"),
    PAY_ME_COST("They Pay Me At Cost"),
    PAY_ME_COMMERCIAL("They Pay Me Commercial Rates");

    private String displayName;

    private FinancialArrangement(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

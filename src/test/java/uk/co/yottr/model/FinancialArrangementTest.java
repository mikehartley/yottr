package uk.co.yottr.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class FinancialArrangementTest {

    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("Free", FinancialArrangement.FREE.getDisplayName());
        assertEquals("I Pay Them At Cost", FinancialArrangement.PAY_THEM_COST.getDisplayName());
        assertEquals("I Pay Them Commercial Rates", FinancialArrangement.PAY_THEM_COMMERCIAL.getDisplayName());
        assertEquals("They Pay Me At Cost", FinancialArrangement.PAY_ME_COST.getDisplayName());
        assertEquals("They Pay Me Commercial Rates", FinancialArrangement.PAY_ME_COMMERCIAL.getDisplayName());
    }
}
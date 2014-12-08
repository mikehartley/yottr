package uk.co.yottr.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class FinancialArrangementTest {

    public static final FinancialArrangement FREE = new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.FREE);
    public static final FinancialArrangement PAY_THEM_COST = new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.PAY_THEM_COST);
    public static final FinancialArrangement THEM_COMMERCIAL = new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.PAY_THEM_COMMERCIAL);
    public static final FinancialArrangement ME_COST = new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.PAY_ME_COST);
    public static final FinancialArrangement ME_COMMERCIAL = new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.PAY_ME_COMMERCIAL);

    @Test
    public void testGetName() throws Exception {
        assertEquals(FinancialArrangement.FinancialArrangementEnum.FREE.name(), FREE.getName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.PAY_THEM_COST.name(), PAY_THEM_COST.getName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.PAY_THEM_COMMERCIAL.name(), THEM_COMMERCIAL.getName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.PAY_ME_COST.name(), ME_COST.getName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.PAY_ME_COMMERCIAL.name(), ME_COMMERCIAL.getName());
    }

    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals(FinancialArrangement.FinancialArrangementEnum.FREE.getDisplayName(), FREE.getDisplayName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.PAY_THEM_COST.getDisplayName(), PAY_THEM_COST.getDisplayName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.PAY_THEM_COMMERCIAL.getDisplayName(), THEM_COMMERCIAL.getDisplayName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.PAY_ME_COST.getDisplayName(), ME_COST.getDisplayName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.PAY_ME_COMMERCIAL.getDisplayName(), ME_COMMERCIAL.getDisplayName());
    }
}
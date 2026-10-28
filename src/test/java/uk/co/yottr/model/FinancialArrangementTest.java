package uk.co.yottr.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FinancialArrangementTest {

    @Test
    public void testGetName() throws Exception {
        assertEquals("NO_CONTRIBUTION", FinancialArrangement.NO_CONTRIBUTION.getName());
        assertEquals("FOOD_ONLY", FinancialArrangement.FOOD_ONLY.getName());
        assertEquals("SHARED_FIXED", FinancialArrangement.SHARED_FIXED.getName());
        assertEquals("SHARED_VARIABLE", FinancialArrangement.SHARED_VARIABLE.getName());
        assertEquals("COMMERCIAL", FinancialArrangement.COMMERCIAL.getName());
        assertEquals("PAID", FinancialArrangement.PAID.getName());
    }

    @Test
    public void testGetDisplayName() throws Exception {
        assertEquals("No cost to crew", FinancialArrangement.NO_CONTRIBUTION.getDisplayName());
        assertEquals("Crew share food only", FinancialArrangement.FOOD_ONLY.getDisplayName());
        assertEquals("Crew pay a fixed share", FinancialArrangement.SHARED_FIXED.getDisplayName());
        assertEquals("Crew share running costs", FinancialArrangement.SHARED_VARIABLE.getDisplayName());
        assertEquals("Crew pay (charter, trip or course)", FinancialArrangement.COMMERCIAL.getDisplayName());
        assertEquals("Crew are paid", FinancialArrangement.PAID.getDisplayName());
    }

    @Test
    public void writesItselfAsItsNameSoAFormCanBindItBack() {
        assertEquals("SHARED_FIXED", new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.SHARED_FIXED).toString());
    }
}

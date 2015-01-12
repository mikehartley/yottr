package uk.co.yottr.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.*;
import static uk.co.yottr.builder.UserBuilder.aUser;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class BoatTest {
    private static Validator validator;

    private static final String SIZE_ERROR_MSG = "size must be between %d and %d";
    private static final String REQUIRED_ERROR_MSG = "required";

    private Boat boat;

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Before
    public void beforeEachTest() {
        boat = createValidBoat(aUser().build());
    }

    @Test
    public void allValid() {
        final Set<ConstraintViolation<Boat>> violations = validator.validate(boat);
        assertEquals("not expecting any violations", 0, violations.size());
    }

    @Test
    public void reference() {
        final Boat boat1 = new Boat();
        final Boat boat2 = new Boat();

        assertTrue("reference should be automatically populated and a decent size", boat1.getReference().length() > 5);
        assertNotEquals("two consecutive references should not be the same", boat1.getReference(), boat2.getReference());
    }

    @Test
    public void firstPosted() {
        assertEquals("first posted should be today", LocalDate.now(), boat.getFirstPosted());
    }

    @Test
    public void lastUpdated() {
        assertEquals("last updated should initially be same as first posted", boat.getFirstPosted(), boat.getLastUpdated());
    }

    @Test
    public void title() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 2, 64);

        boat.setTitle(null);
        assertSingleViolation(REQUIRED_ERROR_MSG);

        boat.setTitle("");
        assertSingleViolation(sizeErrorMessage);

        boat.setTitle("1");
        assertSingleViolation(sizeErrorMessage);

        boat.setTitle("12345678901234567890123456789012345678901234567890123456789012345");
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void makeAndModel() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 1, 60);

        boat.setMakeAndModel(null);
        assertSingleViolation(REQUIRED_ERROR_MSG);

        boat.setMakeAndModel("");
        assertSingleViolation(sizeErrorMessage);

        boat.setMakeAndModel("1234567890123456789012345678901234567890123456789012345678901");
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void length() {
        final String minErrorMessage = String.format("must be greater than or equal to %d", 3);
        final String maxErrorMessage = String.format("must be less than or equal to %d", 999);

        boat.setLength(null);
        assertSingleViolation(REQUIRED_ERROR_MSG);

        boat.setLength(2);
        assertSingleViolation(minErrorMessage);

        boat.setLength(1000);
        assertSingleViolation(maxErrorMessage);
    }

    @Test
    public void hullType() {
        boat.setHullType(null);
        assertSingleViolation(REQUIRED_ERROR_MSG);
    }

    @Test
    public void description() {
        boat.setDescription(null);
        assertSingleViolation(REQUIRED_ERROR_MSG);

        boat.setDescription("");
        assertSingleViolation(REQUIRED_ERROR_MSG);
    }

    @Test
    public void sailingPurpose() {
        boat.setSailingPurpose(null);
        assertSingleViolation(REQUIRED_ERROR_MSG);
    }

    @Test
    public void financialArrangement() {
        boat.setFinancialArrangement(null);
        assertSingleViolation(REQUIRED_ERROR_MSG);
    }

    @Test
    public void canSetAndGetMinQualificationByRank() {
        boat.setMinimumRequiredLevel(RyaSailCruisingLevel.NONE);

        boat.setMinimumRequiredLevelByRank(RyaSailCruisingLevel.DAY_SKIPPER.getRank());

        assertEquals(RyaSailCruisingLevel.DAY_SKIPPER.getRank(), boat.getMinimumRequiredLevel().getRank());
        assertEquals(RyaSailCruisingLevel.DAY_SKIPPER.getDisplayName(), boat.getMinimumRequiredLevel().getDisplayName());

        assertEquals(RyaSailCruisingLevel.DAY_SKIPPER.getRank(), boat.getMinimumRequiredLevelByRank());
    }

    @Test
    public void doesNotSetMinQualificationByRankWhenRankInvalid() {
        boat.setMinimumRequiredLevel(RyaSailCruisingLevel.COMPETENT_CREW);

        boat.setMinimumRequiredLevelByRank(654513516);

        assertEquals(RyaSailCruisingLevel.COMPETENT_CREW.getRank(), boat.getMinimumRequiredLevel().getRank());
    }

    @Test
    public void numberOfCrewWanted() {
        assertNull("number of crew wanted should initially be null", boat.getNumberOfCrewWanted());

        Integer number = 5;
        boat.setNumberOfCrewWanted(number);
        assertEquals("number of crew wanted", number, boat.getNumberOfCrewWanted());

        boat.setNumberOfCrewWanted(0);
        assertSingleViolation("must be greater than or equal to 1");

        boat.setNumberOfCrewWanted(100);
        assertSingleViolation("must be less than or equal to 99");
    }

    @Test
    public void frequency() {
        boat.setFrequency(wrapInCollection());

        assertSingleViolation("must have at least one");
    }

    @Test
    public void noSmokingCantBeNull() {
        boat.setSmoking(null);
        assertSingleViolation(REQUIRED_ERROR_MSG);
    }

    @Test
    public void noSmokingByDefault() {
        assertEquals(Smoking.NO, boat.getSmoking());
    }

    @Test
    public void unitsDefaultToImperial() {
        assertTrue(new Boat().isUnitsImperial());
    }

    @Test
    public void hullTypeDefaultsToMonohull() {
        assertEquals(Boat.HullType.MONO, new Boat().getHullType());
    }

    @Test
    public void yearBuiltCantBeNull() {
        boat.setYearBuilt(null);
    }

    @Test
    public void yearBuiltMustBeBetweenLimits() {
        int inFiveYearsTime = LocalDate.now().getYear() + 5;

        boat.setYearBuilt(1899);
        assertSingleViolation("must be greater than or equal to 1900");

        boat.setYearBuilt(2021);
        assertSingleViolation("must be less than...");
    }

    @Test
    public void suspended() {
        assertFalse(boat.isSuspended());

        boat.setSuspended(true);

        assertTrue(boat.isSuspended());
    }

    @Test
    public void ownerSetWithConstructor() {
        User owner1 = aUser().withUsername("u1").build();
        Boat ownersBoat = new Boat(owner1);

        assertEquals("owner that was set via boat constructor", owner1, ownersBoat.getOwner());
        assertSame("back reference to boat from owner", owner1.getBoatListings().get(0), ownersBoat);
    }

    @Test
    public void ownerSetWithSetter() {
        User owner1 = aUser().withUsername("u1").build();
        Boat ownersBoat = new Boat();
        ownersBoat.setOwner(owner1);

        assertEquals("owner that was set via setter", owner1, ownersBoat.getOwner());
        assertSame("back reference to boat from owner", owner1.getBoatListings().get(0), ownersBoat);
    }

    private void assertSingleViolation(String expectedErrorMessage) {
        Set<ConstraintViolation<Boat>> violations = validator.validate(boat);
        assertEquals(violations.toString(), 1, violations.size());
        final ConstraintViolation<Boat> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().toString(), expectedErrorMessage, violation.getMessage());
    }

    private Boat createValidBoat(User user) {
        Boat boat = new Boat(user);
        boat.setTitle("title");
        boat.setMakeAndModel("blah");
        boat.setLength(10);
        boat.setHullType(Boat.HullType.MONO);
        boat.setDescription("blah");
        boat.setSailingPurpose(SailingPurpose.DELIVERY);
        boat.setFinancialArrangement(FinancialArrangement.COMMERCIAL);
        boat.setFrequency(wrapInCollection(Frequency.TRIP));
        boat.setYearBuilt(2001);
        return boat;
    }

    private Collection<Frequency> wrapInCollection(Frequency... frequency) {
        Collection<Frequency> frequencies = new ArrayList<>();
        frequencies.addAll(Arrays.asList(frequency));
        return frequencies;
    }
}
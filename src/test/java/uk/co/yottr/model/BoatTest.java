package uk.co.yottr.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
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
        boat = createBoat();
    }

    @Test
    public void allValid() {
        final Set<ConstraintViolation<Boat>> violations = validator.validate(boat);
        assertEquals("not expecting any violations", 0, violations.size());
    }

    @Test
    public void manufacturer() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 1, 30);

        boat.setManufacturer(null);
        assertSingleViolation(REQUIRED_ERROR_MSG);

        boat.setManufacturer("");
        assertSingleViolation(sizeErrorMessage);

        boat.setManufacturer("1234567890123456789012345678901");
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void model() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 1, 30);

        boat.setModel(null);
        assertSingleViolation(REQUIRED_ERROR_MSG);

        boat.setModel("");
        assertSingleViolation(sizeErrorMessage);

        boat.setModel("1234567890123456789012345678901");
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

    private void assertSingleViolation(String expectedErrorMessage) {
        Set<ConstraintViolation<Boat>> violations = validator.validate(boat);
        assertEquals(violations.toString(), 1, violations.size());
        final ConstraintViolation<Boat> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().toString(), expectedErrorMessage, violation.getMessage());
    }

    private Boat createBoat() {
        Boat boat = new Boat();
        boat.setManufacturer("blah");
        boat.setModel("blah");
        boat.setLength(10);
        boat.setHullType(Boat.HullType.MONO);
        boat.setDescription("blah");
        return boat;
    }
}
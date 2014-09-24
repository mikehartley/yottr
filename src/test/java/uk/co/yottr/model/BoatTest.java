package uk.co.yottr.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class BoatTest {
    private static Validator validator;

    private static final String SIZE_ERROR_MSG = "size must be between %d and %d";
    private static final String NOT_NULL_ERROR_MSG = "required";

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
        fail("need to sort out missing dependency and implement complete test");

        // size 1-30
        boat.setManufacturer(null);
        Set<ConstraintViolation<Boat>> violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail

        boat.setManufacturer("");
        violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail

        boat.setManufacturer("1234567890123456789012345678901");
        violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail
    }

    @Test
    public void model() {
        fail("need to sort out missing dependency and implement complete test");

        // size 1-30
        boat.setModel(null);
        Set<ConstraintViolation<Boat>> violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail

        boat.setModel("");
        violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail

        boat.setModel("1234567890123456789012345678901");
        violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail
    }

    @Test
    public void length() {
        fail("need to sort out missing dependency and implement complete test");

        // 3-999
        boat.setLength(null);
        Set<ConstraintViolation<Boat>> violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail

        boat.setLength(2);
        violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail

        boat.setLength(1000);
        violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail
    }

    @Test
    public void hullType() {
        fail("need to sort out missing dependency and implement complete test");

        // not null
        boat.setHullType(null);
        Set<ConstraintViolation<Boat>> violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail
    }

    @Test
    public void description() {
        fail("need to sort out missing dependency and implement complete test");

        // not empty
        boat.setDescription(null);
        Set<ConstraintViolation<Boat>> violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail

        boat.setDescription("");
        violations = validator.validate(boat);
        assertEquals("violation count", 1, violations.size());
        //TODO check violation in more detail
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
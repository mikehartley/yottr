package uk.co.yottr.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class YearBuiltValidatorTest {

    private YearBuiltValidator yearBuiltValidator;
    private ConstraintValidatorContext nullContext = null;
    private int thisYear = LocalDate.now().getYear();

    @BeforeEach
    public void setup() {
        yearBuiltValidator = new YearBuiltValidator();
    }

    @Test
    public void testIsValid() throws Exception {
        assertFalse(yearBuiltValidator.isValid(1899, nullContext), "should not be valid");
        assertTrue(yearBuiltValidator.isValid(1900, nullContext), "should be valid");
        assertTrue(yearBuiltValidator.isValid(thisYear + 2, nullContext), "should be valid");
        assertFalse(yearBuiltValidator.isValid(thisYear + 3, nullContext), "should not be valid");
    }
}
package uk.co.yottr.validator;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class YearBuiltValidatorTest {

    private YearBuiltValidator yearBuiltValidator;
    private ConstraintValidatorContext nullContext = null;
    private int thisYear = LocalDate.now().getYear();

    @Before
    public void setup() {
        yearBuiltValidator = new YearBuiltValidator();
    }

    @Test
    public void testIsValid() throws Exception {
        assertFalse("should not be valid", yearBuiltValidator.isValid(1899, nullContext));
        assertTrue("should be valid", yearBuiltValidator.isValid(1900, nullContext));
        assertTrue("should be valid", yearBuiltValidator.isValid(thisYear + 2, nullContext));
        assertFalse("should not be valid", yearBuiltValidator.isValid(thisYear + 3, nullContext));
    }
}
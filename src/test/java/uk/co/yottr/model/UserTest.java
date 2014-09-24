package uk.co.yottr.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class UserTest {
    private static Validator validator;

    private User user;

    private static final String SIZE_ERROR_MSG = "size must be between %d and %d";
    private static final String NOT_NULL_ERROR_MSG = "required";

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Before
    public void beforeEachTest() {
        user = createUser();
    }

    @Test
    public void allValid() {
        final Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals("not expecting any violations", 0, violations.size());
    }

    @Test
    public void username() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 2, 50);

        user.setUsername(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setUsername("");
        assertSingleViolation(sizeErrorMessage);

        user.setUsername("a");
        assertSingleViolation(sizeErrorMessage);

        user.setUsername(randomAscii(51));
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void password() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 8, 60);

        user.setPassword(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setPassword("");
        assertSingleViolation(sizeErrorMessage);

        user.setPassword(randomAscii(7));
        assertSingleViolation(sizeErrorMessage);

        user.setPassword(randomAscii(61));
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void title() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 0, 10);

        user.setTitle(randomAscii(11));
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void firstName() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 1, 50);

        user.setFirstName(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setFirstName("");
        assertSingleViolation(sizeErrorMessage);

        user.setFirstName(randomAscii(51));
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void lastName() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 1, 50);

        user.setLastName(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setLastName("");
        assertSingleViolation(sizeErrorMessage);

        user.setLastName(randomAscii(51));
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void email() {
        final String emailErrorMessage = "invalid email";

        user.setEmail(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setEmail("");
        assertSingleViolation(emailErrorMessage);

        user.setEmail(randomAscii(4));
        assertSingleViolation(emailErrorMessage);
    }

    @Test
    public void mobile() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 11, 25);

        user.setMobile(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setMobile("");
        assertSingleViolation(sizeErrorMessage);

        user.setMobile(randomNumeric(10));
        assertSingleViolation(sizeErrorMessage);

        user.setMobile(randomNumeric(26));
        assertSingleViolation(sizeErrorMessage);
    }

    private void assertSingleViolation(String expectedErrorMessage) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.toString(), 1, violations.size());
        final ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().toString(), expectedErrorMessage, violation.getMessage());
    }

    private User createUser() {
        User user = new User();
        user.setUsername(randomAlphabetic(10));
        user.setPassword(randomAlphanumeric(10));
        user.setTitle("Mr");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@who.where");
        user.setMobile("123456789012");
        user.setCountry(User.Country.UK);
        user.setPostcode("p05tc0d3");
        user.setAboutMe("about");
        return user;
    }
}
package uk.co.yottr.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.testconfig.UnitConfig;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UnitConfig.class})
public class UserTest {

    @Autowired
    private Validator validator;

    private User user;

    private static final String SIZE_ERROR_MSG = "size must be between %d and %d";
    private static final String NOT_NULL_ERROR_MSG = "required";

    @Autowired
    private UserRepository mockUserRepository;

    @BeforeEach
    public void beforeEachTest() {
        reset(mockUserRepository);
        user = createUser();
    }

    @Test
    public void allValid() {
        final Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "not expecting any violations");
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

        user.setUsername(secure().nextAscii(51));
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void password() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 8, 60);

        user.setPassword(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setPassword("");
        assertSingleViolation(sizeErrorMessage);

        user.setPassword(secure().nextAscii(7));
        assertSingleViolation(sizeErrorMessage);

        user.setPassword(secure().nextAscii(61));
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void title() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 0, 10);

        user.setTitle(secure().nextAscii(11));
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void firstName() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 1, 50);

        user.setFirstName(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setFirstName("");
        assertSingleViolation(sizeErrorMessage);

        user.setFirstName(secure().nextAscii(51));
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void lastName() {
        final String sizeErrorMessage = String.format(SIZE_ERROR_MSG, 1, 50);

        user.setLastName(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setLastName("");
        assertSingleViolation(sizeErrorMessage);

        user.setLastName(secure().nextAscii(51));
        assertSingleViolation(sizeErrorMessage);
    }

    @Test
    public void email() {
        final String emailErrorMessage = "invalid email";

        user.setEmail(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setEmail("");
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setEmail("asdf");
        assertSingleViolation(emailErrorMessage);

        user.setEmail("two@at@signs.test");
        assertSingleViolation(emailErrorMessage);
    }

    @Test
    public void mobile() {
        final String phoneErrorMessage = "must be a phone number";

        user.setMobile(null);
        assertSingleViolation(NOT_NULL_ERROR_MSG);

        user.setMobile("");
        assertSingleViolation(phoneErrorMessage);

        user.setMobile(secure().nextNumeric(6));
        assertSingleViolation(phoneErrorMessage);

        user.setMobile(secure().nextNumeric(26));
        assertSingleViolation(phoneErrorMessage);

        user.setMobile("07700 9OO123");
        assertSingleViolation(phoneErrorMessage);
    }

    @Test
    public void mobileAcceptsTheWaysPeopleWriteNumbers() {
        for (String mobile : new String[] { "07700900123", "07700 900123", "+44 7700 900123", "+44 (0)7700 900-123", "1234567" }) {
            user.setMobile(mobile);
            assertTrue(validator.validate(user).isEmpty(), mobile + " should be valid");
        }
    }

    @Test
    public void maxListings() {
        final String maxListingsErrorMessage = "must not be negative";

        user.setMaxListings(-1);
        assertSingleViolation(maxListingsErrorMessage);
    }

    @Test
    public void maxListingsDefault() {
        assertEquals(Integer.valueOf(2), new User().getMaxListings());
    }

    @Test
    public void isAllowedMoreListingsReturnsTrueWhenLimitNotReached() {
        user.setMaxListings(2);
        new Boat(user);

        assertTrue(user.isAllowedMoreListings(), "user should be allowed more listings");
    }

    @Test
    public void isAllowedMoreListingsReturnsFalseWhenLimitReached() {
        user.setMaxListings(1);
        new Boat(user);

        assertFalse(user.isAllowedMoreListings(), "user should not be allowed more listings");
    }

    private void assertSingleViolation(String expectedErrorMessage) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), violations.toString());
        final ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(expectedErrorMessage, violation.getMessage(), violation.getPropertyPath().toString());
    }

    private User createUser() {
        User user = new User();
        user.setUsername(secure().nextAlphabetic(10));
        user.setPassword(secure().nextAlphanumeric(10));
        user.setTitle("Mr");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@who.where");
        user.setMobile("123456789012");
        user.setCountry(Country.UK);
        user.setPostcode("p05tc0d3");
        user.setAboutMe("about");
        return user;
    }
}
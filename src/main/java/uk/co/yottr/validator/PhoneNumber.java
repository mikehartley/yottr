package uk.co.yottr.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

/**
 * A phone number as people write one: digits, spaces, brackets and dashes, perhaps starting with +, and 7 to 25
 * characters long. It leaves null to @NotNull.
 */
@Documented
@Constraint(validatedBy = {})
@Pattern(regexp = "\\+?[0-9 ()-]{7,25}")
@ReportAsSingleViolation
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {

    String message() default "must be a phone number";

    // required but not used
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

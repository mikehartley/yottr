package uk.co.yottr.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * A password that is long enough, not too long for bcrypt, and not one of the most common. There are no rules
 * about mixing upper case, digits and symbols, which make passwords harder to remember without making them much
 * harder to guess. It leaves null to @NotNull.
 */
@Documented
@Constraint(validatedBy = AcceptablePasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptablePassword {

    String message() default "is not an acceptable password";

    // required but not used
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

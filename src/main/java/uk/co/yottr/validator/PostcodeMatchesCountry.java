package uk.co.yottr.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * A UK address needs a UK postcode; anywhere else the postcode is optional and free text. The violation is reported
 * on the postcode, so a form shows it beside that field.
 */
@Documented
@Constraint(validatedBy = PostcodeMatchesCountryValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostcodeMatchesCountry {

    String message() default "must be a UK postcode";

    // required but not used
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

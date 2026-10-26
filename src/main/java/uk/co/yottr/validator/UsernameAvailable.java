package uk.co.yottr.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameAvailableValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameAvailable {

    String message() default "Username not available";

    // required but not used
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

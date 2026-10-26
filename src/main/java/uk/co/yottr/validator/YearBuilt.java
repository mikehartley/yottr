package uk.co.yottr.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = YearBuiltValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface YearBuilt {

    String message() default "{validation.year.built}";

    // required but not used
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
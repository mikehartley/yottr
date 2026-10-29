package uk.co.yottr.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Text with none of the words in offensive-words.txt (#35). It's refused, with the word named, so the member can
 * reword it; nothing is changed or quietly flagged.
 */
@Documented
@Constraint(validatedBy = InoffensiveValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inoffensive {

    String message() default "{validation.inoffensive}";

    // required but not used
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

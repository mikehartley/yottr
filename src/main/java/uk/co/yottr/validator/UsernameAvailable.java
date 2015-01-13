package uk.co.yottr.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Documented
@Constraint(validatedBy = UsernameAvailableValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameAvailable {

    String message() default "Username not available";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

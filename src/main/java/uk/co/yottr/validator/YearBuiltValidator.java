package uk.co.yottr.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class YearBuiltValidator implements ConstraintValidator<YearBuilt, Integer> {

    public static final int EARLIEST_EXPECTED_YEAR = 1900;

    @Override
	public void initialize(YearBuilt constraintAnnotation) {
	}

	@Override
	public boolean isValid(Integer year, ConstraintValidatorContext ctx) {
		if(year == null){
			return false;
		}

        final int latestExpectedYear = Calendar.getInstance().get(Calendar.YEAR) + 2;

		final boolean isValid = year >= EARLIEST_EXPECTED_YEAR && year <= latestExpectedYear;

		if (!isValid)
		{
			ctx.disableDefaultConstraintViolation();
			ctx.buildConstraintViolationWithTemplate("{validation.year.built.2}").addPropertyNode("msg").addConstraintViolation();
		}

		return isValid;
	}

}

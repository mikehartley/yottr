package uk.co.yottr.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.time.Year;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class YearBuiltValidator implements ConstraintValidator<YearBuilt, Integer> {

    public static final int EARLIEST_EXPECTED_YEAR = 1900;

    @Override
	public void initialize(YearBuilt constraintAnnotation) {
	}

	/**
	 * A null year is left to @NotNull, so that a missing year gets the single "required" message.
	 */
	@Override
	public boolean isValid(Integer year, ConstraintValidatorContext ctx) {
		if(year == null){
			return true;
		}

        final int latestExpectedYear = Year.now().getValue() + 2;

		final boolean isValid = year >= EARLIEST_EXPECTED_YEAR && year <= latestExpectedYear;

		if (!isValid && ctx != null)
		{
			ctx.unwrap(HibernateConstraintValidatorContext.class)
					.addMessageParameter("min", EARLIEST_EXPECTED_YEAR)
					.addMessageParameter("max", latestExpectedYear);
		}

		return isValid;
	}

}

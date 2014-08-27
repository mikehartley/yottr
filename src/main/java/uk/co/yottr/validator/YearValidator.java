package uk.co.yottr.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class YearValidator implements ConstraintValidator<Year, Integer> {

    public static final int EARLIEST_EXPECTED_YEAR = 1850;

    @Override
	public void initialize(Year paramA) {
	}

	@Override
	public boolean isValid(Integer year, ConstraintValidatorContext ctx) {
		if(year == null){
			return false;
		}

        final int nextYear = Calendar.getInstance().get(Calendar.YEAR) + 1;

        return year >= EARLIEST_EXPECTED_YEAR && year <= nextYear;
	}

}

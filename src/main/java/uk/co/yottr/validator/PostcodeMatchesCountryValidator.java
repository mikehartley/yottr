package uk.co.yottr.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.co.yottr.form.PersonalDetailsForm;
import uk.co.yottr.model.Country;

import java.util.regex.Pattern;

public class PostcodeMatchesCountryValidator implements ConstraintValidator<PostcodeMatchesCountry, PersonalDetailsForm> {

    /**
     * The outward code (area, district, perhaps a sub-district letter), an optional space, then the inward code.
     * It checks the shape only: it can't tell whether the postcode exists.
     */
    static final Pattern UK_POSTCODE = Pattern.compile("[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValid(PersonalDetailsForm form, ConstraintValidatorContext ctx) {
        if (form.getCountry() != Country.UK) {
            return true;
        }

        final String postcode = form.getPostcode() == null ? "" : form.getPostcode().trim();
        if (UK_POSTCODE.matcher(postcode).matches()) {
            return true;
        }

        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(ctx.getDefaultConstraintMessageTemplate())
                .addPropertyNode("postcode")
                .addConstraintViolation();
        return false;
    }
}

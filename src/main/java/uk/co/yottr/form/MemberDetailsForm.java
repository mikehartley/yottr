package uk.co.yottr.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import uk.co.yottr.model.User;

/**
 * An administrator's changes to a member's details, which can also set how many listings they may have. The
 * username and password can't be changed here.
 */
public class MemberDetailsForm extends PersonalDetailsForm {

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Min(value = 0, message = "must not be negative")
    @Max(99)
    private Integer maxListings;

    public static MemberDetailsForm from(User user) {
        final MemberDetailsForm form = new MemberDetailsForm();
        form.copyFrom(user);
        form.maxListings = user.getMaxListings();
        return form;
    }

    public void applyTo(User user) {
        copyTo(user);
        user.setMaxListings(maxListings);
    }

    public Integer getMaxListings() {
        return maxListings;
    }

    public void setMaxListings(Integer maxListings) {
        this.maxListings = maxListings;
    }
}

package uk.co.yottr.form;

import uk.co.yottr.model.User;
import uk.co.yottr.validator.AcceptablePassword;

/**
 * A member's changes to their own details. The username can't be changed, and a blank password means
 * keep the current one.
 */
public class MyDetailsForm extends PersonalDetailsForm {

    @AcceptablePassword
    private String password;

    /**
     * The form for a user's current details. The password is left blank: it is never sent back to the page.
     */
    public static MyDetailsForm from(User user) {
        final MyDetailsForm form = new MyDetailsForm();
        form.copyFrom(user);
        return form;
    }

    /**
     * Copies the details onto the user. The new password, if there is one, is left for the caller to encode.
     */
    public void applyTo(User user) {
        copyTo(user);
    }

    public boolean isChangingPassword() {
        return password != null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null || password.isEmpty() ? null : password;
    }
}

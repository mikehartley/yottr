package uk.co.yottr.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uk.co.yottr.model.User;
import uk.co.yottr.validator.AcceptablePassword;
import uk.co.yottr.validator.Inoffensive;
import uk.co.yottr.validator.UsernameAvailable;

public class SignupForm extends PersonalDetailsForm {

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 2, max = 50)
    @UsernameAvailable
    @Inoffensive
    private String username;

    @NotNull(message = REQUIRED_ERROR_MSG)
    @AcceptablePassword
    private String password;

    /**
     * A new user with these details and the plain-text password, still to be encoded. Roles, the listing
     * limit and whether the account is enabled are the caller's to decide.
     */
    public User toUser() {
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        copyTo(user);
        return user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

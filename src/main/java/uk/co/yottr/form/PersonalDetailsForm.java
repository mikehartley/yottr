package uk.co.yottr.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * The details a member can enter about themselves, on sign-up and on My Details.
 * <p>
 * The forms bind to these classes rather than to {@link User}, so a request can only set the fields that are
 * on the form: never the id, roles, listing limit or whether the account is enabled.
 */
public abstract class PersonalDetailsForm {

    static final String REQUIRED_ERROR_MSG = "required";

    @Size(max = 10)
    private String title;

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 1, max = 50)
    private String firstName;

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 1, max = 50)
    private String lastName;

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Pattern(regexp = ".*@.*", message = "invalid email")
    private String email;

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 11, max = 25)
    private String mobile;

    @NotNull
    private Country country;

    @NotNull
    private String postcode;

    @Size(max = 400)
    private String aboutMe;

    void copyFrom(User user) {
        title = user.getTitle();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        mobile = user.getMobile();
        country = user.getCountry();
        postcode = user.getPostcode();
        aboutMe = user.getAboutMe();
    }

    void copyTo(User user) {
        user.setTitle(title);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setCountry(country);
        user.setPostcode(postcode);
        user.setAboutMe(aboutMe);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }
}

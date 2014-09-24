package uk.co.yottr.model;

import uk.co.yottr.security.Role;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Entity
@Table(name = "users")
public class User {

    private static final String REQUIRED_ERROR_MSG = "required";

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<UserRole> userRoles = new HashSet<>(0);

    @Column(name = "username", unique = true, nullable = false, length = 50)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 2, max = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 60)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 8, max = 60)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "title", length = 10)
    @Size(max = 10)
    private String title;

    @Column(name = "first_name", nullable = false, length = 50)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 1, max = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 1, max = 50)
    private String lastName;

    @Column(name = "email", nullable = false)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Pattern(regexp = ".*@.*", message = "invalid email")
    private String email;

    @Column(name = "mobile", length = 25)
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 11, max = 25)
    private String mobile;

    public enum Country {
        UK, OTHER
    }

    @Column(name = "country")
    @NotNull
    private Country country;

    @Column(name = "postcode")
    @NotNull
    private String postcode;

    @Column(name = "about_me", length = 400)
    @Size(max = 400)
    private String aboutMe;

    public Long getId() {
        return id;
    }

    public Collection<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Collection<UserRole> userRoles) {
        this.userRoles = userRoles;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public void addRole(Role role) {
        Collection<UserRole> newRoles = new ArrayList<>(userRoles);
        newRoles.add(new UserRole(this, role));
        setUserRoles(newRoles);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + getUserRoles() +
                '}';
    }
}

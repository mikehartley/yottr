package uk.co.yottr.builder;

import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static uk.co.yottr.builder.BoatBuilder.aBoat;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class UserBuilder {

    private User user;

    public UserBuilder() {
        User user = new User();

        user.setUsername("user" + randomNumeric(10));
        user.setPassword("password" + randomAlphanumeric(6));
        user.setTitle("Mr");
        user.setFirstName("Izzy");
        user.setLastName("Wizzy");
        user.setEmail("izzy@wizzy.test");
        user.setMobile(randomNumeric(5) + " " + randomNumeric(6));
        user.setCountry(Country.UK);
        user.setPostcode("W8 4QT");
        user.setAboutMe("This is a bunch of text followed by some random characters " + randomAlphanumeric(50));
        user.setEnabled(true);

        this.user = user;
    }

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public UserBuilder withUsername(String username) {
        user.setUsername(username);
        return this;
    }

    public UserBuilder withBoat(BoatBuilder boatBuilder) {
        boatBuilder.withOwner(user).build();

        return this;
    }

    public UserBuilder withBoat() {
        return withBoat(aBoat());
    }

    public UserBuilder withMaxListings(int maxListings) {
        user.setMaxListings(maxListings);
        return this;
    }

    public User build() {
        return user;
    }
}

package uk.co.yottr.builder;

import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static uk.co.yottr.builder.BoatBuilder.aBoat;

public class UserBuilder {

    private User user;

    public UserBuilder() {
        User user = new User();

        user.setUsername("user" + secure().nextNumeric(10));
        user.setPassword("password" + secure().nextAlphanumeric(6));
        user.setTitle("Mr");
        user.setFirstName("Izzy");
        user.setLastName("Wizzy");
        user.setEmail("izzy@wizzy.test");
        user.setMobile(secure().nextNumeric(5) + " " + secure().nextNumeric(6));
        user.setCountry(Country.UK);
        user.setPostcode("W8 4QT");
        user.setAboutMe("This is a bunch of text followed by some random characters " + secure().nextAlphanumeric(50));
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

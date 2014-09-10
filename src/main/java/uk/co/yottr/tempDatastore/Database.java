package uk.co.yottr.tempDatastore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.security.Roles;

import java.util.*;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * For in-memory testing when a database isn't required.
 */
public class Database {

    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    private static Map<Long, User> users = new HashMap<>();
    private static Collection<Boat> boats = new ArrayList<>();

    static {
        initialiseUsers();
        initialiseBoats();
    }

    public Map<Long, User> getUsers() {
        return users;
    }

    public Collection<Boat> getBoats() {
        return boats;
    }

    private static void initialiseUsers() {
        addUser("mike");
    }

    private static void initialiseBoats() {
        boats.add(createBoat("Y0001", "Halberg Rassy", "HR42", 42, Boat.HullType.MONO, "Looking for some crew to do some sailing."));
        boats.add(createBoat("Y0002", "Jaguar", "SeaCat", 55, Boat.HullType.MULTI, "I just like to motor about then have some lunch."));
    }

    private static Boat createBoat(String reference, String manufacturer, String model, int length,
                            Boat.HullType hullType, String description) {
        Boat boat = new Boat();
        boat.setReference(reference);
        boat.setManufacturer(manufacturer);
        boat.setModel(model);
        boat.setLength(length);
        boat.setHullType(hullType);
        boat.setDesc(description);
        return boat;
    }

    private static void addUser(final String usernamePassword) {
        User newUser = createUser(usernamePassword);
//        users.put(newUser.getId(), newUser);
        LOG.info("added user: " + newUser);
    }

    private static User createUser(final String usernamePassword) {
        User user = new User();
        user.setUsername(usernamePassword);
        user.setPassword(usernamePassword);
        user.setAuthorities(Arrays.asList(
                new SimpleGrantedAuthority(Roles.FREE.name()),
                new SimpleGrantedAuthority(Roles.CREW.name()),
                new SimpleGrantedAuthority(Roles.ADMIN.name())));
        user.setEnabled(true);

        user.setTitle("Mr");
        user.setFirstName("Stephen");
        user.setLastName("Clark");
        user.setCountry(User.Country.UK);
        user.setPostcode("W12 8QT");
        user.setEmail("test@null.and.void");
        user.setMobile("07123 456789");
        user.setAboutMe("I like to sail.");

        return user;
    }
}

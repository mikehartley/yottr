package uk.co.yottr.initialise;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.co.yottr.config.AppConfig;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.security.Roles;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * This initialises the database and shouldn't be run as part of the normal set of tests,
 * and so should be left in a disabled state unless required.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
@WebAppConfiguration
public class InitialiseDatabase {

    private boolean ENABLED = false;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void ensureRepositoryHasBeenInjected() {

        Assert.assertFalse("This shouldn't normally be enabled.", ENABLED);

        Assert.assertNotNull("check spring configuration : userRepository was not injected", userRepository);
    }

    @Test
    public void setupAdminUser() {
        if (!ENABLED) return;

        User user = new User();

        user.setUsername("mike");
        user.setPassword("lucidlucid123");
        user.addRole(Roles.ADMIN.name());
        user.addRole(Roles.CREW.name());
        user.addRole(Roles.FREE.name());
        user.setEnabled(true);
        user.setTitle("Mr");
        user.setFirstName("Mike");
        user.setLastName("H");
        user.setEmail("m@yottr.co.uk");
        user.setAboutMe("Nothing to see here.");
        user.setCountry(User.Country.UK);
        user.setMobile("07973 000000");
        user.setPostcode("DY8 1AA");

        userRepository.save(user);
    }
}

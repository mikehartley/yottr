package uk.co.yottr.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.model.User;
import uk.co.yottr.testconfig.IntegrationTestConfig;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationTestConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void paging() {
        for (int i = 0; i < 12; i++) {
            userRepository.save(createUser("user" + i));
        }

        final Page<User> page1 = userRepository.findAll(new PageRequest(0, 4, Sort.Direction.ASC, "id"));
        assertEquals(4, page1.getSize());

        int i = 0;
        for (User user : page1) {
            assertEquals("user" + i++, user.getUsername());
        }
    }

    private User createUser(String username) {
        User user = new User();

        user.setUsername(username);
        user.setPassword("password");
        user.setFirstName("Izzy");
        user.setLastName("Wizzy");
        user.setEmail("izzy@wizzy.test");
        user.setMobile("01234 123456");
        user.setCountry(User.Country.UK);
        user.setPostcode("W8 4QT");

        return user;
    }
}
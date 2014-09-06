package uk.co.yottr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring-context-test.xml")
public class TestServiceTest {

    @Autowired
    private TestService testService;

    @Before
    public void setUp() throws Exception {
        testService.deleteAll();
    }

    @Test
    public void testFindByTextLike() throws Exception {
        final String someTestText = "this is some test text";
        testService.save(someTestText);

        assertEquals("count", 1, testService.count());

        assertEquals("find using like", someTestText, testService.findByTextLike("%some%").getText());

        assertEquals("find using findAll", someTestText, testService.findAll().iterator().next().getText());
    }
}
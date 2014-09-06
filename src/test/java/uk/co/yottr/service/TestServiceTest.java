package uk.co.yottr.service;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

        final long count = testService.count();
        Assert.assertEquals("count", 1, count);
    }
}
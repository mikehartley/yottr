package uk.co.yottr.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.yottr.model.TestEntity;

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

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testFindByTextLike() throws Exception {
        final String someTestText = "this is some test text";
        testService.save(someTestText);

        final TestEntity theFindResult = testService.findByTextLike("this");
        Assert.assertEquals(someTestText, theFindResult.getText());
    }
}
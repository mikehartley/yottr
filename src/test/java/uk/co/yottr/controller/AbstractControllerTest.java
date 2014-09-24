package uk.co.yottr.controller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.yottr.testconfig.ControllerTestConfig;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ControllerTestConfig.class})
@WebAppConfiguration
public abstract class AbstractControllerTest {
    public static final String PATH_TO_VIEWS = "/WEB-INF/views/";
    public static final String JSP_SUFFIX = ".jsp";

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    String urlForView(String viewName) {
        return PATH_TO_VIEWS + viewName + JSP_SUFFIX;
    }

    @Before
    public void instantiateMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
}
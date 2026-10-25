package uk.co.yottr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * Controllers with mocked services and no security filters, as the old ControllerTestConfig set up.
 */
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
public abstract class AbstractControllerTest {

    @Autowired
    MockMvc mockMvc;

    // needed by UsernameAvailableValidator
    @MockitoBean
    UserRepository mockUserRepository;

    @MockitoBean
    BoatService mockBoatService;

    @MockitoBean
    UserService mockUserService;

    @MockitoBean
    ReferenceDataService mockReferenceDataService;

    @MockitoBean
    LoggersEndpoint mockLoggersEndpoint;
}

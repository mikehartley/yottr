package uk.co.yottr.testconfig;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

import org.springframework.data.domain.PageRequest;

public class ConstantsForTests {
    public static final int DEFAULT_PAGE_REQUEST_START_PAGE = 0;
    public static final int DEFAULT_PAGE_REQUEST_END_PAGE = 4;
    public static final PageRequest DEFAULT_PAGE_REQUEST = new PageRequest(DEFAULT_PAGE_REQUEST_START_PAGE, DEFAULT_PAGE_REQUEST_END_PAGE);
}

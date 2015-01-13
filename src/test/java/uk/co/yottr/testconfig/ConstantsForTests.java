package uk.co.yottr.testconfig;

import org.springframework.data.domain.PageRequest;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class ConstantsForTests {
    public static final int DEFAULT_PAGE_REQUEST_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 4;
    public static final PageRequest DEFAULT_PAGE_REQUEST = new PageRequest(DEFAULT_PAGE_REQUEST_PAGE, DEFAULT_PAGE_SIZE);
}

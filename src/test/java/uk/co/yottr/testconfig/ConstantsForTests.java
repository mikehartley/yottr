package uk.co.yottr.testconfig;

import org.springframework.data.domain.PageRequest;

public class ConstantsForTests {
    public static final int DEFAULT_PAGE_REQUEST_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 4;
    public static final PageRequest DEFAULT_PAGE_REQUEST = PageRequest.of(DEFAULT_PAGE_REQUEST_PAGE, DEFAULT_PAGE_SIZE);
}

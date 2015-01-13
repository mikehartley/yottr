package uk.co.yottr.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import uk.co.yottr.model.Boat;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class PageWrapperTest {

    private Page<Boat> mockPage;
    private PageWrapper<Boat> wrapper;

    /*
         Range radius of 3, =range of 6:
         0 1 2 [3] 4 5 6
    */

    private static int RANGE_RADIUS = 3;
    private static int RANGE = RANGE_RADIUS * 2;

    @Before
    public void setUp() throws Exception {
        mockPage = mock(Page.class);
        wrapper = new PageWrapper<>(mockPage);
    }

    @Test
    public void testGetPage() throws Exception {
        PageWrapper<Boat> wrapper = new PageWrapper<>(mockPage);

        assertEquals(mockPage, wrapper.getPage());
    }

    @Test
    public void inMiddleOfRange() {
        // 0 1 2 3 4 5 6 7 8 9
        //     - - - * - - -
        int currentPage = 5;
        int totalPages = 10;
        int expectedLowerLimit = 2;
        int expectedUpperLimit = 8;

        when(mockPage.getNumber()).thenReturn(currentPage);
        when(mockPage.getTotalPages()).thenReturn(totalPages);

        assertEquals("lower limit", expectedLowerLimit, wrapper.getRangeStart());
        assertEquals("upper limit", expectedUpperLimit, wrapper.getRangeEnd());
    }

    @Test
    public void atStartOfRange() {
        // 0 1 2 3 4 5 6 7 8 9
        // * - - - - - -
        int currentPage = 0;
        int totalPages = 10;
        int expectedLowerLimit = 0;
        int expectedUpperLimit = 6;

        when(mockPage.getNumber()).thenReturn(currentPage);
        PageWrapper<Boat> wrapper = new PageWrapper<>(mockPage);
        when(mockPage.getTotalPages()).thenReturn(totalPages);

        assertEquals("lower limit", expectedLowerLimit, wrapper.getRangeStart());
        assertEquals("upper limit", expectedUpperLimit, wrapper.getRangeEnd());
    }

    @Test
    public void nearStartOfRange() {
        // 0 1 2 3 4 5 6 7 8 9
        // - - * - - - -
        int currentPage = 2;
        int totalPages = 10;
        int expectedLowerLimit = 0;
        int expectedUpperLimit = 6;

        when(mockPage.getNumber()).thenReturn(currentPage);
        when(mockPage.getTotalPages()).thenReturn(totalPages);

        assertEquals("lower limit", expectedLowerLimit, wrapper.getRangeStart());
        assertEquals("upper limit", expectedUpperLimit, wrapper.getRangeEnd());
    }

    @Test
    public void atEndOfRange() {
        // 0 1 2 3 4 5 6 7 8 9
        //       - - - - - - *
        int currentPage = 9;
        int totalPages = 10;
        int expectedLowerLimit = 3;
        int expectedUpperLimit = 9;

        when(mockPage.getNumber()).thenReturn(currentPage);
        when(mockPage.getTotalPages()).thenReturn(totalPages);
        PageWrapper<Boat> wrapper = new PageWrapper<>(mockPage);

        assertEquals("lower limit", expectedLowerLimit, wrapper.getRangeStart());
        assertEquals("upper limit", expectedUpperLimit, wrapper.getRangeEnd());
    }

    @Test
    public void nearEndOfRange() {
        // 0 1 2 3 4 5 6 7 8 9
        //       - - - - * - -
        int currentPage = 7;
        int totalPages = 10;
        int expectedLowerLimit = 3;
        int expectedUpperLimit = 9;

        when(mockPage.getNumber()).thenReturn(currentPage);
        when(mockPage.getTotalPages()).thenReturn(totalPages);

        assertEquals("lower limit", expectedLowerLimit, wrapper.getRangeStart());
        assertEquals("upper limit", expectedUpperLimit, wrapper.getRangeEnd());
    }

    @Test
    public void whenCurrentPageInMiddleAndRangeIsBiggerThanTotalPagesThenUpperLimitIsShortened() {
        // 0 1 2
        // - * -
        int currentPage = 1;
        int totalPages = 3;
        int expectedLowerLimit = 0;
        int expectedUpperLimit = 2;

        when(mockPage.getNumber()).thenReturn(currentPage);
        when(mockPage.getTotalPages()).thenReturn(totalPages);

        assertEquals("lower limit", expectedLowerLimit, wrapper.getRangeStart());
        assertEquals("upper limit", expectedUpperLimit, wrapper.getRangeEnd());
    }

    @Test
    public void whenCurrentPageAtStartAndRangeIsBiggerThanTotalPagesThenUpperLimitIsShortened() {
        // 0 1 2 3 4 5
        // * - - - - -
        int currentPage = 0;
        int totalPages = 6;
        int expectedLowerLimit = 0;
        int expectedUpperLimit = 5;

        when(mockPage.getNumber()).thenReturn(currentPage);
        when(mockPage.getTotalPages()).thenReturn(totalPages);

        assertEquals("lower limit", expectedLowerLimit, wrapper.getRangeStart());
        assertEquals("upper limit", expectedUpperLimit, wrapper.getRangeEnd());
    }

    @Test
    public void whenCurrentPageAtEndAndRangeIsBiggerThanTotalPagesThenUpperLimitIsShortened() {
        // 0 1 2 3 4 5
        // - - - - - *
        int currentPage = 5;
        int totalPages = 6;
        int expectedLowerLimit = 0;
        int expectedUpperLimit = 5;

        when(mockPage.getNumber()).thenReturn(currentPage);
        when(mockPage.getTotalPages()).thenReturn(totalPages);

        assertEquals("lower limit", expectedLowerLimit, wrapper.getRangeStart());
        assertEquals("upper limit", expectedUpperLimit, wrapper.getRangeEnd());
    }
}
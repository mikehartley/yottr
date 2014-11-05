package uk.co.yottr.controller;

import org.springframework.data.domain.Page;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class PageWrapper<T> {

    private static int RANGE_RADIUS = 3;

    private Page<T> page;

    public PageWrapper(Page<T> page) {
        this.page = page;
    }

    public Page<T> getPage() {
        return page;
    }

    public int getRangeStart() {
        int rangeStart;

        if (rangeOverlapsBeginning() || rangeWidthWouldBeBiggerThanRequired()) {
            rangeStart = 0;
        }
        else if (rangeOverlapsEnd()) {
            rangeStart = maxPageNumber() - (rangeWidth());
        }
        else {
            rangeStart = page.getNumber() - RANGE_RADIUS;
        }

        return rangeStart;
    }

    public int getRangeEnd() {
        int rangeEnd;

        if (rangeOverlapsEnd() || rangeWidthWouldBeBiggerThanRequired()) {
            rangeEnd = maxPageNumber();
        }
        else if (rangeOverlapsBeginning()) {
            rangeEnd = rangeWidth();
        }
        else {
            rangeEnd = page.getNumber() + RANGE_RADIUS;
        }

        return rangeEnd;
    }

    private boolean rangeWidthWouldBeBiggerThanRequired() {
        return rangeWidth() > maxPageNumber();
    }

    private int rangeWidth() {
        return RANGE_RADIUS * 2;
    }

    private boolean rangeOverlapsBeginning() {
        return page.getNumber() - RANGE_RADIUS < 0;
    }

    private boolean rangeOverlapsEnd() {
        return page.getNumber() + RANGE_RADIUS > maxPageNumber();
    }

    private int maxPageNumber() {
        return page.getTotalPages() - 1;
    }
}

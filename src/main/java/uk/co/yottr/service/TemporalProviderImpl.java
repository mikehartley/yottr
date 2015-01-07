package uk.co.yottr.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Component
public class TemporalProviderImpl implements TemporalProvider {
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}

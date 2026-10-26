package uk.co.yottr.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TemporalProviderImpl implements TemporalProvider {
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}

package uk.co.yottr.repository;

import org.springframework.data.domain.Page;
import uk.co.yottr.model.Boat;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class BoatRepositoryImpl implements BoatSearch {

    @Override
    public Page<Boat> find(String reference) {
        return null;
    }
}

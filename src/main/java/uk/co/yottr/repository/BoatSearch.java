package uk.co.yottr.repository;

import org.springframework.data.domain.Page;
import uk.co.yottr.model.Boat;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface BoatSearch {
    Page<Boat> find(String reference);

    //TODO implement custom repository then add interface to main one. see notes.
}

package uk.co.yottr.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.yottr.model.Boat;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface BoatRepository extends CrudRepository<Boat, Long> {
    Boat findByReference(String reference);
}

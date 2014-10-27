package uk.co.yottr.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import uk.co.yottr.model.Boat;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface BoatRepository extends PagingAndSortingRepository<Boat, Long> {
    Boat findByReference(String reference);
}

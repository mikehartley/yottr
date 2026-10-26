package uk.co.yottr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.co.yottr.model.Boat;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface BoatRepository extends PagingAndSortingRepository<Boat, Long>, CrudRepository<Boat, Long> {
    Boat findByReference(String reference);
    Page<Boat> findBySuspended(boolean suspended, Pageable pageable);
}

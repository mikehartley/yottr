package uk.co.yottr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import uk.co.yottr.model.Boat;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface BoatRepository extends PagingAndSortingRepository<Boat, Long> {
    Boat findByReference(String reference);
    Page<Boat> findBySuspended(boolean suspended, Pageable pageable);

    @SuppressWarnings("JpaQlInspection")
    @Query("select b from Boat b where b.reference = :reference")
    Page<Boat> find(@Param("reference") String reference, Pageable pageable);
}

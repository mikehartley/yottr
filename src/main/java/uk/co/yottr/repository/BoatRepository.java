package uk.co.yottr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;

import java.util.List;
import java.util.Optional;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface BoatRepository extends PagingAndSortingRepository<Boat, Long>, CrudRepository<Boat, Long> {
    Boat findByReference(String reference);
    Page<Boat> findBySuspended(boolean suspended, Pageable pageable);

    Optional<Boat> findByReferenceAndOwnerUsername(String reference, String ownerUsername);
    List<Boat> findByOwnerUsernameOrderByReference(String ownerUsername);
    long countByOwner(User owner);
}

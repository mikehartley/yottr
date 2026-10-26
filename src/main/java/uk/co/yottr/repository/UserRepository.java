package uk.co.yottr.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import uk.co.yottr.model.User;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface UserRepository extends PagingAndSortingRepository<User, Long>, CrudRepository<User, Long> {
    User findByUsername(String username);

    /**
     * The user, with their row locked until the transaction ends.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.username = :username")
    User findByUsernameForUpdate(@Param("username") String username);
}

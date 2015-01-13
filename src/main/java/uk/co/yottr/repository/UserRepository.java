package uk.co.yottr.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import uk.co.yottr.model.User;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    User findByUsername(String username);
}

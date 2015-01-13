package uk.co.yottr.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.yottr.model.RyaSailCruisingLevel;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface RyaSailCruisingLevelRepository extends CrudRepository<RyaSailCruisingLevel, Long> {
    RyaSailCruisingLevel findByRank(int rank);
}

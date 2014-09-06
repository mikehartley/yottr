package uk.co.yottr.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.yottr.model.TestEntity;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface TestRepository extends CrudRepository<TestEntity, Integer> {
    TestEntity findByTextLike(String text);
}

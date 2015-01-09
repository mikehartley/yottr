package uk.co.yottr.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.yottr.model.Frequency;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface FrequencyRepository extends CrudRepository<Frequency, String> {
}

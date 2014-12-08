package uk.co.yottr.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.yottr.model.FinancialArrangement;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface FinancialArrangementRepository extends CrudRepository<FinancialArrangement, String> {
}

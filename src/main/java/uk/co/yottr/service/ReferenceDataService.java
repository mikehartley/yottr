package uk.co.yottr.service;

import uk.co.yottr.model.*;

import java.util.List;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public interface ReferenceDataService {
    List<RyaSailCruisingLevel> ryaSailCruisingLevels();
    List<SailingPurpose> sailingPurposes();
    List<Boat.HullType> hullTypes();
    List<Country> countries();
    List<FinancialArrangement> financialArrangements();
}

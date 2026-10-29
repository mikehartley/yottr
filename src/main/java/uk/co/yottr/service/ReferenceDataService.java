package uk.co.yottr.service;

import uk.co.yottr.model.*;

import java.util.List;
import java.util.Map;

public interface ReferenceDataService {
    List<RyaSailCruisingLevel> ryaSailCruisingLevels();
    List<SailingPurpose> sailingPurposes();
    List<Boat.HullType> hullTypes();
    List<Country> countries();

    List<MoneyPeriod> moneyPeriods();

    List<CommercialOffering> commercialOfferings();

    /**
     * The sailing regions in their groups, in the order the choices list them.
     */
    Map<SailingRegion.Group, List<SailingRegion>> sailingRegions();
    List<FinancialArrangement> financialArrangements();
    List<VesselType> vesselTypes();
    List<RoleRequired> rolesRequired();
    List<Frequency> frequencies();
    List<Smoking> smokingOptions();
    List<TravelExpenses> travelExpenses();
    List<CourseLevel> courseLevels();
}

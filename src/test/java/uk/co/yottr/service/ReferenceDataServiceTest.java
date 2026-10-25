package uk.co.yottr.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.yottr.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class ReferenceDataServiceTest {

    private ReferenceDataService referenceDataService;

    @BeforeEach
    public void setup() {
        referenceDataService = new ReferenceDataServiceImpl();
    }

    @Test
    public void ryaSailCruisingLevelsDisplayNameOrdering() {
        final List<RyaSailCruisingLevel> cruisingLevels = referenceDataService.ryaSailCruisingLevels();

        assertEquals(RyaSailCruisingLevel.NONE.getDisplayName(), cruisingLevels.get(0).getDisplayName());
        assertEquals(RyaSailCruisingLevel.START_YACHTING.getDisplayName(), cruisingLevels.get(1).getDisplayName());
        assertEquals(RyaSailCruisingLevel.COMPETENT_CREW.getDisplayName(), cruisingLevels.get(2).getDisplayName());
        assertEquals(RyaSailCruisingLevel.DAY_SKIPPER.getDisplayName(), cruisingLevels.get(3).getDisplayName());
        assertEquals(RyaSailCruisingLevel.COASTAL_SKIPPER.getDisplayName(), cruisingLevels.get(4).getDisplayName());
        assertEquals(RyaSailCruisingLevel.YACHTMASTER_COASTAL.getDisplayName(), cruisingLevels.get(5).getDisplayName());
        assertEquals(RyaSailCruisingLevel.YACHTMASTER_OFFSHORE.getDisplayName(), cruisingLevels.get(6).getDisplayName());
        assertEquals(RyaSailCruisingLevel.YACHTMASTER_OCEAN.getDisplayName(), cruisingLevels.get(7).getDisplayName());
        assertEquals(8, cruisingLevels.size(), "list size");
    }

    @Test
    public void testSailingPurposesOrdering() throws Exception {
        final List<SailingPurpose> sailingPurposes = referenceDataService.sailingPurposes();

        assertEquals(SailingPurpose.CRUISING, sailingPurposes.get(0));
        assertEquals(SailingPurpose.RACING, sailingPurposes.get(1));
        assertEquals(SailingPurpose.DELIVERY, sailingPurposes.get(2));
        assertEquals(SailingPurpose.LONG_TERM, sailingPurposes.get(3));
        assertEquals(SailingPurpose.COURSE, sailingPurposes.get(4));
    }
    
    @Test
    public void testHullTypesOrdering() throws Exception {
        final List<Boat.HullType> hullTypes = referenceDataService.hullTypes();

        assertEquals(Boat.HullType.MONO, hullTypes.get(0));
        assertEquals(Boat.HullType.MULTI, hullTypes.get(1));
    }

    @Test
    public void testCountriesOrdering() throws Exception {
        final List<Country> countries = referenceDataService.countries();

        assertEquals(Country.UK.getDisplayName(), countries.get(0).getDisplayName());
        assertEquals(Country.OTHER.getDisplayName(), countries.get(1).getDisplayName());
    }

    @Test
    public void testFinancialArrangementsOrdering() throws Exception {
        final List<FinancialArrangement> financialArrangementEnums = referenceDataService.financialArrangements();

        assertEquals(FinancialArrangement.FinancialArrangementEnum.NO_CONTRIBUTION.getDisplayName(), financialArrangementEnums.get(0).getDisplayName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.FOOD_ONLY.getDisplayName(), financialArrangementEnums.get(1).getDisplayName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.SHARED_FIXED.getDisplayName(), financialArrangementEnums.get(2).getDisplayName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.SHARED_VARIABLE.getDisplayName(), financialArrangementEnums.get(3).getDisplayName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.COMMERCIAL.getDisplayName(), financialArrangementEnums.get(4).getDisplayName());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.PAID.getDisplayName(), financialArrangementEnums.get(5).getDisplayName());
    }

    @Test
    public void testVesselTypesOrdering() throws Exception {
        final List<VesselType> vesselTypes = referenceDataService.vesselTypes();

        assertEquals(VesselType.SAIL, vesselTypes.get(0));
        assertEquals(VesselType.POWER, vesselTypes.get(1));
        assertEquals(2, vesselTypes.size(), "list size");
    }

    @Test
    public void testRolesRequiredOrdering() throws Exception {
        final List<RoleRequired> rolesRequired = referenceDataService.rolesRequired();

        assertEquals(RoleRequired.CREW, rolesRequired.get(0));
        assertEquals(RoleRequired.MATE, rolesRequired.get(1));
        assertEquals(RoleRequired.SKIPPER, rolesRequired.get(2));
        assertEquals(RoleRequired.DOESNT_MATTER, rolesRequired.get(3));
        assertEquals(4, rolesRequired.size(), "list size");
    }

    @Test
    public void testFrequenciesOrdering() throws Exception {
        final List<Frequency> frequencies = referenceDataService.frequencies();

        assertEquals(Frequency.FrequencyEnum.WEEKDAYS.name(), frequencies.get(0).getName());
        assertEquals(Frequency.FrequencyEnum.WEEKENDS.name(), frequencies.get(1).getName());
        assertEquals(Frequency.FrequencyEnum.HOLIDAYS.name(), frequencies.get(2).getName());
        assertEquals(Frequency.FrequencyEnum.LONG_TERM.name(), frequencies.get(3).getName());
        assertEquals(Frequency.FrequencyEnum.TRIP.name(), frequencies.get(4).getName());
        assertEquals(5, frequencies.size(), "list size");
    }
}
package uk.co.yottr.service;

import org.springframework.stereotype.Service;
import uk.co.yottr.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Service
public class ReferenceDataServiceImpl implements ReferenceDataService {

    @Override
    public List<RyaSailCruisingLevel> ryaSailCruisingLevels() {
        List<RyaSailCruisingLevel> cruisingLevels = new ArrayList<>();

        for (RyaSailCruisingLevel.Level level : RyaSailCruisingLevel.Level.values()) {
            cruisingLevels.add(new RyaSailCruisingLevel(level));
        }

        Collections.sort(cruisingLevels, new RankComparator());

        return cruisingLevels;
    }

    @Override
    public List<SailingPurpose> sailingPurposes() {
        List<SailingPurpose> sailingPurposes = new ArrayList<>();

        sailingPurposes.add(SailingPurpose.CRUISING);
        sailingPurposes.add(SailingPurpose.RACING);
        sailingPurposes.add(SailingPurpose.DELIVERY);
        sailingPurposes.add(SailingPurpose.LONG_TERM);
        sailingPurposes.add(SailingPurpose.COURSE);

        return sailingPurposes;
    }

    @Override
    public List<Boat.HullType> hullTypes() {
        List<Boat.HullType> hullTypes = new ArrayList<>();

        hullTypes.add(Boat.HullType.MONO);
        hullTypes.add(Boat.HullType.MULTI);

        return hullTypes;
    }

    @Override
    public List<Country> countries() {
        List<Country> countries = new ArrayList<>();

        countries.add(Country.UK);
        countries.add(Country.OTHER);

        return countries;
    }

    @Override
    public List<FinancialArrangement> financialArrangements() {
        List<FinancialArrangement> financialArrangementEnums = new ArrayList<>();

        financialArrangementEnums.add(new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.NO_CONTRIBUTION));
        financialArrangementEnums.add(new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.FOOD_ONLY));
        financialArrangementEnums.add(new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.SHARED_FIXED));
        financialArrangementEnums.add(new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.SHARED_VARIABLE));
        financialArrangementEnums.add(new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.COMMERCIAL));
        financialArrangementEnums.add(new FinancialArrangement(FinancialArrangement.FinancialArrangementEnum.PAID));

        return financialArrangementEnums;
    }



    private class RankComparator implements Comparator<RyaSailCruisingLevel> {
        @Override
        public int compare(RyaSailCruisingLevel first, RyaSailCruisingLevel second) {
            Integer firstRank = first.getRank();
            Integer secondRank = second.getRank();
            return firstRank.compareTo(secondRank);
        }
    }
}

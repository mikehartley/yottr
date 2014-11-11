package uk.co.yottr.service;

import org.springframework.stereotype.Service;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.Country;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
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
    public List<SailingStyle> sailingStyles() {
        List<SailingStyle> sailingStyles = new ArrayList<>();

        sailingStyles.add(SailingStyle.CRUISING);
        sailingStyles.add(SailingStyle.RACING);
        sailingStyles.add(SailingStyle.ALL);

        return sailingStyles;
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

    private class RankComparator implements Comparator<RyaSailCruisingLevel> {
        @Override
        public int compare(RyaSailCruisingLevel first, RyaSailCruisingLevel second) {
            Integer firstRank = first.getRank();
            Integer secondRank = second.getRank();
            return firstRank.compareTo(secondRank);
        }
    }
}

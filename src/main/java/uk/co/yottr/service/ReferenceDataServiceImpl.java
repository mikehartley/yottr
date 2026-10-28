package uk.co.yottr.service;

import org.springframework.stereotype.Service;
import uk.co.yottr.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    @Override
    public List<VesselType> vesselTypes() {
        List<VesselType> vesselTypes = new ArrayList<>();

        vesselTypes.add(VesselType.SAIL);
        vesselTypes.add(VesselType.POWER);

        return vesselTypes;
    }

    @Override
    public List<RoleRequired> rolesRequired() {
        List<RoleRequired> rolesRequired = new ArrayList<>();

        rolesRequired.add(RoleRequired.CREW);
        rolesRequired.add(RoleRequired.MATE);
        rolesRequired.add(RoleRequired.SKIPPER);
        rolesRequired.add(RoleRequired.DOESNT_MATTER);

        return rolesRequired;
    }

    @Override
    public List<Frequency> frequencies() {
        List<Frequency> frequencies = new ArrayList<>();

        frequencies.add(new Frequency(Frequency.FrequencyEnum.WEEKDAYS));
        frequencies.add(new Frequency(Frequency.FrequencyEnum.WEEKENDS));
        frequencies.add(new Frequency(Frequency.FrequencyEnum.HOLIDAYS));
        frequencies.add(new Frequency(Frequency.FrequencyEnum.LONG_TERM));
        frequencies.add(new Frequency(Frequency.FrequencyEnum.TRIP));

        return frequencies;
    }

    @Override
    public List<Smoking> smokingOptions() {
        return List.of(Smoking.values());
    }

    @Override
    public List<TravelExpenses> travelExpenses() {
        return List.of(TravelExpenses.values());
    }

    /**
     * In the order the RYA courses are taken.
     */
    @Override
    public List<CourseLevel> courseLevels() {
        return List.of(CourseLevel.values());
    }

    @Override
    public List<MoneyPeriod> moneyPeriods() {
        return List.of(MoneyPeriod.values());
    }

    @Override
    public List<CommercialOffering> commercialOfferings() {
        return List.of(CommercialOffering.values());
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

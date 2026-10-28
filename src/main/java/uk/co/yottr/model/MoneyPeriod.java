package uk.co.yottr.model;

/**
 * What a listing's amount of money is for.
 */
public enum MoneyPeriod implements DisplayableName {

    DAY("per day"),
    WEEK("per week"),
    MONTH("per month"),
    YEAR("per year"),
    TRIP("for the trip"),
    COURSE("for the course");

    private final String displayName;

    private MoneyPeriod(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}

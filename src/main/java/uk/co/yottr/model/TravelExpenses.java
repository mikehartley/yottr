package uk.co.yottr.model;

public enum TravelExpenses implements DisplayableName {
    PAID_BY_CREW("Paid by crew"),
    PAID_BY_OWNER("Paid by owner"),
    NEGOTIABLE("Negotiable");

    private final String displayName;

    private TravelExpenses(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}

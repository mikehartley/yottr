package uk.co.yottr.model;

/**
 * What a business offers crew who pay for it, on a listing whose financial arrangement is commercial.
 */
public enum CommercialOffering implements DisplayableName {

    BAREBOAT_CHARTER("Bareboat charter"),
    SKIPPERED_CHARTER("Skippered charter"),
    SKIPPERED_TRIP("Skippered trip"),
    TUITION("Tuition");

    private final String displayName;

    private CommercialOffering(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}

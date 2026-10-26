package uk.co.yottr.model;

public enum VesselType implements DisplayableName {

    SAIL("Sail"),
    POWER("Power");

    private String displayName;

    private VesselType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}

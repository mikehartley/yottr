package uk.co.yottr.model;

public enum Country implements DisplayableName {

    UK("UK"),
    OTHER("Other");

    private String displayName;

    private Country(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}

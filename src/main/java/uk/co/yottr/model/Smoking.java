package uk.co.yottr.model;

public enum Smoking implements DisplayableName {
    NO("No"),
    YES_OUTSIDE("Yes - outside only"),
    YES_ANYWHERE("Yes - inside and outside");

    private String displayName;

    private Smoking(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}

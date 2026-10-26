package uk.co.yottr.model;

/**
 * Defines the purpose of the sailing.
 */
public enum SailingPurpose implements DisplayableName {
    /**Cruising*/
    CRUISING("Cruising"),
    /**Racing*/
    RACING("Racing"),
    /**Boat delivery*/
    DELIVERY("Delivery"),
    /**An extended period of sailing such as an ocean crossing, or expedition.*/
    LONG_TERM("Long Term"),
    /**Sailing course - RYA probably.*/
    COURSE("Course");

    private final String displayName;

    private SailingPurpose(String displayName) {
        this.displayName = displayName;
    }

    /**
     * A displayable name for the enum value.
     * @return a human readable string.
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }
}

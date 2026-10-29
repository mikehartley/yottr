package uk.co.yottr.model;

import java.util.Arrays;
import java.util.List;

/**
 * Where a listing sails from or to, as an area a sailor would search by. The UK and Ireland are split into their
 * sailing areas, since most listings are there; the rest of the world is in broad regions. V13 lists the names too.
 * Each has a rough centre, for the map, and a reach in kilometres from it.
 */
public enum SailingRegion implements DisplayableName {

    EAST_COAST("East Coast and Thames", true, 51.75, 1.20, 400),
    SOUTH_COAST("Solent and South Coast", true, 50.78, -1.30, 400),
    WEST_COUNTRY("West Country", true, 50.25, -4.60, 400),
    CHANNEL_ISLANDS("Channel Islands", true, 49.45, -2.45, 400),
    WALES_AND_IRISH_SEA("Wales and the Irish Sea", true, 53.00, -4.50, 400),
    WEST_SCOTLAND("West of Scotland", true, 56.30, -5.60, 400),
    EAST_SCOTLAND_AND_NORTH_EAST("East of Scotland and North East England", true, 56.00, -2.50, 400),
    IRELAND("Ireland", true, 52.70, -7.50, 400),

    NORTHERN_EUROPE("Northern Europe and the Baltic", false, 56.00, 11.00, 1500),
    BISCAY_AND_IBERIA("Biscay, Spain and Portugal", false, 44.00, -4.00, 1000),
    MEDITERRANEAN("Mediterranean", false, 38.50, 15.00, 2200),
    ATLANTIC_ISLANDS("Canaries, Madeira and the Azores", false, 32.50, -17.00, 1400),
    CARIBBEAN("Caribbean", false, 15.00, -63.00, 1800),
    NORTH_AMERICA("North America", false, 40.00, -70.00, 5000),
    CENTRAL_AND_SOUTH_AMERICA("Central and South America", false, -15.00, -40.00, 5000),
    AFRICA_AND_INDIAN_OCEAN("Africa and the Indian Ocean", false, -5.00, 50.00, 6000),
    ASIA("Asia", false, 10.00, 105.00, 5000),
    PACIFIC("Pacific", false, -17.50, -150.00, 6000),
    AUSTRALIA_AND_NEW_ZEALAND("Australia and New Zealand", false, -36.00, 160.00, 4500);

    // How far from its centre a place in the region can be, to be believed: a place looked up by name may be
    // another of the same name somewhere else.
    private static final double UK_AND_IRELAND_REACH_KM = 400;
    private static final double FURTHER_AFIELD_REACH_KM = 3500;

    private final String displayName;
    private final boolean ukAndIreland;
    private final Coordinates centre;
    // how far from its centre a place in the region can be, to be believed: a place looked up by name may be
    // another of the same name somewhere else
    private final double reachKm;

    SailingRegion(String displayName, boolean ukAndIreland, double latitude, double longitude, double reachKm) {
        this.displayName = displayName;
        this.ukAndIreland = ukAndIreland;
        this.centre = new Coordinates(latitude, longitude);
        this.reachKm = reachKm;
    }

    /**
     * Roughly the middle of the region's sailing waters, where a listing shows on the map when its place can't be
     * found.
     */
    public Coordinates getCentre() {
        return centre;
    }

    /**
     * Whether a place found at these coordinates could be in the region.
     */
    public boolean reaches(Coordinates place) {
        return centre.kilometresTo(place) <= reachKm;
    }

    /**
     * Whether it's in the UK, Ireland or the Channel Islands, where a place is looked for among those countries.
     */
    public boolean isUkAndIreland() {
        return ukAndIreland;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     * The sailing areas of the UK and Ireland, which the choices list first.
     */
    public static List<SailingRegion> ukAndIreland() {
        return Arrays.stream(values()).filter(region -> region.ukAndIreland).toList();
    }

    /**
     * The rest of the world.
     */
    public static List<SailingRegion> furtherAfield() {
        return Arrays.stream(values()).filter(region -> !region.ukAndIreland).toList();
    }
}

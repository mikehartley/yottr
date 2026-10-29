package uk.co.yottr.model;

import java.util.Arrays;
import java.util.List;

/**
 * Where a listing sails from or to, as an area a sailor would search by. The UK and Ireland are split into their
 * sailing areas, since most listings are there; the rest of the world is in broad regions. V13 lists the names too.
 * Each has a rough centre, for the map, and a reach in kilometres from it.
 */
public enum SailingRegion implements DisplayableName {

    EAST_COAST("East Coast and Thames", Group.UK_AND_IRELAND, 51.75, 1.20, 400),
    SOUTH_COAST("Solent and South Coast", Group.UK_AND_IRELAND, 50.78, -1.30, 400),
    WEST_COUNTRY("West Country", Group.UK_AND_IRELAND, 50.25, -4.60, 400),
    CHANNEL_ISLANDS("Channel Islands", Group.UK_AND_IRELAND, 49.45, -2.45, 400),
    WALES_AND_IRISH_SEA("Wales and the Irish Sea", Group.UK_AND_IRELAND, 53.00, -4.50, 400),
    WEST_SCOTLAND("West of Scotland", Group.UK_AND_IRELAND, 56.30, -5.60, 400),
    EAST_SCOTLAND_AND_NORTH_EAST("East of Scotland and North East England", Group.UK_AND_IRELAND, 56.00, -2.50, 400),
    IRELAND("Ireland", Group.UK_AND_IRELAND, 52.70, -7.50, 400),

    NORTHERN_EUROPE("Northern Europe and the Baltic", Group.FURTHER_AFIELD, 56.00, 11.00, 1500),
    BISCAY_AND_IBERIA("Biscay, Spain and Portugal", Group.FURTHER_AFIELD, 44.00, -4.00, 1000),
    MEDITERRANEAN("Mediterranean", Group.FURTHER_AFIELD, 38.50, 15.00, 2200),
    ATLANTIC_ISLANDS("Canaries, Madeira and the Azores", Group.FURTHER_AFIELD, 32.50, -17.00, 1400),
    CARIBBEAN("Caribbean", Group.FURTHER_AFIELD, 15.00, -63.00, 1800),
    NORTH_AMERICA("North America", Group.FURTHER_AFIELD, 40.00, -70.00, 5000),
    CENTRAL_AND_SOUTH_AMERICA("Central and South America", Group.FURTHER_AFIELD, -15.00, -40.00, 5000),
    AFRICA_AND_INDIAN_OCEAN("Africa and the Indian Ocean", Group.FURTHER_AFIELD, -5.00, 50.00, 6000),
    ASIA("Asia", Group.FURTHER_AFIELD, 10.00, 105.00, 5000),
    PACIFIC("Pacific", Group.FURTHER_AFIELD, -17.50, -150.00, 6000),
    AUSTRALIA_AND_NEW_ZEALAND("Australia and New Zealand", Group.FURTHER_AFIELD, -36.00, 160.00, 4500);

    // How far from its centre a place in the region can be, to be believed: a place looked up by name may be
    // another of the same name somewhere else.
    private static final double UK_AND_IRELAND_REACH_KM = 400;
    private static final double FURTHER_AFIELD_REACH_KM = 3500;

    /**
     * The two halves of the list: a search can take a whole one, or any of the regions in them.
     */
    public enum Group implements DisplayableName {
        UK_AND_IRELAND("UK and Ireland"),
        FURTHER_AFIELD("Further afield");

        private final String displayName;

        Group(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        public List<SailingRegion> getRegions() {
            return Arrays.stream(SailingRegion.values()).filter(region -> region.group == this).toList();
        }
    }

    private final String displayName;
    private final Group group;
    private final Coordinates centre;
    // how far from its centre a place in the region can be, to be believed: a place looked up by name may be
    // another of the same name somewhere else
    private final double reachKm;

    SailingRegion(String displayName, Group group, double latitude, double longitude, double reachKm) {
        this.displayName = displayName;
        this.group = group;
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
        return group == Group.UK_AND_IRELAND;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }


}

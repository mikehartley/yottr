package uk.co.yottr.model;

import java.util.Arrays;
import java.util.List;

/**
 * Where a listing sails from or to, as an area a sailor would search by. The UK and Ireland are split into their
 * sailing areas, since most listings are there; the rest of the world is in broad regions. V13 lists the names too.
 */
public enum SailingRegion implements DisplayableName {

    EAST_COAST("East Coast and Thames", true),
    SOUTH_COAST("Solent and South Coast", true),
    WEST_COUNTRY("West Country", true),
    CHANNEL_ISLANDS("Channel Islands", true),
    WALES_AND_IRISH_SEA("Wales and the Irish Sea", true),
    WEST_SCOTLAND("West of Scotland", true),
    EAST_SCOTLAND_AND_NORTH_EAST("East of Scotland and North East England", true),
    IRELAND("Ireland", true),

    NORTHERN_EUROPE("Northern Europe and the Baltic", false),
    BISCAY_AND_IBERIA("Biscay, Spain and Portugal", false),
    MEDITERRANEAN("Mediterranean", false),
    ATLANTIC_ISLANDS("Canaries, Madeira and the Azores", false),
    CARIBBEAN("Caribbean", false),
    NORTH_AMERICA("North America", false),
    CENTRAL_AND_SOUTH_AMERICA("Central and South America", false),
    AFRICA_AND_INDIAN_OCEAN("Africa and the Indian Ocean", false),
    ASIA("Asia", false),
    PACIFIC("Pacific", false),
    AUSTRALIA_AND_NEW_ZEALAND("Australia and New Zealand", false);

    private final String displayName;
    private final boolean ukAndIreland;

    SailingRegion(String displayName, boolean ukAndIreland) {
        this.displayName = displayName;
        this.ukAndIreland = ukAndIreland;
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

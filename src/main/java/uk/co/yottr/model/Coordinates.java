package uk.co.yottr.model;

/**
 * A point on the earth, in degrees.
 */
public record Coordinates(double latitude, double longitude) {

    private static final double EARTH_RADIUS_KM = 6371;

    /**
     * The distance to another point along the earth's surface (the haversine formula).
     */
    public double kilometresTo(Coordinates other) {
        final double latitudeChange = Math.toRadians(other.latitude - latitude);
        final double longitudeChange = Math.toRadians(other.longitude - longitude);
        final double a = Math.pow(Math.sin(latitudeChange / 2), 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(other.latitude)) * Math.pow(Math.sin(longitudeChange / 2), 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(a));
    }
}

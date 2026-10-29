package uk.co.yottr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import uk.co.yottr.model.Coordinates;
import uk.co.yottr.model.SailingRegion;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

/**
 * Finds where a place a listing names is, with OpenStreetMap's Nominatim, so the map can show it there rather than
 * in the middle of its region. Nominatim asks for no more than a request a second and a User-Agent that says who's
 * asking; a listing is looked up only when its place changes, which is well within that.
 *
 * A place is believed only if it's within reach of its region, since a name may belong to somewhere else as well.
 * Anything that goes wrong means the place isn't found, and the listing shows at its region's centre.
 */
@Component
public class PlaceLocator {

    private static final Logger LOG = LoggerFactory.getLogger(PlaceLocator.class);

    static final String UK_AND_IRELAND_COUNTRY_CODES = "gb,ie,gg,je,im";

    private final RestClient restClient;
    private final boolean enabled;

    @Autowired
    public PlaceLocator(@Value("${yottr.geocoding.enabled:true}") boolean enabled,
                        @Value("${yottr.geocoding.url:https://nominatim.openstreetmap.org}") String url) {
        // short timeouts, since a listing is being saved meanwhile
        this(RestClient.builder().requestFactory(timeouts()), enabled, url);
    }

    PlaceLocator(RestClient.Builder builder, boolean enabled, String url) {
        this.enabled = enabled;
        this.restClient = builder.baseUrl(url)
                .defaultHeader(HttpHeaders.USER_AGENT, "Yottr/1.0 (boats looking for crew)")
                .build();
    }

    private static SimpleClientHttpRequestFactory timeouts() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        return requestFactory;
    }

    /**
     * Where the place is, if it can be found in its region.
     */
    public Optional<Coordinates> locate(String place, SailingRegion region) {
        if (!enabled || place == null || place.isBlank() || region == null) {
            return Optional.empty();
        }
        try {
            final Found[] found = restClient.get()
                    .uri(uri -> {
                        uri.path("/search").queryParam("q", place).queryParam("format", "jsonv2").queryParam("limit", 5);
                        if (region.isUkAndIreland()) {
                            uri.queryParam("countrycodes", UK_AND_IRELAND_COUNTRY_CODES);
                        }
                        return uri.build();
                    })
                    .retrieve()
                    .body(Found[].class);
            final Optional<Coordinates> located = found == null ? Optional.empty() : Arrays.stream(found)
                    .map(Found::coordinates)
                    .flatMap(Optional::stream)
                    .filter(region::reaches)
                    .findFirst();
            LOG.info("place {} in {} {}", place, region, located.map(c -> "found at " + c).orElse("not found"));
            return located;
        } catch (RestClientException e) {
            LOG.warn("couldn't look up place {} in {}: {}", place, region, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * A place as Nominatim gives it, with its coordinates as strings.
     */
    record Found(String lat, String lon) {
        Optional<Coordinates> coordinates() {
            try {
                return Optional.of(new Coordinates(Double.parseDouble(lat), Double.parseDouble(lon)));
            } catch (NullPointerException | NumberFormatException e) {
                return Optional.empty();
            }
        }
    }
}

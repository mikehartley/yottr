package uk.co.yottr.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import uk.co.yottr.model.Coordinates;
import uk.co.yottr.model.SailingRegion;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.startsWith;

public class PlaceLocatorTest {

    private static final String URL = "https://nominatim.example.test";

    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

    @Test
    public void aPlaceInTheUkIsLookedForThereAndFound() {
        server.expect(requestTo(startsWith(URL + "/search")))
                .andExpect(queryParam("q", "Hamble"))
                .andExpect(queryParam("countrycodes", PlaceLocator.UK_AND_IRELAND_COUNTRY_CODES))
                .andExpect(header("User-Agent", startsWith("Yottr")))
                .andRespond(withSuccess("[{\"lat\": \"50.8580\", \"lon\": \"-1.3120\", \"name\": \"Hamble-le-Rice\"}]", MediaType.APPLICATION_JSON));

        assertEquals(Optional.of(new Coordinates(50.858, -1.312)), locator(true).locate("Hamble", SailingRegion.SOUTH_COAST));
        server.verify();
    }

    @Test
    public void aPlaceOfTheSameNameOutsideTheRegionIsPassedOver() {
        // Falmouth, Massachusetts first, then Falmouth, Jamaica: neither is in the West Country
        server.expect(requestTo(startsWith(URL + "/search")))
                .andRespond(withSuccess("[{\"lat\": \"41.55\", \"lon\": \"-70.61\"}, {\"lat\": \"18.49\", \"lon\": \"-77.65\"}]", MediaType.APPLICATION_JSON));

        assertEquals(Optional.empty(), locator(true).locate("Falmouth", SailingRegion.WEST_COUNTRY));
    }

    @Test
    public void theFirstPlaceInTheRegionIsTaken() {
        server.expect(requestTo(startsWith(URL + "/search")))
                .andRespond(withSuccess("[{\"lat\": \"41.55\", \"lon\": \"-70.61\"}, {\"lat\": \"18.49\", \"lon\": \"-77.65\"}]", MediaType.APPLICATION_JSON));

        assertEquals(Optional.of(new Coordinates(18.49, -77.65)), locator(true).locate("Falmouth", SailingRegion.CARIBBEAN));
    }

    @Test
    public void aPlaceThatCantBeLookedUpIsNotFound() {
        server.expect(requestTo(startsWith(URL + "/search"))).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertEquals(Optional.empty(), locator(true).locate("Hamble", SailingRegion.SOUTH_COAST));
    }

    @Test
    public void nothingIsLookedUpWhenItIsTurnedOffOrThereIsNoPlace() {
        assertEquals(Optional.empty(), locator(false).locate("Hamble", SailingRegion.SOUTH_COAST));
        assertEquals(Optional.empty(), locator(true).locate(null, SailingRegion.SOUTH_COAST));
        server.verify(); // no requests expected, and none made
    }

    private PlaceLocator locator(boolean enabled) {
        return new PlaceLocator(builder, enabled, URL);
    }
}

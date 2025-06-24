package clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigReader;
import config.ObjectMapperFactory;
import models.requests.GenericRequest;
import models.responses.FavouritesChampsResponse;
import models.responses.HighlightsResponse;
import models.responses.StaticTranslationsResponse;
import models.responses.UpcomingResponse;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.create();

    public static StaticTranslationsResponse getStaticTranslations(GenericRequest request) {
        return get("/api/Translation/StaticTranslations", request, StaticTranslationsResponse.class);
    }

    public static UpcomingResponse getUpcoming(GenericRequest request) {
        return get("/api/Sportsbook/GetUpcoming", request, UpcomingResponse.class);
    }

    public static HighlightsResponse getHighlights(GenericRequest request) {
        return get("/api/Sportsbook/GetHighlights", request, HighlightsResponse.class);
    }

    public static FavouritesChampsResponse getFavouritesChamps(GenericRequest request) {
        return get("/api/Sportsbook/GetFavouritesChamps", request, FavouritesChampsResponse.class);
    }

    private static <T> T get(String endpoint, GenericRequest request, Class<T> responseClass) {
        return given()
                .baseUri(ConfigReader.getProperty("front_page"))
                .queryParams(objectMapper.convertValue(request, Map.class))
                .when()
                .get(endpoint)
                .then()
                .extract()
                .as(responseClass);
    }
}

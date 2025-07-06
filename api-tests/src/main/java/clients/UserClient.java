package clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigReader;
import config.ObjectMapperFactory;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.create();

    public static com.altenar.sb2.frontend.model.StringStringDictionaryApiResult getStaticTranslations(GenericRequest request) {
        return get("/api/Translation/StaticTranslations", request, com.altenar.sb2.frontend.model.StringStringDictionaryApiResult.class);
    }

    public static com.altenar.sb2.frontend.model.EventResultOutApiResult getUpcoming(GenericRequest request) {
        return get("/api/Sportsbook/GetUpcoming", request, com.altenar.sb2.frontend.model.EventResultOutApiResult.class);
    }

    public static com.altenar.sb2.frontend.model.EventResultOutApiResult getHighlights(GenericRequest request) {
        return get("/api/Sportsbook/GetHighlights", request, com.altenar.sb2.frontend.model.EventResultOutApiResult.class);
    }

    public static com.altenar.sb2.frontend.model.FavChampOutIEnumerableApiResult getFavouritesChamps(GenericRequest request) {
        return get("/api/Sportsbook/GetFavouritesChamps", request, com.altenar.sb2.frontend.model.FavChampOutIEnumerableApiResult.class);
    }

    private static <T> T get(String endpoint, GenericRequest request, Class<T> responseClass) {
        RestAssured.defaultParser = Parser.JSON;
        return given()
                .baseUri(ConfigReader.getProperty("front_page"))
                .queryParams(objectMapper.convertValue(request, Map.class))
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract()
                .as(responseClass);
    }
}

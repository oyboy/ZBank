package clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigReader;
import config.ObjectMapperFactory;
import models.entities.Event;
import models.entities.HighlightEvent;
import models.entities.Sport;
import models.requests.ChampionshipsRequest;
import models.requests.UpdateConfigRequest;
import models.responses.ChampionshipResponse;
import models.responses.ConfigSettingsResponse;
import models.responses.UpdateConfigResponse;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AdminClient {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.create();

    public static List<Sport> getRawSportsFromChampionships(Map<String, String> cookie) {
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().atZone(ZoneId.systemDefault()).plusDays(1).toInstant();
        ChampionshipsRequest request = ChampionshipsRequest.builder()
                .sportIds(new int[]{74, 76})
                .dateFrom(startDate)
                .dateTo(endDate)
                .build();

        return getChampionships(request, cookie).getSport();
    }
    public static List<Sport> getPreparedSports(Map<String, String> cookie) {
        List<Sport> sports = getRawSportsFromChampionships(cookie);
        for (int i = 0; i < sports.size(); i++) {
            Sport sport = sports.get(i);
            sport.setOrder(i + 1);
            sport.setEnabled(true);
        }
        return sports;
    }

    public static ChampionshipResponse getChampionships(ChampionshipsRequest request, Map<String, String> cookie){
        return given()
                .log().all()
                .baseUri(ConfigReader.getProperty("admin_page"))
                .header("Content-Type", "application/json")
                .cookies(cookie)
                .when()
                .body(objectMapper.convertValue(request, Map.class))
                .post("/Api/HighlightsManager/GetChampionships")
                .then()
                .log().all()
                .extract()
                .response()
                .as(ChampionshipResponse.class);
    }

    public static ConfigSettingsResponse getConfigSettings(Map<String, String> cookie){
        return given()
                .cookies(cookie)
                .baseUri(ConfigReader.getProperty("admin_page"))
                .when()
                .queryParam("configId", "126")
                .get("/Api/HighlightsManager/GetConfigSettings")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(ConfigSettingsResponse.class);
    }

    public static List<HighlightEvent> getHighlightEvents(Map<String, String> cookie) {
        return getConfigSettings(cookie).getData().getEvents().stream()
                .map(AdminClient::mapToHighlightEvent)
                .collect(Collectors.toList());
    }
    private static HighlightEvent mapToHighlightEvent(Event event) {
        HighlightEvent h = new HighlightEvent();
        h.setEventId(event.getEventId());
        h.setOrder(event.getOrder() != null ? event.getOrder() : 0);
        h.setPromo(Boolean.TRUE.equals(event.getIsPromo()));
        h.setSafe(Boolean.TRUE.equals(event.getIsSafe()));
        return h;
    }

    public static UpdateConfigResponse updateConfig(UpdateConfigRequest request, Map<String, String> cookie){
        return given()
                .log().all()
                .cookies(cookie)
                .baseUri(ConfigReader.getProperty("admin_page"))
                .contentType("application/json")
                .body(objectMapper.convertValue(request, Map.class))
                .when()
                .post("/Api/HighlightsManager/UpdateConfig")
                .then()
                .statusCode(200)
                .body("Success", equalTo(true))
                .extract()
                .response()
                .as(UpdateConfigResponse.class);
    }
}

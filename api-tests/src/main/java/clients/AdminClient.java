package clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigReader;
import config.ObjectMapperFactory;
import models.request.ChampionshipsRequest;
import models.response.config_settings.ConfigSettingsResponse;
import models.response.config_settings.GetChampionshipResponse;
import models.response.config_settings.HighlightEvent;
import models.response.config_settings.Sport;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class AdminClient {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.create();

    public static List<Sport> getSportsFromChampionships(Map<String, String> cookie){
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().atZone(ZoneId.systemDefault()).plusDays(1).toInstant();

        ChampionshipsRequest request = ChampionshipsRequest.builder()
                .sportIds(new int[]{74, 76})
                .dateFrom(startDate)
                .dateTo(endDate)
                .build();

        GetChampionshipResponse response = given()
                .log().all()
                .baseUri(ConfigReader.getProperty("admin_page"))
                .header("Content-Type", "application/json")
                .cookies(cookie)
                .when()
                .body(objectMapper.convertValue(request, Map.class))
                .post("/Api/HighlightsManager/GetChampionships")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(GetChampionshipResponse.class);

        List<Sport> sports = response.getSport();
        for (int i = 0; i < sports.size(); i++) {
            Sport sport = sports.get(i);
            sport.setOrder(i + 1);
            sport.setEnabled(true);
        }

        return sports;
    }
    public static ConfigSettingsResponse getConfigSettings(Map<String, String> cookie){
        ConfigSettingsResponse configSettings = given()
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
        return configSettings;
    }
    public static List<HighlightEvent> getHighlightEvents(Map<String, String> cookie){
        List<HighlightEvent> highlights = getConfigSettings(cookie).getData().getEvents().stream().map(event -> {
            HighlightEvent h = new HighlightEvent();
            h.setEventId(event.getEventId());
            h.setOrder(event.getOrder() != null ? event.getOrder() : 0);
            h.setPromo(Boolean.TRUE.equals(event.getIsPromo()));
            h.setSafe(Boolean.TRUE.equals(event.getIsSafe()));
            return h;
        }).collect(Collectors.toList());
        return highlights;
    }
}

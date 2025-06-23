package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.example.api.config.ConfigReader;
import org.example.api.config.CookieExtractor;
import org.example.api.model.request.ChampionshipsRequest;
import org.example.api.model.request.UpdateConfigRequest;
import org.example.api.model.response.config_settings.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class AdminPageTest {
    private static Map<String, String> cookie;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUp() {
        cookie = CookieExtractor.getCookie();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private List<Sport> getSportsFromChampionships(){
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
        return response.getSport();
    }
    private ConfigSettingsResponse getConfigSettings(){
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
    private List<HighlightEvent> getHighlightEvents(){
        List<HighlightEvent> highlights = getConfigSettings().getData().getEvents().stream().map(event -> {
            HighlightEvent h = new HighlightEvent();
            h.setEventId(event.getEventId());
            h.setOrder(event.getOrder() != null ? event.getOrder() : 0);
            h.setPromo(Boolean.TRUE.equals(event.getIsPromo()));
            h.setSafe(Boolean.TRUE.equals(event.getIsSafe()));
            return h;
        }).collect(Collectors.toList());
        return highlights;
    }

    @Test
    public void testRemoveLanguage() throws Exception {
        ConfigSettingsResponse configSettings = getConfigSettings();
        List<Sport> sportsFromApi = getSportsFromChampionships();

        UpdateConfigRequest request = new UpdateConfigRequest();
        request.setConfigId(126);
        request.setHighlightsEvents(getHighlightEvents());
        request.setLanguageTabs(configSettings.getData().getLanguageTabs());
        request.setSports(sportsFromApi);

        String body = new ObjectMapper().writeValueAsString(request);

        Response updateConfigResponse = given()
                .log().all()
                .cookies(cookie)
                .baseUri(ConfigReader.getProperty("admin_page"))
                .contentType("application/json")
                .body(body)
                .when()
                .post("/Api/HighlightsManager/UpdateConfig")
                .then()
                .statusCode(200)
                .body("Success", equalTo(true))
                .extract()
                .response();
    }


    @Test
    public void getChampionshipsWithCorrectDate() {
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().atZone(ZoneId.systemDefault()).plusDays(1).toInstant();

        ChampionshipsRequest request = ChampionshipsRequest.builder()
                .sportIds(new int[]{74, 76})
                .dateFrom(startDate)
                .dateTo(endDate)
                .build();

        Response response = given()
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
                .response();

        JsonPath jsonPath = response.jsonPath();
        boolean success = jsonPath.getBoolean("Success");
        assertTrue(success, "Запрос должен вернуть список событий при корректном диапазоне дат");
    }
    @Test
    public void getChampionshipsWithIncorrectDate() {
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().atZone(ZoneId.systemDefault()).minusDays(1).toInstant();

        ChampionshipsRequest request = ChampionshipsRequest.builder()
                .sportIds(new int[]{74, 76})
                .dateFrom(startDate)
                .dateTo(endDate)
                .build();

        Response response = given()
                .log().all()
                .baseUri(ConfigReader.getProperty("admin_page"))
                .header("Content-Type", "application/json")
                .cookies(cookie)
                .when()
                .body(objectMapper.convertValue(request, Map.class))
                .post("/Api/HighlightsManager/GetChampionships")
                .then()
                .statusCode(400)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();
        boolean success = jsonPath.getBoolean("Success");
        assertFalse(success, "Запрос должен вернуть ошибку при некорректном диапазоне дат");
    }
}

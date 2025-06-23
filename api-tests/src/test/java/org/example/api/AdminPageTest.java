package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.example.api.config.ConfigReader;
import org.example.api.config.CookieExtractor;
import org.example.api.model.request.ChampionshipsRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

import static io.restassured.RestAssured.given;
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

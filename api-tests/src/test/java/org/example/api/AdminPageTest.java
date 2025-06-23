package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.example.api.config.ConfigReader;
import org.example.api.config.CookieExtractor;
import org.example.api.model.request.GenericRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
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

    private GenericRequest.GenericRequestBuilder baseRequestBuilder() {
        return GenericRequest.builder()
                .timezoneOffset(420)
                .langId(39)
                .skinName("betsonic")
                .configId(1)
                .culture("fr-fr")
                .countryCode("RU")
                .deviceType("Desktop")
                .numformat("en")
                .integration("skintest");
    }

    private Response sendRequest(String endpoint, GenericRequest request) {
        return given()
                .log().all()
                .baseUri(ConfigReader.getProperty("front_page"))
                .cookies(cookie)
                .queryParams(objectMapper.convertValue(request, Map.class))
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Test
    public void getLanguagesList() {
        GenericRequest request = baseRequestBuilder()
                .timezoneOffset(-180)
                .langId(8)
                .countryCode("")
                .build();

        Response response = sendRequest("/api/Translation/StaticTranslations", request);

        JsonPath jsonPath = response.jsonPath();
        Map<String, String> res = jsonPath.get("Result");

        assertFalse(res.isEmpty(), "Translation list should not be empty");
        assertEquals("Aujourd’hui", res.get("Today"), "Translation for 'Today' is incorrect");
        assertEquals("Mise", res.get("Stake"), "Translation for 'Stake' is incorrect");
        assertEquals("Paris introuvables", res.get("No bets found"), "Translation for 'No bets found' is incorrect");
    }

    @Test
    public void checkViewAllEventsButtonIsNotDisplayed() {
        GenericRequest request = baseRequestBuilder()
                .sportId(74)
                .showAllEvents(false)
                .count(10)
                .hasStreaming(false)
                .build();

        Response response = sendRequest("/api/Sportsbook/GetUpcoming", request);

        JsonPath jsonPath = response.jsonPath();
        boolean isDisplayed = jsonPath.getBoolean("Result.ShowMoreEvents");
        int eventsCount = jsonPath.getInt("Result.EventsCount");

        assertThat("Количество событий должно быть меньше 10", eventsCount < 10);
        assertFalse(isDisplayed, "Кнопка показа всех событий не должна отображаться");
    }

    @Test
    public void checkViewAllEventsButtonIsDisplayed() {
        GenericRequest request = baseRequestBuilder()
                .sportId(76)
                .showAllEvents(false)
                .count(10)
                .hasStreaming(false)
                .build();

        Response response = sendRequest("/api/Sportsbook/GetUpcoming", request);

        JsonPath jsonPath = response.jsonPath();
        boolean isDisplayed = jsonPath.getBoolean("Result.ShowMoreEvents");
        int eventsCount = jsonPath.getInt("Result.EventsCount");

        assertThat("Количество событий должно быть больше или равно 10", eventsCount, greaterThanOrEqualTo(10));
        assertTrue(isDisplayed, "Кнопка показа всех событий должна отображаться");
    }

    @Test
    public void callingGetHighlightsReturnsNonEmptyEventList() {
        GenericRequest request = baseRequestBuilder()
                .sportId(76)
                .showAllEvents(false)
                .count(10)
                .hasStreaming(false)
                .build();

        Response response = sendRequest("/api/Sportsbook/GetHighlights", request);

        JsonPath jsonPath = response.jsonPath();
        List<Object> events = jsonPath.getList("Result.Items");
        assertFalse(events.isEmpty(), "Массив не должен быть пуст, если метод вызывается");
    }

    @Test
    public void getFavouriteChampsWithCorrectDate() {
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().atZone(ZoneId.systemDefault()).plusMonths(1).toInstant();

        GenericRequest request = baseRequestBuilder()
                .period("periodmonth")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Response response = sendRequest("/api/Sportsbook/GetFavouritesChamps", request);

        JsonPath jsonPath = response.jsonPath();
        List<Object> events = jsonPath.getList("Result");
        assertFalse(events.isEmpty(), "Список событий не должен пуст при корректной дате");
    }

    @Test
    public void getFavouriteChampsWithUncorrectDate() {
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().atZone(ZoneId.systemDefault()).minusMonths(1).toInstant();

        GenericRequest request = baseRequestBuilder()
                .period("periodmonth")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Response response = sendRequest("/api/Sportsbook/GetFavouritesChamps", request);

        JsonPath jsonPath = response.jsonPath();
        List<Object> events = jsonPath.getList("Result");
        assertTrue(events.isEmpty(), "Список событий должен быть пуст при некорректной дате");
    }
}
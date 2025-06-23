package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.example.api.config.ConfigReader;
import org.example.api.config.CookieExtractor;
import org.example.api.model.request.GenericRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class AdminPageTest {
    private static Map<String, String> cookie;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUp() {
        cookie = CookieExtractor.getCookie();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void getLanguagesList(){
        GenericRequest request = GenericRequest.builder()
                .timezoneOffset(-180)
                .langId(8)
                .skinName("betsonic")
                .configId(1)
                .culture("fr-fr")
                .countryCode("")
                .deviceType("Desktop")
                .numformat("en")
                .integration("skintest")
                .build();

        Response response = given()
                .log().all()
                .baseUri(ConfigReader.getProperty("front_page"))
                .cookies(cookie)
                .queryParams(objectMapper.convertValue(request, Map.class))
                .when()
                .get("/api/Translation/StaticTranslations")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();
        Map<String, String> res = jsonPath.get("Result");

        assertFalse(res.isEmpty(), "Translation list should not be empty");
        assertEquals("Aujourd’hui", res.get("Today"), "Translation for 'Today' is incorrect");
        assertEquals("Mise", res.get("Stake"), "Translation for 'Stake' is incorrect");
        assertEquals("Paris introuvables", res.get("No bets found"), "Translation for 'No bets found' is incorrect");
    }

    @Test
    public void checkViewAllEventsButtonIsNotDisplayed(){
        GenericRequest request = GenericRequest.builder()
                .timezoneOffset(420)
                .langId(39)
                .skinName("betsonic")
                .configId(1)
                .culture("fr-fr")
                .countryCode("RU")
                .deviceType("Desktop")
                .numformat("en")
                .integration("skintest")
                .sportId(74)
                .showAllEvents(false)
                .count(10)
                .hasStreaming(false)
                .build();

        Response response = given()
                .log().all()
                .baseUri(ConfigReader.getProperty("front_page"))
                .cookies(cookie)
                .queryParams(objectMapper.convertValue(request, Map.class))
                .when()
                .get("/api/Sportsbook/GetUpcoming")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();
        boolean isDisplayed = jsonPath.getBoolean("Result.ShowMoreEvents");
        int eventsCount = jsonPath.getInt("Result.EventsCount");

        assertThat("Количество событий должно быть меньше 10", eventsCount < 10);
        assertFalse(isDisplayed, "Кнопка показа всех событий не должна отображаться");
    }
    @Test
    public void checkViewAllEventsButtonIsDisplayed(){
        GenericRequest request = GenericRequest.builder()
                .timezoneOffset(420)
                .langId(39)
                .skinName("betsonic")
                .configId(1)
                .culture("fr-fr")
                .countryCode("RU")
                .deviceType("Desktop")
                .numformat("en")
                .integration("skintest")
                .sportId(76)
                .showAllEvents(false)
                .count(10)
                .hasStreaming(false)
                .build();

        Response response = given()
                .log().all()
                .baseUri(ConfigReader.getProperty("front_page"))
                .cookies(cookie)
                .queryParams(objectMapper.convertValue(request, Map.class))
                .when()
                .get("/api/Sportsbook/GetUpcoming")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();
        boolean isDisplayed = jsonPath.getBoolean("Result.ShowMoreEvents");
        int eventsCount = jsonPath.getInt("Result.EventsCount");

        assertThat("Количество событий должно быть больше или равно 10", eventsCount >= 10);
        assertTrue(isDisplayed, "Кнопка показа всех событий должна отображаться");
    }
}

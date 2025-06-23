package org.example.api;

import io.restassured.response.Response;
import org.example.api.config.ConfigReader;
import org.example.api.config.CookieExtractor;
import org.example.api.model.response.StaticTranslationsResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class AdminPageTest {
    private static Map<String, String> cookie;

    @BeforeAll
    public static void setUp() {
        cookie = CookieExtractor.getCookie();
    }

    @Test
    public void getLanguagesList(){
        String culture = "fr-fr";
        int timezoneOffset = -180;
        int langId = 8;
        String skinName = "betsonic";
        int configId = 1;
        String countryCode = "";
        String deviceType = "Mobile";
        String numformat = "en";
        String integration = "skintest";

        Response response = given()
                .log().all()
                .baseUri(ConfigReader.getProperty("front_page"))
                .cookies(cookie)
                .queryParams("timezoneOffset", timezoneOffset,
                        "langId", langId,
                        "skinName", skinName,
                        "configId", configId,
                        "culture", culture,
                        "countryCode", countryCode,
                        "deviceType", deviceType,
                        "numformat", numformat,
                        "integration", integration)
                .when()
                .get("/api/Translation/StaticTranslations")
                .then()
                .statusCode(200)
                .extract()
                .response();

        StaticTranslationsResponse translationsResponse = response.as(StaticTranslationsResponse.class);

        Map<String, String> res = translationsResponse.getResult();

        assertFalse(res.isEmpty(), "Translation list should not be empty");
        assertEquals("Aujourd’hui", res.get("Today"), "Translation for 'Today' is incorrect");
        assertEquals("Mise", res.get("Stake"), "Translation for 'Stake' is incorrect");
        assertEquals("Paris introuvables", res.get("No bets found"), "Translation for 'No bets found' is incorrect");
    }
}

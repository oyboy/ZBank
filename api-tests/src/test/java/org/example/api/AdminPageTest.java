package org.example.api;

import org.example.api.config.ConfigReader;
import org.example.api.config.CookieExtractor;
import org.example.api.model.response.Language;
import org.example.api.model.response.LanguagesListResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AdminPageTest {
    private static Map<String, String> cookie;

    @BeforeAll
    public static void setUp() {
        cookie = CookieExtractor.getCookie();
    }

    @Test
    public void getLanguagesList(){
        LanguagesListResponse apiResponse = given()
                .baseUri(ConfigReader.getProperty("base_page"))
                .cookies(cookie)
                .when()
                .get("/Api/HighlightsManager/LanguagesList")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(LanguagesListResponse.class);

        List<Language> languages = apiResponse.getData();
        /*languages.forEach(lang ->
                System.out.println(lang.getName() + " (" + lang.getCode() + ")"));*/

        assertFalse(languages.isEmpty(), "Languages list should not be empty");
    }
}

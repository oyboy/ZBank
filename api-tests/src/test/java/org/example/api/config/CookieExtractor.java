package org.example.api.config;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CookieExtractor {
    private static Map<String, String> cookie;

    public static Map<String, String> getCookie() {
        if (cookie == null) {
            RestAssured.baseURI = ConfigReader.getProperty("admin_page");
            Response response = given()
                    .param("username", ConfigReader.getProperty("username"))
                    .param("password", ConfigReader.getProperty("password"))
                    .when()
                    .post("/Account/Login")
                    .then()
                    .extract().response();
            cookie = response.getCookies();
        }
        return cookie;
    }
}

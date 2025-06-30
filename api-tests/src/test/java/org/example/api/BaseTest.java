package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import config.CookieExtractor;
import config.ObjectMapperFactory;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;
public abstract class BaseTest {
    protected static ObjectMapper objectMapper;
    protected static Map<String, String> cookie;

    @BeforeAll
    public static void globalSetUp() {
        objectMapper = ObjectMapperFactory.create();
        cookie = CookieExtractor.getCookie();
        RestAssured.filters(new SwaggerCoverageRestAssured());
    }
}

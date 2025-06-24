package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import models.response.config_settings.ConfigSettingsResponse;
import models.response.config_settings.Sport;
import config.ConfigReader;
import models.request.ChampionshipsRequest;
import models.request.UpdateConfigRequest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static clients.AdminClient.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class AdminPageTest extends BaseTest {
    @Test
    @Description("Проверка обновления конфигурации при удалении языковой вкладки")
    @Story("Admin_Удаление языка")
    @TmsLink("TC_ADMIN_01")
    public void testRemoveLanguage() throws Exception {
        ConfigSettingsResponse configSettings = getConfigSettings(cookie);
        List<Sport> sportsFromApi = getSportsFromChampionships(cookie);

        UpdateConfigRequest request = new UpdateConfigRequest();
        request.setConfigId(126);
        request.setHighlightsEvents(getHighlightEvents(cookie));
        request.setLanguageTabs(configSettings.getData().getLanguageTabs());
        request.setSports(sportsFromApi);

        String body = objectMapper.writeValueAsString(request);

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
    @Description("Проверка получения чемпионатов при корректной дате")
    @Story("Admin_Чемпионаты")
    @TmsLink("TC_ADMIN_02")
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
    @Description("Проверка ошибки при запросе чемпионатов с некорректной датой")
    @Story("Admin_Чемпионаты")
    @TmsLink("TC_ADMIN_03")
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

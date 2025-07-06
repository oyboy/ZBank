package clients;

import com.altenar.sb2.admin.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigReader;
import config.ObjectMapperFactory;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AdminClient {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.create();

    static{
        RestAssured.config = RestAssured.config().objectMapperConfig(
                ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory((cls, charset) -> objectMapper)
        );
    }

    public static List<SportRequestItem> getSportsRequestItem(List<ConfigSport> sports) {
        return sports.stream()
                .map(sport -> objectMapper.convertValue(sport, SportRequestItem.class))
                .collect(Collectors.toList());
    }

    public static List<LanguageTabRequestItem> removeLanguageByName(
            List<ConfigLanguageTab> languageTabs, String languageNameToRemove) {
        return languageTabs.stream()
                .filter(tab -> !languageNameToRemove.equalsIgnoreCase(tab.getName()))
                .map(AdminClient::mapToLanguageTabRequestItem)
                .collect(Collectors.toList());
    }

    private static LanguageTabRequestItem mapToLanguageTabRequestItem(ConfigLanguageTab tab) {
        LanguageTabRequestItem reqItem = new LanguageTabRequestItem();
        reqItem.setLanguageId(tab.getLanguageId());
        reqItem.setHighlightsEvents(mapTopEventsToHighlightEvents(tab.getTopEvents()));
        return reqItem;
    }

    private static List<HighlightsEventRequestItem> mapTopEventsToHighlightEvents(List<ConfigEvent> topEvents) {
        if (topEvents == null) return Collections.emptyList();

        return topEvents.stream()
                .map(AdminClient::mapToHighlightEventRequestItem)
                .collect(Collectors.toList());
    }

    private static HighlightsEventRequestItem mapToHighlightEventRequestItem(ConfigEvent event) {
        HighlightsEventRequestItem hEvent = new HighlightsEventRequestItem();
        hEvent.setOrder(event.getOrder());
        hEvent.setIsPromo(event.getIsPromo());
        hEvent.setIsSafe(event.getIsSafe());
        hEvent.setEventId(event.getEventId());
        return hEvent;
    }


    public static com.altenar.sb2.admin.model.ApiResult updateConfig(com.altenar.sb2.admin.model.UpdateHighlightsConfigRequest request, Map<String, String> cookie){
        return given()
                .log().all()
                .cookies(cookie)
                .baseUri(ConfigReader.getProperty("admin_page"))
                .contentType("application/json")
                .body(request)
                .when()
                .post("/Api/HighlightsManager/UpdateConfig")
                .then()
                .statusCode(200)
                .body("Success", equalTo(true))
                .extract()
                .response()
                .as(com.altenar.sb2.admin.model.ApiResult.class);
    }
    public static com.altenar.sb2.admin.model.HighlightsConfigSettingsApiResult getConfigSettings(Map<String, String> cookie){
        return given()
                .cookies(cookie)
                .baseUri(ConfigReader.getProperty("admin_page"))
                .when()
                .queryParam("configId", "126")
                .get("/Api/HighlightsManager/GetConfigSettings")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(com.altenar.sb2.admin.model.HighlightsConfigSettingsApiResult.class);
    }

    public static com.altenar.sb2.admin.model.EventSportItemListApiResult getChampionships(com.altenar.sb2.admin.model.GetHighlightsChampionshipsRequest request, Map<String, String> cookie){
        return given()
                .log().all()
                .baseUri(ConfigReader.getProperty("admin_page"))
                .header("Content-Type", "application/json")
                .cookies(cookie)
                .when()
                .body(request)
                .post("/Api/HighlightsManager/GetChampionships")
                .then()
                .log().all()
                .extract()
                .response()
                .as(com.altenar.sb2.admin.model.EventSportItemListApiResult.class);
    }

    public static List<com.altenar.sb2.admin.model.HighlightsEventRequestItem> getHighlightEvents(Map<String, String> cookie) {
        return getConfigSettings(cookie).getData().getEvents().stream()
                .map(AdminClient::mapToHighlightEvent)
                .collect(Collectors.toList());
    }
    private static com.altenar.sb2.admin.model.HighlightsEventRequestItem mapToHighlightEvent(com.altenar.sb2.admin.model.ConfigEvent event) {
        com.altenar.sb2.admin.model.HighlightsEventRequestItem h = new com.altenar.sb2.admin.model.HighlightsEventRequestItem();
        h.setEventId(event.getEventId());
        h.setOrder(event.getOrder() != null ? event.getOrder() : 0);
        h.setIsPromo(Boolean.TRUE.equals(event.getIsPromo()));
        h.setIsSafe(Boolean.TRUE.equals(event.getIsSafe()));
        return h;
    }
}

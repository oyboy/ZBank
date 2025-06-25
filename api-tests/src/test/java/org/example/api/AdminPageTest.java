package org.example.api;

import clients.AdminClient;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import models.responses.ChampionshipResponse;
import models.responses.ConfigSettingsResponse;
import models.entities.Sport;
import models.requests.ChampionshipsRequest;
import models.requests.UpdateConfigRequest;
import models.responses.UpdateConfigResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdminPageTest extends BaseTest {
    @Test
    @DisplayName("Проверка удаления языка")
    @Description("Удаляет языковую вкладку из конфигурации при помощи POST-запроса UpdateConfig")
    @Story("Admin_Удаление языка")
    @TmsLink("TC_ADMIN_01")
    public void testRemoveLanguage() {
        ConfigSettingsResponse configSettings = AdminClient.getConfigSettings(cookie);
        List<Sport> sportsFromApi = AdminClient.getPreparedSports(cookie);

        UpdateConfigRequest request = new UpdateConfigRequest();
        request.setConfigId(126);
        request.setHighlightsEvents(AdminClient.getHighlightEvents(cookie));
        request.setLanguageTabs(configSettings.getData().getLanguageTabs());
        request.setSports(sportsFromApi);

        UpdateConfigResponse updateConfigResponse = AdminClient.updateConfig(request, cookie);
        assertTrue(updateConfigResponse.getSuccess(), "Конфигурация должна быть успешно обновлена");
    }


    @Test
    @DisplayName("Проверка получения чемпионатов при корректной дате")
    @Description("Отправляет POST-запрос на /GetChampionships с валидным диапазоном дат и проверяет, что возвращаемое поле Success = true")
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

        ChampionshipResponse response = AdminClient.getChampionships(request, cookie);
        assertTrue(response.getSuccess(), "Запрос должен вернуть список событий при корректном диапазоне дат");
    }

    @Test
    @DisplayName("Проверка ошибки при запросе чемпионатов с некорректной датой")
    @Description("При передаче некорректных дат запрос возвращает ошибку")
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

        ChampionshipResponse response = AdminClient.getChampionships(request, cookie);
        assertFalse(response.getSuccess(), "Запрос должен вернуть ошибку при некорректном диапазоне дат");
    }
}
